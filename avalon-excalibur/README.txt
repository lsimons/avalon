
                                E X C A L I B U R

  What is it?
  -----------

  Excalibur is a collection of often-needed reusable components. It includes
  tools for threading, pooling, datasources, command-line interface (CLI)
  option parsing and more.

  It also contains several containers and libraries that aid in building
  containers.

  Where is it?
  ------------

  http://jakarta.apache.org/avalon


  General Building Requirements
  -----------------------------

  - JDK 1.2 or above
  - Jakarta Ant 1.4.1 or higher, with optional.jar and junit.jar in $ANT_HOME/lib
  - To build from CVS you must set JAVA_HOME to the jdk dir (eg:/usr/bin/jdk1.2
    or c:\jdk1.3)
  - To build Informix drivers you need version 2.2 of the JDBC driver
  - To build J2EE client for connections you need JNDI and the JDBC extensions


  Getting Started
  ---------------

  Excalibur is comprised of a number of subprojects. These subprojects are what
  you see in most of the subdirectories. They can be built individually, by
  running 'ant' in the subproject directory. If the subproject depends on other
  subprojects, these will be automatically (recursively) built.

  Alternatively if you type 'ant' in the project root, a file
  'avalon-excalibur.jar' will be built, containing an internally consistent set
  of components historically regarded as comprising 'Excalibur'.


  Component dependencies
  ----------------------

  Each component may have dependencies on other Avalon jars (typically Avalon
  Framework and LogKit), external jars, and on other components. These
  dependencies are listed in the ant.properties.sample files in the root of
  each component. You may need to customize these properties for your local
  environment. See the text in ant.properties.sample for more information.

  In general, it is safe to type 'ant', and if an external dependency is
  missing, you will be prompted on what to add to ant.properties. If instead
  you get compiler errors, the course of action you should take is as follows:

  - If building from CVS, make sure you have the latest code, with no local
    modifications. Always do 'cvs update -d' to get added directories.
  - Do an 'ant clean', and try again.
  - Make sure your CLASSPATH variable is empty (preferably), or at least
    doesn't contain any Avalon/Excalibur jars. On Windows, check this by typing
    'echo %CLASSPATH%' at a DOS prompt; on Unix, it's 'echo ${CLASSPATH}'.
  - If you're using jikes (Ant property build.compiler=jikes), try switching to
    Sun's javac. While generally better, certain versions of jikes have been
    known to cause odd problems.
  - If nothing helped, let us know! It might be a bug in the build system.
    Please include the Ant log messages in your mail. The output of 'ant -v' is
    also sometimes useful for debugging.


  Relation with Jakarta Commons
  -----------------------------

  Users are encouraged to take a look at the Jakarta Commons project
  (http://jakarta.apache.org/commons/), another part of Jakarta dedicated to
  producing quality reusable software. There are many functional overlaps
  between Commons and Excalibur. The primary difference is that Excalibur
  components are designed to fit in well with the Avalon Framework--in many
  cases implementing Framework interfaces--and generally applying the
  principles of SoC and IoC (see the website). Combine this philosophical
  difference with some minor disputes on project organization, and you have the
  current situation. In time, we hope to make the distinction between Commons
  and Exclibur much less prominent.

  Some of the components in excalibur have been moved into Jakarta Commons;
  we keep them in excalibur cvs only for backwards compatibility.


  Relation with Apache Commons
  ----------------------------

  Users are also encouraged to take a look at the Apache Commons project
  (http://commons.apache.org/), a new project @ apache dedicated to reusable
  components. The relationships between Avalon Excalibur, Jakarta Commons
  and the new Apache Commons are only currently being defined.

  It is likely that there will be a lot of synergy between Avalon Excalibur
  and Apache Commons. Some of the code currently in the excalibur CVS might
  move to Apache Commons if it makes sense to do so.

  This is not something to worry about; the code will not change and the
  developers will be the same guys 'n gals :D

  Relation with Jakarta Turbine
  -----------------------------

  Users are also encouraged to look at the subproject of Jakarta Turbine
  called Fulcrum (http://jakarta.apache.org/turbine/fulcrum/), which contains
  various services related to web development. Work is underway to make fulcrum
  and the services it offers completely avalon-compatbile.

  Problems?
  ---------

  If you have problems, questions or feedback about Excalibur, don't hesitate
  to contact the dev team at avalon-dev@jakarta.apache.org. Feedback is always
  welcome!


The Avalon Excalibur Team
