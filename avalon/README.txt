
=======================================================================
            __
           /  \    Apache
          /    \__________________________________
         /  /\  \  \/  /    \ |  |  /   \|   \|  |
        /  /  \  \    /  /\  \|  |_|  O  |  \ \  |
       /__/    \__\__/__/  \__\_____\__ /|__|\___|


                         == Server Framework ==

=======================================================================
summary:   an effort to create, design, develop and maintain a common
           framework and set of components for applications written
           using the java language.
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
Most of our documentation is stored in CVS only in xml format. The HTML-
formatted pages that are generated from those xml files are available at
our website at

    http://avalon.apache.org/


Building from source?
=====================
The avalon buildfiles use a common buildsystem which utilizes maven and
forrest. You will need to download and install maven, the
maven-forrest-plugin, and the avalon-buildsystem module. After doing so,
change into the buildsystem directory and type `maven avalon:info` to
get yourself started.


Where did the Avalon-Framework subproject go?
=============================================
Don't worry, it didn't move very far. The framework sources have been
relocated to the framework subdirectory.
