
Security enabled Composition Management
=======================================

Overview
--------

This is an extension of the tutorial/composition, in that here
we use security profiles to GRANT access for one component, but
not the other, to the Location, which is configured to 
"Europe.France.Paris" in the Application.xprofile defaults.
One application component has the security enabled for Spain
and the other has the security enabled for France. 

This tutorial is interesting as it shows that it is possible
to grant different security access to the same codebase, which
is not possible in standard Java security policies.

For more information about the other aspects of this example, 
please check the tutorial/composition, which is practically
identical.

Build and execution instructions:
---------------------------------

  $ cd application/impl
  $ maven build
  $ merlin -execute target\*.jar -repository %MAVEN_HOME% 

  [INFO   ] (application.publisher.publisher): created
  [INFO   ] (application.location.info): location: Paris
  [INFO   ] (application.application): servicing application
  [INFO   ] (application.location.info): location: Paris
  [INFO   ] (application.publisher.publisher): created
  [INFO   ] (application.publisher.publisher):
  ******************
  * Paris
  ******************
  [INFO   ] (application.application): done


Use the following command to include container debug level log entries for the 
demanding developer.

  $ merlin -execute -repository %MAVEN_HOME% target\*.jar -config conf\debug.xml

Summary
-------

The purpose of this demonstration is to show how the container can be viewed as
an implementation strategy for the creation of a virtual service, and how these
service can be composed within a higher level containers, enabling new 
implementation solutions.  The key to achiving this is related to the <service> 
directives inside the <container> directive.  Each service directive describes 
a service that is exported by the container in its role as a virtual component.


