#! /usr/bin/sh

export RSYNC_RSH=ssh

ssh leosimons@cvs.apache.org mkdir -p ~/public_html/dist-candidates/avalon
cd target/www.apache.org/dist/avalon
rsync -vRrlpt --ignore-existing * leosimons@cvs.apache.org:~/public_html/dist-candidates/avalon