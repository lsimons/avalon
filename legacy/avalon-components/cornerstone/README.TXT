
Avalon Components
-----------------

The components repository is based on the restructuring of the Cornerstone 
project.  The restructuring incorporates the seperation of each component 
into its own directory including isolation of of api relative to 
implemetation.

Components subprojects include:

   connection
   datasources
   scheduler
   sockets
   store
   threads

The default target will build, jar and install the complete component 
suite into your local maven repository.

  $ cd avalon-components/cornerstone
  $ maven

Site documetation generated hy the avalon:site goal will be placed in 
the avalon-components/cornerstone/target/docs directory.

Each project can be build indepedendently using classic Maven 
goals.  For example:

  $ cd avalon-components/cornerstone/threads/api
  $ maven jar:install

For more information please consult the project documentation
included under the avalon-components/cornerstone/site directory.