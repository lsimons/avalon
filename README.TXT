
The metro tree contains code dealing with the refactoring of the existing 3.3.0 codebase to enable the establishment of the repository management subsystem as a top-level plugin controller capable of launching and running the merlin, magic or anything other system.  The code structure and content is described below:

  metro/transit

    The transit main package includes a bootstrapping system supporting 
    initialiozation of transit from the command line, from ant, and classic
    embedding scenarios.  The code includes the repository APIs, controller
    interface, repository monitors, and the basic code code of the repository
    loading utility.  The implementation has a weak dependency on the transit
    plugin which in turn is used to establish the fully functional transit 
    kernel.

    The transit/plugin is a plugin implementation that is the transit plugin
    controller.  It is establish by transit/main and is responsible for the 
    management of the execution of a single target plugin (the application).
    The objective is to enable the execution of a target application such
    as merlin or magic by delegating command line arguments through the 
    boostrap and controller to the target.  The net result will be the 
    elimination of repository and classloader managment code in both merlin 
    and magic and the elimination of logging system concerns from the merlin
    system.

  metro/util

    The util package is derived from the avalon-util pacakages with some 
    degree of rationalization. The Env content has been folded into the 
    transit main package and the crieteria, defauolts, exception and i18n
    packages are setup under the metro namespace and used in conjunction with 
    the transit plugin controller implementation.

  metro/runtime/logging/

    Responsibility for the establishment of a logging system has been moved
    from the deplioyed plugion to the plugin controller.  The code in this
    section is based on the 3.3.0 runtime/logging package with changes to 
    synchronize with the transit plugin model.  The logkit plugin has been 
    rationalized through folding together of current logkit with the logging
    logkit plugin classes together with strict sepatation of target subsystem
    as sub-plugin plugins.  In addition the logigng factory critera have been 
    separated into a shared library and the Log4J plugin has been updated to 
    support the transit plugin model.

  metro/configuration

    Refactoring of the framework configration and util-configuration into a 
    api and impl package under a new package namespace.  The different is 
    primarily related to the frequent use of configuration models outside of
    a fomal component scenario and the need to redefine the error handling 
    model without the restrictions implied by framework.
    
  metro/runtime/extension

    Code is basically a direct fork of the util-extension poackage placed
    here as this needs to be follded into the transit package, enabling the 
    elimination of any classloading management from managed applications.

Examples:

  The following example shows how metro can be used to simply download 
  an artifact to the local cache:

    $ metro -get artifact:saxon/saxon#6.5.3 -overwrite

  The following example demonstrates a request for the controler to 
  download and run a plugin:

    $ metro -load artifact:metro/transit/metro-transit-demo#SNAPSHOT -execute
    [INFO   ] (metro): Hello.

  Both examples assume that you have a properties file named metro.properties
  that includes the property metro.hosts with a comma separated list of 
  remote repositories from which respective resources can be resolved. There
  are also a bunch of metro.initial.* properties that define the initial 
  boostrap cache, hosts, proxy setting etc. detailed in the InitialContext
  interface class. THe environment variable METRO_HOME is used which is simper
  to Magic in that the default cache is %METRO_HOME%/main.

To-be-done:

Some refactoring of the plugin class to more cleanly separate 'get' versus 
'load' objectives, the addition of a telnet controller (in particular the 
ability to reload plugins), and improvements to the repository model to include host policies in the basic apis.  From there its basically a 
question of adding functionality into the plugin realted to security, policy 
management, etc.



