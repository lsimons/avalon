export RSYNC_RSH=ssh

cd target/www.apache.org/dist/avalon
ssh leosimons@www.apache.org mkdir ~/test
rsync -vRrlpt --ignore-existing * www.apache.org:~/test