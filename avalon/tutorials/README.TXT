
Merlin Tutorials
----------------

A set of buildable and executable projects that demonstrate
different features of the Merlin platform.

 * hello         - a very simple single component
                   demloyment example

 * configuration - using default configurations, 
                   explicit deployment configuations,
                   and overriding configurations

 * context       - using standard context entries, 
                   constructed context values, 
                   volatile context entries, type-safe
                   context casting, and plugin context
                   strategy handlers.

 * dependencies  - demonstration of service dependecy 
                   and service provision declaration using
                   javadoc tags, auto assembly features, 
                   and orderly deployment and decommissioning
                   behaviour.

 * composition   - creation of virtual components by 
                   composition


Unless noted in a project readme, demonstrations can be built
using the following command (assuming you have installed 
Maven 10 and Merlin 3.0):

  $ maven

Runtime execution can be by simulation relative to the 
target/classes directory, or by executing the generated jar 
file as shown in the following two command variants.

Windows:
 
  $ merlin -execute target\classes
  $ merlin -execute target\<jar-name>.jar

Unix/Linux:

  $ merlin.sh -execute target\classes
  $ merlin.sh -execute target\<jar-name>.jar

Alternatively, demonstrations can be built and deployed 
via maven using the following command:

  $ maven demo

Additional tutorials are in preparation. Questions and comments
should be directed to users@avalon.apache.org.

