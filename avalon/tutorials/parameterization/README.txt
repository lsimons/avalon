
Hello
=====

The parameterization tutorial demonstrates a component implementing
the Avalon Parameterizable interface.

Build
-----

Build the project using the following command:

$ maven

The above command triggers the default goal jar:jar which will 
create a jar file under the target directory named 
parameters-1.0.jar.  The jar file contains a single
component, generated .xinfo descriptor, and a bundled block.xml 
deployment descriptor. 

Runtime
-------

To see Merlin in action, execute Merlin and give it either the 
jar file of the target/classes directory as the deployment 
argument.  

$ merlin target\classes -execute

Or:

$ merlin target\parameters-1.0.jar -execute


