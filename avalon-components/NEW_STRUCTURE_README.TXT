
The cornerstone repository has been restructured to support the seperation
of components, and for each component - the seperation of api relative to 
implemetation builds.

Components subprojects include:

   connection-api
   connection-impl

   datasources-api
   datasources-impl

   scheduler-api
   scheduler-impl

   sockets-api
   sockets-impl

   store-api
   store-impl

   threads-api
   threads-impl
   threads-tutorial

The entire suite can be built as follows:

  $ cd avalon-components
  $ maven

The default target will build, jar and install the component into 
your local maven repository followind by the generation of the 
project documetation under the following directory:

  avalon-components/site/target/docs


