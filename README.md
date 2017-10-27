# Rate matcher

This application starts a jetty webserver on port 8080 that listens to requests to match rates for on the /rest/rate path. This request takes two ISO formatted datetime strings that are the start and end of the searched for range.

The specs and documentation for this can be found in the api_docs directory under the project root. The specs can also be retrieved from the running server at /rest/swagger.json or /rest/swagger.yaml.

A set of metrics for the application can be found at /metrics.

It's a gradle project and can be run with the "gradle run" (or gradlew run, if you don't have gradle installed) command. It will also generate a fat jar in the build\libs directory if you run "gradle fatJar".

The rates are loaded from a default data.json file contained in the resources section. If you want to use your own file with definitions, adding the path to that file as a command line argument when starting the server will instead load from that file.

