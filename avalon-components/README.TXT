
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

The entire suite can be built as follows:

  $ cd avalon-components
  $ maven

The default target will build, jar and install the component into 
your local maven repository followind by the generation of the 
project documetation under the following directory:

  avalon-components/site/target/docs

Other convinience goals avalible within the root avalon-components
directory include:

  project-install
  project-site
  project-clean

Furthermore, each project can be build indepedendently using
classic Maven goals.  For example:

  $ cd avalon-components/threads-api
  $ maven jar:install

For more information please consult the project documentation
included under the avalon-components/site directory.