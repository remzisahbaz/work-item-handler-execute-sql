# work-item-handler-execute-sql
jbpm business central custom task wok item handler   : execute sql . 3 input and 1 result

````
`@Wid(widfile= "MyWIHExecuteSqlDefinitions.wid", name="MyWIHExecuteSqlDefinitions",
displayName="WIHExecuteSqlDefinitions",
defaultHandler="mvel: new org.acme.MyWIHExecuteSqlWorkItemHandler(\"dataSourceName\")",
documentation = "${artifactId}/index.html",
category = "${artifactId}",
icon = "MyWIHExecuteSqlDefinitions.png",
parameters = {
@WidParameter(name = "Param1", required = true),
@WidParameter(name = "Param2" ,required = true),
@WidParameter(name = "Param3", required = true)
},
results = {
@WidResult(name = "Result", runtimeType = "java.lang.Object")
},
mavenDepends = {
@WidMavenDepends(group = "${groupId}", artifact = "${artifactId}", version = "${version}")
},
serviceInfo = @WidService(category = "${name}", description = "${description}",
keywords = "database,fetch,sql,execute",
action = @WidAction(title = "Execute SQL statements"),
authinfo = @WidAuth(required = true, params = {"Data source JNDI"},
paramsdescription = {"Data source JNDI"},
referencesite = "https://github.com/remzisahbaz")
)
)`````
