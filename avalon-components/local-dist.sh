#! /usr/bin/sh
echo =============================================================
echo          Stupid Shell Script to grab all dists
echo =============================================================

echo Generating distributions in
echo
echo '   target/www.apache.org/dist/avalon/'
echo
echo removing old stuff...
rm -Rf target/www.apache.org


echo creating directories....
mkdir -p target/www.apache.org/dist/avalon/
cp site/etc/LICENSE.txt target/www.apache.org/dist/avalon/

DATE=`date +%Y%m%d`

for i in \
    `find . -type d -maxdepth 1 \
    ! -name 'CVS' ! -name 'target' ! -name 'site' ! -name 'src' ! -name '\.' ! -name 'threads-tutorial' \
    -printf '%f\n'`
#for i in `find . -type d -maxdepth 1 -name 'connection-api' -printf '%f\n'`
do
    echo - Deploying cornerstone-$i to local distribution location

    mkdir target/www.apache.org/dist/avalon/cornerstone-$i
    mkdir target/www.apache.org/dist/avalon/cornerstone-$i/binaries
    mkdir target/www.apache.org/dist/avalon/cornerstone-$i/jars
    mkdir target/www.apache.org/dist/avalon/cornerstone-$i/source
    cd $i/target/distributions

    # select binaries, but not dists with a timestamp, and copy to the appropriate location
    find . -type f -maxdepth 1 \
        ! -name "*$DATE*" \
        ! -name '*-src\.*' \
        -exec \
          cp -f \{\} ../../../target/www.apache.org/dist/avalon/cornerstone-$i/binaries/ \;

    # select sources now
    find . -type f -maxdepth 1 \
        ! -name "*$DATE*" \
        -name '*-src\.*' \
        -exec \
          cp -f \{\} ../../../target/www.apache.org/dist/avalon/cornerstone-$i/source/ \;

    cd ..
    # jars
    find . -type f -maxdepth 1 \
        ! -name "*$DATE*" \
        -name '*\.jar*' \
        -exec \
          cp -f \{\} ../../target/www.apache.org/dist/avalon/cornerstone-$i/jars/ \;

    # license
    cd ../../target/www.apache.org/dist/avalon/cornerstone-$i/
    ln -s ../LICENSE.txt
    cd binaries
    ln -s ../LICENSE.txt
    cd ../jars
    ln -s ../LICENSE.txt
    cd ../source
    ln -s ../LICENSE.txt

    # done
    cd ../../../../../../
done
