/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/** TO RUN THAT ADD TO THE POM:
 * <dependency>
 *   <groupId>org.codehaus.groovy</groupId>
 *   <artifactId>groovy-all</artifactId>
 *   <version>3.0.0-alpha-1</version>
 * </dependency>
 * <dependency>
 *   <groupId>org.apache.ivy</groupId>
 *   <artifactId>ivy</artifactId>
 *   <version>2.4.0</version>
 * </dependency>
 *
 * IMPORTANT: this is a base for the impl, all is not yet correct (but it saves some time)
 */

@Grab(group = 'org.apache.xbean', module = 'xbean-finder-shaded', version = '4.8')
import org.apache.xbean.finder.AnnotationFinder
import org.apache.xbean.finder.archive.FilteredArchive
import org.apache.xbean.finder.archive.JarArchive
import org.apache.xbean.finder.filter.Filters
@Grab(group = 'org.apache.tomee', module = 'ziplock', version = '7.0.4')
import org.apache.ziplock.JarLocation
@Grab(group = 'org.apache.tomee', module = 'ziplock', version = '7.0.4')
import org.apache.ziplock.JarLocation
import org.eclipse.microprofile.openapi.models.Extensible
import org.eclipse.microprofile.openapi.models.OpenAPI
import org.eclipse.microprofile.openapi.models.PathItem
@Grab(group = 'org.eclipse.microprofile.openapi', module = 'microprofile-openapi-api', version = '1.0.1')
import org.eclipse.microprofile.openapi.models.security.OAuthFlow

import java.lang.reflect.ParameterizedType

def outputFolder = new File(System.getProperty("output", "/tmp/microprofile-openapi-model"))
def dryRun = Boolean.getBoolean("dryRun")
def packageName = 'org.apache.geronimo.microprofile.openapi.impl.model'
def header = """/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ${packageName};
"""

outputFolder.mkdirs()

def appendImports(imports, type) {
    if (type.typeName.startsWith('java.util')) {
        if (ParameterizedType.class.isInstance(type)) {
            def parameterizedType = ParameterizedType.class.cast(type)
            def name = parameterizedType.rawType.typeName
            imports.add(name)
            parameterizedType.actualTypeArguments.each { nested -> appendImports(imports, nested) }
        } else if (Class.class.isInstance(type)) {
            def name = Class.class.cast(type).typeName
            if (!name.contains('$')) {
                imports.add(name)
            }
        } // else unsupported but shouldn't occur
    } else if (type.typeName.startsWith('org.eclipse.microprofile.openapi.models')) {
        def name = Class.class.cast(type).typeName
        if (!name.contains('$')) {
            imports.add(name)
        }
    }
}
def extractType(from) {
    if (from.typeName.startsWith('org.eclipse.microprofile.openapi.models')) {
        return from.simpleName
    } else {
        if (ParameterizedType.class.isInstance(from)) {
            def parameterizedType = ParameterizedType.class.cast(from)
            return "${extractType(parameterizedType.rawType)}<${parameterizedType.actualTypeArguments.collect {p -> extractType(p)}.join(', ')}>"
        }
        return from.typeName.replace('java.lang.', '').replace('java.util.', '')
    }
}

def apiJar = JarLocation.jarLocation(OAuthFlow.class)
def loader = Thread.currentThread().contextClassLoader
def archive = new JarArchive(loader, apiJar.toURI().toURL())
new AnnotationFinder(new FilteredArchive(archive, Filters.packages("org.eclipse.microprofile.openapi.models")))
        .annotatedClassNames.collect { loader.loadClass(it) }.findAll { !it.enum }.each {
    def simpleName = it.name.substring(it.name.lastIndexOf('.') + 1)
    def outputFile = new File(outputFolder, "${simpleName}Impl.java")

    def fields = it.methods
            .findAll { m -> m.name.startsWith("get") && m.parameterCount == 0 && m.name.length() > 3 }
            .findAll { m -> m.declaringClass != Extensible.class || it == Extensible.class }
            .collect { m -> new Tuple2<>(m.name.substring(3).uncapitalize(), m.getGenericReturnType()) }
            .sort { n -> n.first }
    def isMap = Map.class.isAssignableFrom(ParameterizedType.class.isInstance(it) ? ParameterizedType.class.cast(it).rawType : it)
    if (fields.isEmpty() && !isMap) {
        return
    }

    def imports = [it.typeName]

    def extensibleDelegateMethods = Extensible.class != it && Extensible.class.isAssignableFrom(it)
    def builder = new StringBuilder("public class ${simpleName}Impl ")
    def mapAddMethod = new StringBuilder()
    if (isMap) {
        def mapType = it.genericInterfaces.findAll { t -> ParameterizedType.class.isInstance(t) && ParameterizedType.class.cast(t).rawType == Map.class  }.first()
        builder.append("extends LinkedHash${extractType(mapType)} ")
        imports.add(LinkedHashMap.class.name)
        def paramType = ParameterizedType.class.cast(mapType).actualTypeArguments[1]
        imports.add((ParameterizedType.class.isInstance(paramType) ? ParameterizedType.class.cast(paramType).rawType : paramType).name)
        def extractedType = extractType(paramType)
        mapAddMethod.append("    @Override\n" +
                "    public ${simpleName} add${extractedType.replace('API', 'Api').capitalize()}(final String name, final ${ extractedType} item) {\n" +
                "        this.put(name, item);\n" +
                "        return this;\n" +
                "    }")
    }
    builder.append("implements ${simpleName} {\n")
    if (extensibleDelegateMethods) {
        builder.append("    private Extensible _extensible = new ExtensibleImpl();\n");
        imports.add(Extensible.class.name)
        imports.add(Map.class.name)
    }
    fields.each { field ->
        def type = extractType(field.second)
        appendImports(imports, field.second)
        builder.append("    private ${ type} _${field.first};\n") }
    builder.append('\n')
    if (extensibleDelegateMethods) {
        builder.append("    @Override\n" +
                "    public Map<String, Object> getExtensions() {\n" +
                "        return _extensible.getExtensions();\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void addExtension(final String name, final Object value) {\n" +
                "        _extensible.addExtension(name, value);\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void setExtensions(final Map<String, Object> extensions) {\n" +
                "        _extensible.setExtensions(extensions);\n" +
                "    }\n\n");
    }
    if (isMap) {
        builder.append(mapAddMethod).append('\n\n')
    }
    fields.each { field ->
        def type = extractType(field.second)
        builder.append('    @Override\n')
        builder.append("    public ${type} get${field.first.capitalize()}() {\n")
        builder.append("        return _${field.first};\n")
        builder.append('    }\n\n')

        def mutatorsName = field.first.equals('default') ? 'defaultValue' : field.first;

        builder.append('    @Override\n')
        builder.append("    public void set${mutatorsName.capitalize()}(final ${type} _${mutatorsName}) {\n")
        builder.append("        this._${field.first} = _${mutatorsName};\n")
        builder.append('    }\n\n')

        try {
            def paramType = ParameterizedType.class.isInstance(field.second) ? ParameterizedType.class.cast(field.second).rawType : field.second
            it.getMethod(mutatorsName, paramType)

            builder.append('    @Override\n') // todosetDefaultValue
            builder.append("    public ${simpleName} ${mutatorsName}(final ${type} _${mutatorsName}) {\n")
            builder.append("        set${mutatorsName.capitalize()}(_${mutatorsName});\n")
            builder.append("        return this;\n")
            builder.append('    }\n\n')
        } catch (e) {
            // no-op
        }

        if (field.second.typeName.startsWith('java.util.Map')) {
            def methodName
            if (mutatorsName.endsWith('ies')) {
                methodName = mutatorsName.substring(0, mutatorsName.length() - 3) + 'y'
            } else if (mutatorsName.endsWith('s')) {
                methodName = mutatorsName.substring(0, mutatorsName.length() - 1)
            } else {
                methodName = mutatorsName
            }
            methodName = "add${methodName.capitalize()}"

            def parameterizedType = ParameterizedType.class.cast(field.second)
            def lastArg = parameterizedType.actualTypeArguments[parameterizedType.actualTypeArguments.length - 1]
            try {
                def isVoid = it.getMethod(methodName, String.class, lastArg).returnType == void.class

                builder.append('    @Override\n')
                builder.append("    public ${isVoid ? 'void' : simpleName} ${methodName}(final String key, final ${extractType(lastArg)} _${mutatorsName}) {\n")
                builder.append("        (this._${mutatorsName} = this._${mutatorsName} == null ? new LinkedHashMap<>() : this._${mutatorsName}).put(key, _${mutatorsName});\n")
                if (!isVoid) {
                    builder.append("        return this;\n")
                }
                builder.append('    }\n\n')
                imports.add(LinkedHashMap.class.name)
                appendImports(imports, lastArg)
            } catch (e) {
                // no-op
            }
        } else if (field.second.typeName.startsWith('java.util.List')) {
            def lastArg = ParameterizedType.class.cast(field.second).actualTypeArguments[0]
            methodName = "add${lastArg.simpleName.capitalize()}"

            try {
                def isVoid = it.getMethod(methodName, lastArg).returnType == void.class

                builder.append('    @Override\n')
                builder.append("    public ${isVoid ? 'void' : simpleName} ${methodName}(final ${extractType(lastArg)} _${mutatorsName}) {\n")
                builder.append("        (this._${mutatorsName} = this._${mutatorsName} == null ? new ArrayList<>() : this._${mutatorsName}).add(_${mutatorsName});\n")
                if (!isVoid) {
                    builder.append("        return this;\n")
                }
                builder.append('    }\n\n')
                imports.add(ArrayList.class.name)
                appendImports(imports, lastArg)
            } catch (e) {
                // no-op
            }
        }
    }
    if (it == OpenAPI.class) { // no real logic to that method
        imports.add(PathItem.class.name)
        imports.add("${packageName}.PathsImpl")
        builder.append("    @Override\n" +
                "    public OpenAPI path(final String name, final PathItem path) {\n" +
                "        (_paths = this._paths == null ? new PathsImpl() : this._paths).addPathItem(name, path);\n" +
                "        return this;\n" +
                "    }\n\n")
    }
    builder.length = builder.length() - '\n\n'.length();
    builder.append("\n}\n")

    def writer = dryRun ? new StringWriter() : new FileWriter(outputFile)
    try {
        writer.print(header)
        writer.println()
        imports.toSet().sort().each { imp -> writer.print("import ${imp};\n") }
        writer.println()
        writer.print(builder.toString())
    } finally {
        writer.close()
    }
    if (dryRun) {
        println('-----------------------------------')
        println(writer.toString())
        println('-----------------------------------')
    } else {
        println("Generate ${outputFile}")
    }
}
