
Avalon Repository Package
=========================

Overview
--------

This is the Avalon Repository Package.  It provides support for 
the retrival of artifacts from a set of remote repositories and 
the construction of application classloaders.  This is core 
container facilities that serves at the overal bootstrapping
platform.

Build instructions
------------------

Build Dependencies: Maven beta10 or later.

IMPORTANT - before building avalon/repository you must
checkout and build the avalon/util project.

The following instructions assume you are working from the 
repository root directory.

The following goal builds the repository project:

  $ maven avalon:build

An additional utility goal is available to clean all repository
subprojects.

  $ maven avalon:clean

Structure
---------

The repository facility is structured into 7 build subprojects.

* api    -- the client API
* impl   -- the default implementation
* main   -- the bootstrap deliverable
* plugin -- maven plugin supporting artifact metadata generation
* site   -- site documentation
* spi    -- interfaces and imutable classes required by a repository
            implementation (in addition to the api)
* test   -- test project
* util   -- implementation utilities shared by the default impl and main

