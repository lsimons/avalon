
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

Prerequisites:

Some recent version of the avalon meta plugin included content 
that has been moved to the repository plugin. To ensure that 
you do not have any conflicts you can updated the avalon/meta 
project from cvs and invoke avalon:setup from the avalon/meta 
root directory to update the meta plugin.

Before building the repository project you need to install the 
avalon-repository plugin.  A convinience goal is provided:

  $ cd /avalon/repository
  $ maven avalon:setup

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

