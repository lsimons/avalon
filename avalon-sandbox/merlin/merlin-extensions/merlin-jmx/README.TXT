
A JMX server that establishes a JRMP adapter together with a test case that validates local registration of a managable mbean.  What is needed now is extensions supporting remote registration of an appliance and/or component instance.

Build instructions:

  $ cd merlin-jmx
  $ maven merlin:install

To start the jmx server, use:

  $ merlin conf\block.xml 

or, 

  $ merlin target\merlin-jmx-1.0.jar


