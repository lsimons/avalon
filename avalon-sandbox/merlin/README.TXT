

                          APACHE AVALON MERLIN

What Is It?
-----------

   Merlin is an Avalon "Service Management Platform" (SMP):

   Merlin is a service management platform based on an advanced
   assembly engine. Its focus is to provide support for the
   encapsulation of a set of components and services within functional
   blocks. Blocks enable the separation of components as an
   implementation solutions from services established by respective
   components.

Where Is It?
------------

   For more detailed information, visit the online documentation at:

   http://avalon.apache.org/sandbox/merlin


What Is Included:
-----------------

 /$MERLIN_HOME
   /README.txt                This README file
   /bin
     InstallServiceNT.bat     Installs a basic merlin kernel as a
			      Windows(TM) NT service
     UninstallService-NT.bat  Uninstalls the Merlin service
     Wrapper.exe	      Tool for creating Windows(TM) services
     merlin.bat		      CLI for Merlin Kernel  (Windows)
     merlin.sh		      CLI for Merlin Kernel  (UNIX)
     run.sh		      CLI for Merlin Kernel  (UNIX)
   /config
     kernel-service.xml	      Default kernel config for NT service
     kernel.xml		      Default kernel config for CLI
     wrapper.conf	      Default NT Service configuration file
   /ext
   /lib
     Wrapper.dll              DLL file for NT service
     wrapper.jar	      java library for NT service
     /shared
        *.jar                 libraries for the shared classloader
     /system		      
        *.jar		      libraries for the system classloader
   /repository		      default local resource repository
   

Installation Instructions:
--------------------------

The latest installation instructions can be found at:
  http://avalon.apache.org/sandbox/merlin/starting/installation.html

