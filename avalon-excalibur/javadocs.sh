#!/bin/bash
rm -Rf site/target/docs/api
mkdir -p site/target/docs/api
mkdir -p site/target/bigsrc

for i in `find . -type d -maxdepth 1 -mindepth 1 ! -name CVS ! -name site -printf '%P\n'`; do

    if [[ -d $i/src/java/ ]]; then
      cp -Rfp $i/src/java/* site/target/bigsrc
    else

      echo Warning: no Javadocs for $i

    fi
done

echo Copied all sources
echo invoking ant javadoc target....

cd site
ant -buildfile 'build-javadocs.xml'
