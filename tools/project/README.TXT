
Before attempting to build the bootstrap build system please 
read ETC.TXT.

To build and install the antlib using the following command:

  $ cd avalon/tools/project
  $ ant -buildfile bootstrap.xml install

The core antlib is now installed and we can use magic to build and package itself in a block archive.

  $ ant clean install


