To Build Castle you'll need:

NAnt  -> http://nant.sourceforge.net

NUnit is not a requirement as NAnt already includes it. 
We cannot provide them for you due to licensing issues.
Nant is a 100% .NET implmented build tool inspired by
ANT (http://ant.apache.org).  It is licensed under the
GPL, but if you install it separately as a tool (preferred)
then all is well.  NUnit is a testing framework inspired
by JUnit (http://junit.org), and has an installer for
you. 

Once your environment is set up you can type: 

> build.cmd -defaultframework:?????? compile

Where ?????? is one of the following:

  mono-1.0 for Mono 1.0
  net-1.1 for Microsoft .NET Framework 1.1
  net-1.0 for Microsoft .NET Framework 1.0
  sscli-1.0 for Shared CLI 1.0

For example:

> build.cmd -defaultframework:net-1.1 compile

You may also type:

> build.cmd

To build everything that is supported by your machine configuration.


Enjoy!

-- The Avalon Team
