
Welcome to the Plug-In Development at Avalon 
============================================

In Eclipse tradition, the package naming is exposed at the top-level 
directories .

org.apache.avalon.ide         -  Base directory for ALL IDE plugin development,
                                 not only Eclipse.

o.a.a.ide.repository          -  A plug-in agnostic model of a repository
                                 view. Contains API and utility classes for
                                 RepositoryAgent plug-ins.
                                     
o.a.a.ide.repository.testrepo -  Simple RepositoryAgent implementation for 
                                 testing only. Not well functioning, will soon
                                 disappear.

o.a.a.ide.eclipse.repository  -  Eclipse Repository Plug-In.



How to get started
==================

The easiest way is to download and install the Eclipse IDE.
You should then go to
    Window -> Preferences -> Import...
and select the file
    $CVSROOT/avalon-sandbox/ide/eclipse.prefs 

This will ensure a common Eclipse formatter.


Then select the menus;
    File -> Import -> Existing Project Into Workspace

Don't enter anything in the Project Name, and just browse to the directory
    $CVSROOT/avalon-sandbox/ide/org.apache.avalon.ide.repository

Make sure "workspace" (probably a bug in Eclipse 3.0) is removed from the
"Selection" text field.
When you press OK, the correct name should appear in the "Project Name" field.

Repeat this process for all 3 (maybe more by the time you read this) directories
in $CVSROOT/avalon-sandbox/ide/

Now you SHOULD have each of the projects imported and without compile errors
(i.e. no small red stop marks for the projects and its sources).

You are now able to edit the Plug-In sources.


How do I run the Plug-Ins
=========================

A so called Eclipse Feature has not been created yet. Nor any Ant build scripts.
So it is a little bit of "handicraft" to get going.

1. If you just want to run the Plug-Ins without editing and debugging, you
   need to do the above steps, and then 
       File -> Export -> JAR File 
   for the org.apache.avalon.ide.repository and 
       File -> Export -> Deployable Plug-ins and Fragments 
   for the other two projects.
   (Right now; Dec 2003, this has not been fixed up yet, but SOON...)
   
2. If you want to edit/run/debug, you have the 
       Run->Run As...-> Run-time Workbench
   and
       Run->Debug As...-> Run-time Workbench
   You will need to have the proper project and/or file selected to be able
   to start this.
   (Right now; Dec 2003, this has not been fixed up yet, but SOON...)


Any questions to dev@avalon.apache.org.


Cheers,
The Avalon Development Team
