                  Avalons Excalibur Tweety
                  -----------------------------
Notice
------
We've pretty much decided to stop developing tweety as a 'real' software
project; it's instead going to be more closely integrated with existing
documentation and software.

License
-------

Please see the LICENSE.txt file that came with this distribution for
licensing terms.


Background
----------

Talking about the complexity of avalon and its associated learning curve
made me draw a parallel with the complexity of unix. An approach that
has worked there: "Want to learn how to program unix? Start with it's
tiny brother - minix."

Tweety is a minimalist container explicitly designed for educational
purposes.


Goals
-----
- be well documented
- provide full support for the avalon framework interfaces
- provide support for nothing else
- use default framework implementations where possible
- sacrifice flexibility for readable code wherever possible
- sacrifice reusability for readable code wherever possible
- use the most simple configuration possible
- support only minimal metainfo (for example, no dependency mapping)
- no security
- minimal thread management
- no classloader management
- separation of engine and (mainable) embeddor


Use Case
--------
An example of an avalon container for simple components, to use in
teaching avalon concepts.


Getting Started:
----------------
Make sure you have ant installed, then type 'ant' in the directory where
you extracted the distribution. This will start up tweety and run the
included sample. You should see output on your screen similar to the
following:

Buildfile: build.xml

run:
     [java] [INFO] Tweety: Tweety is starting up...
     [java] [INFO] Tweety: Tweety is setting up the component implementing role 'chirp-mondo'
     [java] [INFO] Tweety: Tweety is setting up the component implementing role 'chirp-world'
     [java] [INFO] Tweety: Tweety is starting the component implementing role chirp-mondo
     [java] [INFO] ChirpWorld: I thawgt I saw a pussycat!
     [java] [INFO] Tweety: Tweety is starting the component implementing role chirp-world
     [java] [INFO] ChirpWorld: I thawgt I saw a pussycat!
     [java] [INFO] Tweety: Tweety has started.
     [java] [INFO] Tweety: Tweety is stopping the component implementing role chirp-mondo
     [java] [INFO] Tweety: Tweety is stopping the component implementing role chirp-world
     [java] [INFO] Tweety: Tweety has stopped.

BUILD SUCCESSFUL

Total time: 2 seconds


Now, drop the classes containing your avalon components into the classes/
directory, and any required library jars into the lib/ directory (if you
have your components packaged into jar files, you can drop those into the
lib/ directory as well).

Then, modify the tweety.properties file so tweety knows about your components.
The name you choose for the component is the name other components will be able
to find it under in their ComponentManager and/or ServiceManager.

Type 'ant' again and your components should run. It is that simple!

Note about File IO
------------------
Tweety will create a work directory (aplty called "work) and a permanent
storage directory (even more surprising, called "store") if they do not
exist yet. Your components can get to these through the Context, in the
form of java.io.Files, and then read from and write to them. The idea is
that the work directory contains temporary files your application uses,
whereas the storage directory is more or less permanent.
To get access to these directories, make your component implement
Contextualizable, and get() "component.home" and/or "component.work"
from the context you get, casting them to java.io.File.

More Documentation
------------------
Please see docs/index.html for more, or the tweety site at
http://jakarta.apache.org/avalon/excalibur/tweety.