                            Avalon Exporter
SEPT 2003

Avalon exporter is a simple component for exporting services via AltRMI.
It's designed to be used with Merlin 3.0 and takes advantage of the 
merlin lifecycle extension API.

To build with maven:

  - make sure you have the avalon meta plugin installed

  - RUN:

     maven jar:jar

To startup the server:

    merlin.sh target/avalon-exporter-0.1.jar

After the server has started, invoke the standalone client using the following command:

    maven exporter:run

  - You should get a "Hello World" message

To Do:

  - remove current tests from source tree and make real junit tests
  - seperate into api/impl
  - add ServiceDiscovery interface 
