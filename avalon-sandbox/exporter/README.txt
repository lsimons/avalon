                            Avalon Exporter
SEPT 2003

Avalon exporter is a simple component for exporting services via AltRMI.
It's designed to be used with Merlin 3.0 and takes advantage of the 
merlin lifecycle extension API.

To build with maven:

  - include http://jadetower.sf.net/dist in your remote repository list
  - make sure you have the avalon meta plugin
  - RUN:
     maven jar:jar

To run in merlin:

  - make sure your kernel.xml file includes the host listed above
  - run

    merlin.sh target/avalon-exporter-0.1.jar

To run standalone client

  - follow maven directions above
  - now run:

    maven exporter:run

  - You should get a "Hello World" message

To Do:

  - remove current tests from source tree and make real junit tests
  - seperate into api/impl
  - add ServiceDiscovery interface 
