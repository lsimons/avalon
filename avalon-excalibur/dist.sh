#! /usr/bin/sh

echo =============================================================
echo          Stupid Shell Script for maven reactor problem
echo =============================================================

echo Doing cleanup
echo -------------------------------------------------------------

#maven do-clean 2>&1 3>&1 4>&1 > dist-clean-log.txt
find . -name 'target' -or -name '8.log' -exec rm -Rf \{\} \;  2>&1 3>&1 4>&1 > dist-build-log.txt

echo
echo Building and installing snapshot jars
echo -------------------------------------------------------------

#maven 2>&1 3>&1 4>&1 > dist-build-log.txt
curr=`pwd`
for i in component event/api event/impl sourceresolve store xmlutil; do
    cd $i
    maven jar:install-snapshot  2>&1 3>&1 4>&1 > $curr/$i-build-log.txt
    cd $curr
done

echo
echo -------------------------------------------------------------
echo Building distributions
echo -------------------------------------------------------------
echo

#for i in \
#    `find . -type d -maxdepth 1 \
#    ! -name 'CVS' ! -name 'target' ! -name 'site' ! -name 'src' ! -name '\.' ! -name 'threads-tutorial'`; do

for i in component sourceresolve store xmlutil; do
    cd $i
    echo - BUILDING DIST for: $i > ../dist-log.txt
    maven dist 2>&1 3>&1 4>&1 > ../$i-dist-log.txt
    cd ..
done

# event has a different 'dist' goal...
cd event
echo - BUILDING DIST for: event > ../dist-log.txt
maven event:dist  2>&1 3>&1 4>&1 > ../event-dist-log.txt
cd ..
