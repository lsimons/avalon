export RSYNC_RSH=ssh

ssh leosimons@cvs.apache.org mkdir -p ~/public_html/avalon
cd target/www.apache.org/dist/avalon
rsync -vRrlpt --ignore-existing * cvs.apache.org:~/public_html/avalon