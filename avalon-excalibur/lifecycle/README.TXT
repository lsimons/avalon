äö

Avalon Framework
================

Available goals
---------------

  avalon:build            - build from sources, jar and install into the local Maven repository
                            [reactor jar:install]
  avalon:build-snapshot   - build and install snapshot jars
                            [reactor jar:install-snapshot]
  avalon:site             - build the site
                            [prereqs: avalon:build]
  avalon:dist             - build the distribution under ${basedir}/target
                            [prereqs: avalon:site]
  avalon:clean            - clean this project and all subprojects
                            [reactor clean:clean]

