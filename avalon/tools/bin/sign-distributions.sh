#!/bin/sh

echo -n "Enter gpg password (default key): "
read PASSWORD

for i in distributions/*.gz; do 
 rm -f $i.asc
 echo $PASSWORD | gpg --armour --detach-sign --passphrase-fd 0 --sign $i; 
done;

for i in distributions/*.zip; do 
 rm -f $i.asc
 echo $PASSWORD | gpg --armour --detach-sign --passphrase-fd 0 --sign $i; 
done;
