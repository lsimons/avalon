
JMX
=====

JMX is a demonstration component used as part of the 
Merlin Tutorial, showing how to establish JMX for Merlin hosted
components.

Overview
--------
We are using the simplest form of component, the Hello component,
and adding an simple management interface, which allows to set
the output string.

  * HelloComponent.java - the component source
  * block.xml - the container defintion

Build
-----

Build the project using the following command:

$ maven

The above command triggers the default goal jar:jar which will 
create a jar file under the target directory named 
merlin-hello-tutorial-1.0.jar.  The jar file contains a single
component, generated .xinfo descriptor, and a bundled block.xml 
deployment descriptor. 

Runtime
-------

To see Merlin in action, execute Merlin and give it either the 
jar file of the target/classes directory as the deployment 
argument.  

$ merlin target\classes -execute

Or:

$ merlin target\merlin-hello-tutorial-1.0.jar -execute

The -execute parameter in the above command line simply tells 
Merlin to deploy and decomission the component.  If we didn't
include the -execute option, then Merlin would stay running
until we forced termination using ^C.

To get an idea of what is happening behing the scenes you can
override the logging priorities using a configuration override
argument.  In addition, the -debug CLI parameter presents a 
summary of the resources pulling together to handle deployment:

$ merlin target\classes -execute -debug -config conf\config.xml


