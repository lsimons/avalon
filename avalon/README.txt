
                      A  V  A  L  O  N - F R A M E W O R K

                                   @version@


  What is it?
  -----------

  It is a set of classes and patterns that support high level server development.

  Where is it?
  ------------

  http://avalon.apache.org/framework/

  Requirements
  ------------
  Avalon-Framework requires a Java Runtime Environment, version 1.2 or above.
  
  Additional functionality is available if the following packages are available:
      - JRE 1.4 or higher
      - Avalon-Logkit 1.0 or higher
      - Log4J 1.2 or higher
      - Xerces-J 2.0 or higher

  Building
  --------
  For building from source, there are some additional requirments:
      - Ant 1.5 or above
      - Forrest 0.2 or above
      - JAVA_HOME to the jdk dir (eg:/usr/bin/jdk1.2 or c:\jdk1.3)
      - ANT_HOME set to the ant dir (eg: /opt/ant or c:\ant)
      - FORREST_HOME set to the forrest dir (eg: /opt/forrest or c:\forrest)
      - ant scripts in the path
      - forrest scripts in the path
  If the above requirments are all satisfied, you can build Avalon-Framework by
  running ant, normally done by typing `ant dist`.

  What are the files?
  -------------------
  README.txt        This file.  It is a general overview.
  LICENSE.txt       The Apache Software License 1.1 that covers Avalon.
  KEYS              The list of public keys used for releasing Avalon.
  build.xml         The ANT build script.
