
The cornerstone repository has been restructured to support the seperation
of different components under independent sub-projects.  Each subproject 
contains an api and impl subproject based on Maven builds.

Components subprojects include:

   connection
   datasources
   scheduler
   sockets
   store
   threads

The following example demonstrates the building of a component project
using the threads package as an example:

  $ cd avalon-components\threads
  $ maven

The default target will build, jar and install the component into 
your local maven repository.
