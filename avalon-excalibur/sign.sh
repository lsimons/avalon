#!/bin/bash
echo =============================================================
echo  hacky automated gpg signing script, by LSD
echo =======================================v0.1-Cygwin===========
echo
echo \
'Tip: Temporarily disable your passphrase to avoid typing it
a hundred times. Make sure your machine is disconnected
before you do this!
The commands go somewhat like:

    gpg --edit-key <YOUR ID>
    > key <YOURKEY>
    > passwd
    <YOUR PASSPHRASE>
    <enter>
    <enter>
    yes
    quit
    yes

then run this script. After running the script, make sure you
re-enable your passphrase:

    gpg --edit-key <YOUR ID>
    > key <YOURKEY>
    > passwd
    <YOUR PASSPHRASE>
    <YOUR PASSPHRASE>
    quit
    yes

 -------------------------------------------------------------
 Now signing the distributions
 -------------------------------------------------------------
'

for i in component event sourceresolve store xmlutil; do
    echo - Signing the $i distributions
    cd $i/target/distributions
    for j in `find . -type f -maxdepth 1 -name '*.zip' -or -name '*.gz'`
    do
      #echo gpg -v --output $j.asc --detach-sig --armor $j
      gpg -v --output $j.asc --detach-sig --armor $j
    done

    echo - Signing the $i jars
    cd ..
    for j in *.jar
    do
      #echo gpg -v --output $j.asc --detach-sig --armor $j
      gpg -v --output $j.asc --detach-sig --armor $j
    done

    cd ../..
done
