
                                E X C A L I B U R

  What is it?
  -----------

  Excalibur is a collection of often-needed reusable components. It includes tools for
  threading, pooling, datasources, command-line interface (CLI) option parsing
  and more.

  Where is it?
  ------------

  http://jakarta.apache.org/avalon

  Directory Layout
  ----------------

  Excalibur is comprised of a number reusable components. These components are
  what you see in most of the subdirectories.

  The all/ directory contains a core set of packages that cannot be
  componentized further (excalibur-core.jar). It is a legacy of a time before
  Excalibur was componentized, and as such, it's build system has much
  functionality not yet present elsewhere. In time, functionality in
  all/build.xml will migrate to ./build.xml.

  The avalon-excalibur.jar released in version 4.1 would now consist of the
  following jars:

   all/build/lib/avalon-excalibur.jar
   datasource/build/lib/excalibur-datasource-1.0.jar
   cli/build/lib/excalibur-cli-1.0.jar
   collections/build/lib/excalibur-collections-1.0.jar
   concurrent/build/lib/excalibur-concurrent-1.0.jar
   component/build/lib/excalibur-component-1.0.jar
   i18n/build/lib/excalibur-i18n-1.0.jar
   io/build/lib/excalibur-io-1.0.jar
   naming/build/lib/excalibur-naming-1.0.jar
   logger/build/lib/excalibur-logger-1.0.jar
   pool/build/lib/excalibur-pool-1.0.jar
   testcase/build/lib/excalibur-testcase-1.0.jar
   util/build/lib/excalibur-util-1.0.jar

   in addition, versions post-4.1 have a dependency on:

   instrument/build/lib/excalibur-instrument-0.1.jar

   Typing 'ant' in the root directory will build an avalon-excalibur.jar
   containing these components.


  Component dependencies
  ----------------------

  Each component may have dependencies on external jars, and on other
  components. These dependencies are listed in the ant.properties.sample files
  in the root of each component. You may need to customize these properties for
  your local environment. See the text in ant.properties.sample for more
  information.

  In general, it is safe to type 'ant', and if an external dependency is
  missing, you will be prompted on what to add to ant.properties. If instead
  you get compiler errors, please let us know: it is a bug in the build system.


  General Building Requirements
  -----------------------------

  -JDK1.2 or above
  -Jakarta Ant 1.4.1 or higher, with optional.jar and junit.jar in $ANT_HOME/lib
  -To build from CVS you must set JAVA_HOME to the jdk dir (eg:/usr/bin/jdk1.2 or
   c:\jdk1.3)
  -To build Informix drivers you need version 2.2 of the JDBC driver
  -To build J2EE client for connections you need JNDI and the JDBC extensions


  Getting Started
  ---------------

  Generally, one would build individual components, not the whole of Excalibur.
  This can be done by typing 'ant' in any component's directory.
  However, if you type 'ant' in the project root, a file 'avalon-excalibur.jar'
  will be built, containing a set of components historically regarded as
  comprising 'Excalibur'.


  Problems?
  ---------

  If you have problems, questions or feedback about Excalibur, don't hesitate
  to contact the dev team at avalon-dev@jakarta.apache.org. Feedback is always
  welcome!


The Avalon Excalibur Team
