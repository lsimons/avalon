
                          APACHE AVALON META

What?
-----

   Avalon Meta is a common component meta model.

Where?
------

   For more detailed information, visit the online documentation at:
   http://avalon.apache.org/avalon/meta

Structure:
----------

/avalon
 /meta
   /api ...................... immutable meta-info directives
   /spi ...................... meta model service interfaces
   /impl ..................... meta info builders, writers, serializers, readers
   /tools .................... ant plugin and javadoc tag related content
   /plugin ................... maven plugin
   /site .................... project documetation

Installation
------------

The following commands will result in the complete build of the system, registration of snapshot jar files in your local Maven repository, and installation of the avalon plugin into you maven environment.

  $ cd meta
  $ maven

Quetions should be directed to either of the following lists:

    dev@avalon.apache.org    // developer list
  users@avalon.apache.org    // user list

