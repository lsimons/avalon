
                                E X C A L I B U R

  What is it?
  -----------

  Excalibur is a collection of often-needed reusable components. It includes tools for
  threading, pooling, datasources, CLI option parsing and more.

  Where is it?
  ------------

  http://jakarta.apache.org/avalon

  Directory Layout
  ----------------

  Recently, Excalibur was broken up from a single monolithic jar, to a number
  of reusable components. These components are what you see in all the
  subdirectories.

  The avalon-excalibur.jar released in version 4.1 would now consist of the
  following jars:

   all/build/lib/avalon-excalibur.jar
   cli/build/lib/excalibur-cli-1.0.jar
   collections/build/lib/excalibur-collections-1.0.jar
   concurrent/build/lib/excalibur-concurrent-1.0.jar

   in addition, versions post-4.1 have a dependency on:

   instrument/build/lib/excalibur-instrument-0.1.jar


  Component dependencies
  ----------------------

  Each component may have dependencies on external jars, and on other
  components. These dependencies are listed in the ant.properties.sample files
  in the root of each component. You may need to customize these properties for
  your local environment. See the text in ant.properties.sample for more
  information.

  [Update 2001-03-29]

    To date, there remains a core set of packages which are too interrelated to
    break up further. These reside in the all/ directory. This core set should
    be regarded as a component in it's own right. Like any other component, it
    has dependencies on other Excalibur components, recorded in
    ant.properties.sample.

    The documentation system for all components is still completely within the
    all/ directory. Type 'ant html-docs' in all/ to build the HTML
    documentation. This will be fixed in the near future.


  General Building Requirements
  -----------------------------

  -JDK1.2 or above
  -To build from CVS you must set JAVA_HOME to the jdk dir (eg:/usr/bin/jdk1.2 or
   c:\jdk1.3)
  -To build Informix drivers you need version 2.2 of the JDBC driver
  -To build J2EE client for connections you need JNDI and the JDBC extensions


  Getting Started
  ---------------

  Generally, one would build individual components, not the whole of Excalibur.
  However, as noted above, there is a core set of packages, living in all/,
  that generally constitute what people call 'Excalibur'. To build this, you'll
  first need to build it's dependencies (listed in all/ant.properties.sample).
  Then running Ant in all/ should generate the core jar in build/lib/


  Problems?
  ---------

  If you have problems, questions or feedback about Excalibur, don't hesitate
  to contact the dev team at avalon-dev@jakarta.apache.org. Feedback is always
  welcome!


The Avalon Excalibur Team
