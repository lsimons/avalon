=======================================================================
            __
           /  \    Apache
          /    \__________________________________
         /  /\  \  \/  /    \ |  |  /   \|   \|  |
        /  /  \  \    /  /\  \|  |_|  O  |  \ \  |
       /__/    \__\__/__/  \__\_____\__ /|__|\___|


                         == Avalon-Framework ==

=======================================================================
summary:   the core interfaces of the avalon project
publisher: Avalon Software Foundation
website:   http://avalon.apache.org/framework/
version:   4.1.5RC-2
license:   Please see the LICENSE.txt file
=======================================================================

The Avalon Framework consists of interfaces that define relationships
between commonly used application components, best-of-practice pattern
enforcements, and several lightweight convenience implementations of
the generic components.

More documentation?
===================
We do not distribute all our documentation through releases. Rather,
you are encouraged to visit our website at
    http://avalon.apache.org/framework/
for more information.

Building from source?
=====================
Avalon is built using maven. You will need to download and install
maven, if you do not have it already. The following maven goals are
available:

  avalon:build            - build from sources, jar and install into
                            the local Maven repository
                            [reactor jar:install]
  avalon:build-snapshot   - build and install snapshot jars
                            [reactor jar:install-snapshot]
  avalon:site             - build the site
                            [prereqs: avalon:build]
  avalon:dist             - build the distribution under
                            ${basedir}/target
                            [prereqs: avalon:site]
  avalon:clean            - clean this project and all subprojects
                            [reactor clean:clean]

