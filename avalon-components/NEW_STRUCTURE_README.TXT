
A new structure is being proposed within which each component in cornerstone
is seperated into a distinct project.  Within the component project are two 
sub-projects, a api project and default implimentation project.

To build the a component (threas component example):

  $ cd avalon-cornerstone\threads
  $ maven build

To build the documentation:

  $ cd avalon-cornerstone\site
  $ maven site

These instructions are subject to change as the objective is to optimise one 
component project prior to replication of the patter to other projects.

