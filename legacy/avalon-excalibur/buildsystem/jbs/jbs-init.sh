#! /bin/bash

wget http://cvs.sourceforge.net/viewcvs.py/*checkout*/jicarilla/jicarilla-sandbox/buildsystem/build-basic.xml
mv build-project.xml build.xml
echo '# Dependencies (test file)
#
# artifact-id [version [groupid [repository [gump-id [gump-opts]]]]]
#
' > dependencies.list
