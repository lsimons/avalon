Requirements
============

You'll need Ant 1.4.1, and to have set the environmental variable ANT_HOME to be the 
location of it's installation, before you can build the demo in this kit.

Building the demo
=================

Launch Ant in this directory.  The build process will take come seconds and make four 
archives in build/lib :


  phoenix-demo-server.jar
  
    The server that can run standalone.  It is an executable jar.
   
  phoenix-demo.bar
  
    The server contained withing a Pheonix compatable block. This would be useful if 
    you intended to include this component as part of a multi server solution.
  
  phoenix-demo.sar
  
    The above block contained in a server application archive file.  This will run
    if dropped into Phoenix's apps directory.
  
  phoenix-demo-client.jar  
  
    For all server variations, this is the client that will demonstrate client/server
    functionality.  It is an executable jar.
    
    
How to progress with your own server
====================================

1) The general idea is to replace our "phoenixdemo" Java classes with your own and 
modify the build script to your own requirements.

2) Note the separation (in terms of packages) of the block, server and client.
  
3) Know that you should use Cornerstone's components for connection 
management and thread pooling (plus many other things).  See HelloWorldServer in
Cornerstone for the examples.
    
4) See the src/conf files defining how blocks are laced together.  Cornerstone has 
some more complex cases that have dependancies.

Things to read up on
====================

1) Blocks using services from other blocks.

2) Gereral API features of Excalibur.

3) Logkit & how to use it.

4) Lifecycle concepts of Phoenix blocks.

- The Avalon team.