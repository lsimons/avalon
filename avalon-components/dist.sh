echo =============================================================
echo          Stupid Shell Script for maven reactor problem
echo =============================================================

echo Doing cleanup
echo -------------------------------------------------------------

maven do-clean 2>&1 3>&1 4>&1 > dist-clean-log.txt

echo
echo Building and installing snapshot jars
echo -------------------------------------------------------------
maven 2>&1 3>&1 4>&1 > dist-build-log.txt

echo
echo -------------------------------------------------------------
echo Building distributions
echo -------------------------------------------------------------
echo

for i in \
    `find . -type d -maxdepth 1 \
    ! -name 'CVS' ! -name 'target' ! -name 'site' ! -name 'src' ! -name '\.' ! -name 'threads-tutorial'`
do
    cd $i
    echo - BUILDING DIST for: $i
    maven dist 2>&1 3>&1 4>&1 > $i-dist-log.txt
    cd ..
done
