
PROJECT: Merlin
====================================================================

DESCRIPTION: 
------------

This directory contains the Merlin sources and all resources required to build a Merlin installation.  Structure and build procedures are described below.

STRUCTURE:
----------

  merlin
    |
    +-- assembly                 assembly implementation
    +-- assembly-spi             assembly SPI interfaces and classes
    +-- merlin-cli               merlin command line handler
    +-- merlin-core              merlin kernel, block, and container 
    +-- merlin-spi               merlin SPI interfaces and classes
    +-- merlin-plugin            plugin for Maven 
    +-- merlin-extensions        
        +- merlin-jndi           merlin JNDI connector
        +- merlin-servlet        merlin Servlet utilities
    +-- merlin-platform          site and installation resources
        +- src                   resources used to build a merlin installation
        +- tutorials             several merlin tutorials
        +- examples              example applications
        +- target
           +- docs               generate site documentation
    +-- meta                     meta package implementation
    +-- meta-spi                 meta SPI interfaces and classes
    +-- meta-tools               meta info generator task
    +-- target
        +- inst                  the generated installation (see 
                                 build procedure detailed below)


BUILD PROCEDURE:
----------------

The Merlin project requires the Maven project management tool.

If you have already a MERLIN_HOME environment variable defined, then all
you need to do is invoke the merlin-install goal (although it is 
recommended that discard current jar file beforehand).

  $ maven merlin-install

The merlin-dist goal builds a Merlin installation directory under the 
default /target/inst.  Please note that the installation build 
does not generate a demonstration apps at this time (this should be 
resolved shortly through the addition of a merlin plugin for Maven).  For
the moment a old version of a demonstration block is included in the CVS
to enable validation of an installation.

  $ maven merlin-dist

Following the creation of an installation you need to declare the environment 
variable MERLIN_HOME with a value corresponding to the directory in which you
choose to deploy the merlin system.  Please also make sure that you include
MERLIN_HOME/bin in your PATH environment variable.  You can then copy the inst
directory to MERLIN_HOME following which the Merlin command line utilities 
should function properly.  You can confirm this (on NT) by opening a new DOS 
window and invoking the following command:

  $ merlin -help

Support for the installation and execution of Merlin as an NT process is 
now included in the package (refer bin directory) - special thanks to Leif
for helping to set this up.

For additional information please check the documentation and also use the 
Avalon Users list (subscription details in the doc).  The merlin-site goal 
aggregates the sources from across the main Merlin sub-projects to build a 
consolidated project presentation. The goal should be invoked from the 
root merlin directory.

  $ maven merlin-site

Finally, please keep in mind that while Merlin is nearing completion and 
readiness for a first release, it is still alpha, APIs are changing, and 
much of the documentation needs to be updated.

SJM 15 April 2003
