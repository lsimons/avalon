This was the letter that introduced the Mock UI and describes part of its structure.

Subject: [phoenix] Mock UI for Management Console
From: peter at apache.org
Date: Sun, 25 Nov 2001 14:50:11 +1100

Hi,

I just went and put together a Mock UI for the Phoenixs Management Console. You can check it out in jakarta-avalon-phoenix/console/src/mockui. It makes a few assumptions about how things will be managed. 

It also has no icons yet. You can see where I think that Icons should be included by looking for brackets. (i) means an icon should be used in addition to the associated text. [i] indicates the icon will replace the text while [bi] indicates that icon will be button-like.

-----------------------------

Assumptions
-----------

Firstly it assumes that you will be managing a single instance of Phoenix at any particular time. This will always be true in our current architecture. When we eventually allow management of clusters of Phoenix servers we will have to reasses this assumption.

The second assumption it makes is that you manage a single application at are time and are unlikely to be switching between different apps regularly. Thus if you choose to edit app X then app X will take over all of the screen real estate.

The third assumption it makes is that when ever you make modifications to configuration parameters (config parameters, permissions etc), it will require a restart of the relevent component before they are "activated". This is to make coding Blocks/other components easier and more fault tolerant. So in many cases you are not directly modifying the current object but creating a new object that will be picked up next time app starts up.

The UI
------

There is basically a few different types of screens/forms in the UI. The four common types of form are;

1. display a table of attributes + operations for a set of objects 
2. display a single objects attributes + operations 
3. display a table to generate querys against objects
4. display status/results of action

Table UI
--------

(1) is used in a couple of places like displaying all the extensions, applications and loggers in the kernel. It is also used to display the Manageable services exposed by blocks, the loggers used by application, the permissions applied to application.

The tables usually have the the name of the component in the first column (if it has a string based primary key). In the last column there is a set of operations that can be performed on component (usually things like delete, undeploy, restart, enable etc). These operations are usually based on current state of object.

The rows in table are sortable by clicking on the column names. This will sort them by the specified column.

The table UI usually also has an "add" or "create" button at top left hand of table - at least if the type stored in table supports creation in such a manner. There is also a "query" button at top right hand of screen for when you want to see only items that match a specific query. 

Currently clickling on the name field will goto a page that displays full attributes of specified object. Whereas the table may give an abridged version the single-object version will give full view. See Object UI for further discussion about this.

It may be that in the future you may be able to select the whole row (magic using javascript) however not sure which version is more usable.

Object UI
---------

The Object UI describes a single object. After the page title is a list of attributes of object and following that a list of operations. 

In some cases the Object UI allows you to edit the values of the object and then save them. The Object UI may also allow you to execute operations on objects - in some cases passing parameters.

Some examples of Object UI screens include;

* the create screens associated with each table
* property sheets associated with Container components
* base pages for application/phoenix

Query UI
---------

The query API is probably the most primitively developed screen at this stage but this reflects it's lesser importance. Essentially the query screens allow you to select criteria of the object to display. The criteria essentiallly consists of; attribute name + relation + value

There is currently no way to do OR or NOT criteria

Status UI
---------

Whenever an action is invoked it usually returns some form of results or indicates some sort of status. The Status UI is where this occurs. Usually this page just contains a title, some text describing the status of the operation and results of operation. At the end of the page is a link back to originating page.

Help
----

Currently there is no help on any of the screens. I was thinking of doing two things to rectify this. Place a little "?" besides things that have help describing what they are (like column names). Clicking on this may result in small popup window displaying help or maybe it could display in status bar.

The other way of providing help I was thinking about was placing a help button in some standard position on each page (upper right corner above title?). This button would display help about current screen and describe what is meant to happen in it etc. 

-----------------------------

So what does everyone think? I wouldn't mind some feedback on the ease of navigation and cognitive congruence (ie is it easy to use and does it match how you would do things?) Also do the assumptions I made seem reasonable? Any other Usability issues?

Now the actual presentation of it may be a bit lame and without the icons it may be a bit ugly ... but I never claimed to be a graphic artist ;) Anyone who has better skills in presentation layer should feel free to produce some better looking pages ;]

Anyways I plan to put the basic infrastructure in place sometime in next few weeks. Possibly next weekend. Basically it will consist of a servlet that connects to the MBeanServer via a RMI adaptor and presents a little data to user and maybe a few of the options. However it wont be much for now - unless of course someone wants to volunteer to do that bit ;)

-- 
Cheers,

Pete

--------------------------------------------------
 The fact that nobody understands you doesn't 
 mean you're an artist.
--------------------------------------------------
