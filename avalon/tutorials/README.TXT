
Merlin Tutorials
----------------

A set of buildable and executable projects that demonstrate
different features of the Merlin platform.

 * hello             - a very simple single component
                       demloyment example

 * configuration     - using default configurations, 
                       explicit deployment configuations,
                       and overriding configurations
 
 * context           - using standard context entries, 
                       constructed context values, 
                       volatile context entries, type-safe
                       context casting, and plugin context
                       strategy handlers.

 * dependencies      - demonstration of service dependecy 
                       and service provision declaration using
                       javadoc tags, auto assembly features, 
                       and orderly deployment and decommissioning
                       behaviour.

 * dynamics          - demonstrating dynamic component model 
                       resolution and instance activation

 * lifecycle         - lifecycle extension examples

 * main              - merlin embedded in a main method

 * parameterization  - demonstration of the parameterizable 
                       lifecycle stage

 * composition       - creation of virtual components by 
                       composition

Unless noted in a project readme, demonstrations can be built
using the following command (assuming you have installed 
Maven RC2 or later and a development release of Merlin 3.3):

  $ cd [a-tutorial-dir-containing-a-project.xml-file]
  $ maven jar:install

Runtime execution can normally by simulation relative to the 
target/classes directory, or by executing the generated jar 
file as shown in the following commands (check tutorial 
readme.txt for additional details).
 
  $ cd [a-tutorial-containing-a-project.xml]

then:

  $ merlin -execute target\classes

or:

  $ merlin -execute target\<jar-name>.jar

Alternatively, demonstrations can be built and deployed 
via maven using the following command:

  $ cd [a-tutorial-containing-a-project.xml]
  $ maven demo

Additional tutorials are in constant preparation. 
Questions and comments should be directed to 
users@avalon.apache.org.

