                  Avalons Excalibur Tweety
                  -----------------------------

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
If you downloaded a source release of the component then you
will need to build the component. Directions for building the
component are located in BUILDING.txt

If you downloaded a binary release, or a release with both binary
and source then it is recomended you look over the documentation
in docs/index.html - and then look into the examples/ directory
for examples of the component in action.
