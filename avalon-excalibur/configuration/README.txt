                  Avalons Excalibur Configuration
                  -------------------------------

The Configuration package contains extensions to the Avalon Framework
supporting configuration management.

Resources:
----------

CascadingConfiguration
----------------------
Provides support for the creation of a base and default parent relationship
between two configuration instances.  This is helpful when you have a default
configuration together with a configuration that contains overriding values.
The level of cascading of configurations is arbitary.  The CascadingConfiguration
implementation impements the classic Configuration interface and delegates 
invocations to the base configuration.  If the delegate operation fails, the 
implementation will attempt to resolve the invocation against the parent 
configuration.  Both base and parent configuration reference can be classic or
cascading enabling the creation of complex configuration graphs.

ConfigurationUtil
-----------------
Provides a static list operation that is helpful in debugging.  The list 
operation generates a simple string representation of a configuration.



