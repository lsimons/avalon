=======================================================================
            __
           /  \    Apache
          /    \__________________________________
         /  /\  \  \/  /    \ |  |  /   \|   \|  |
        /  /  \  \    /  /\  \|  |_|  O  |  \ \  |
       /__/    \__\__/__/  \__\_____\__ /|__|\___|

=======================================================================


=======================================================================
summary:   Apache Avalon provides a complete platform for component
	   programming including a core framework, utilities, tools,
	   components, and a container written in the Java language.
publisher: Apache Software Foundation
website:   http://avalon.apache.org/
license:   Please see the LICENSE.txt file
=======================================================================

This is the main cvs module for the Apache Avalon project. It is
organised into several subdirectories containing several subprojects.
See the documentation and other resources inside those subdirectories
for more information.


More documentation?
===================
Most of our documentation is stored in SVN only in xml format. The HTML-
formatted pages that are generated from those xml files are available at
our website at

    http://avalon.apache.org/


Building from source?
=====================
Avalon uses an Apache Ant based build system dubbed 'magic'.  To
build from source, download an install Ant 1.6.1 or later, then
run:

   ant setup               <-- only needed once to bootstrap magic
   ant                     <-- builds entire Avalon platform


What is the repository layout?
==============================
A brief explanation of the source respository structure.  For more
info, see each subproject's own README documentation:

   central
   discovery
   planet
   runtime
   studio
   tools

     



Where did the Avalon-Framework subproject go?
=============================================
Don't worry, it didn't move very far. The framework sources have been
relocated to the framework subdirectory.
