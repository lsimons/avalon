
Avalon Components
-----------------

The components repository is based on the restructuring of the Cornerstone 
project.  The restructuring incorporates the seperation of each component 
into its own directory including isolation of of api relative to 
implemetation.

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

The default target will build, jar and install the complete component 
suite into your local maven repository.

  $ cd avalon-components
  $ maven


Other convinience goals avalible within the root avalon-components
directory include:

  do-install
  do-site
  do-clean

Site documetation generated hy the do-site goal will be placed in 
the avalon-components/site/target/docs directory.

Each project can be build indepedendently using classic Maven 
goals.  For example:

  $ cd avalon-components/threads-api
  $ maven jar:install-snapshot

For more information please consult the project documentation
included under the avalon-components/site directory.