
Historically the cornerstone project has been built as a single jar file.  This is undesirable because it contains many applications meeting different concerns.  As part of the process of rationalizing the James build, a number of specific compoenent builds have been seperated out. 

These builds concern the following:

  cornerstone-connection-1.0.jar
  cornerstone-datasources-1.0.jar
  cornerstone-masterstore-1.0.jar
  cornerstone-sheduler-1.0.jar
  cornerstone-sockets-1.0.jar
  cornerstone-threads-1.0.jar

To build these components you need to invoke the following ant command:

  $ ant -buildfile cornerstone.xml

The build process will place the above jars into the build/lib directory.

