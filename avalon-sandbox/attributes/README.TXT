
                          APACHE AVALON META

What?
-----

   Avalon Meta is a common component meta model.

Where?
------

   For more detailed information, visit the online documentation at:
   http://avalon.apache.org/sandbox/meta

Structure:
----------

/avalon-sandbox
 /meta
   /api ...................... immutable meta-info directives
   /spi ...................... meta model service interfaces
   /impl ..................... meta info builders, writers, serializers, readers
   /tools .................... ant plugin and javadoc tag related content
   /plugin ................... maven plugin
   /xdocs .................... project documetation
   /src ...................... build-related resources

Installation
------------

Install Maven 9. 
It is also recommeded that you follow the instructions concerning patching the Maven 9 binary posted at the following link: http://avalon.apache.org/sandbox/merlin/resources/notebook.html

The following commands will result in the complete build of the system, registration of snapshot jar files in your local Maven repository, and installation of the avalon plugin into you maven environment.

  $ cd avalon-sandbox/meta
  $ maven

Quetions should be directed to either of the following lists:

    dev@avalon.apache.org    // developer list
  users@avalon.apache.org    // user list

