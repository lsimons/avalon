Basics
======
To start a project that uses JBS, create a build.xml that looks like the
following:

build.xml sample
----------------
    <?xml version="1.0"?>

    <project default="jar:install-snapshot" name="JBS-based build" basedir=".">
        <property file="project.properties"/>
        <property name="jbs.home" value="${user.home}/.jbs"/>
        <import file="${jbs.home}/build-project.xml"/>
    </project>

Next, create a file named project.properties that contains the name for
your project as the value for a project.name:

project.properties sample
-------------------------
project.name = myproject

That's it!

Dependencies
============
if your project has any compile or runtime dependencies, create a text file
named dependencies.list. This file contains lines of dependencies. Each
line has several fields that are space-seperated. You can create comments
on empty lines by starting the line with '#' or '//' or a space. Empty lines
are also allowed.
The recognized fields are as follows:

artifact-id [version [group-id [repository [gump-id [opts]]]]]

artifact-id:        the name of the jar
version:            the version of the jar required. Defaults to SNAPSHOT.
group-id:           the group the jar belongs to. Defaults to whatever
                    you set for the artifact id.
repository          the remote (maven-compatible) repository from which the
                    jar can be fetched. Defaults to the repository maintained
                    at ibiblio.
gump-id             the name of the project to depend on during gump
                    integration. Defaults to whatever you set for the artifact
                    id. Use a value of "SKIP" if this dependency should not
                    be included during gump testing.
opts                A comma-seperated list (no spaces allowed!) of options
                    to apply to the dependency (only used during gump
                    integration right now). Options include:

        runtime     to indicate this is a runtime dependency (otherwise it is
                    assumed this is a compile-time dependency only)
        optional    to indicate the project will compile and run without this
                    dependency (otherwise it is considered non-optional)
        inherit     to indicate the inheritance policy to apply during gump
                    integration. If only the keyword is given, a value of "all"
                    is assumed. If no keyword is given, a value of "none" is
                    assumed. You can specify what policy to apply by using
                    an "=" followed by the policy (without spaces), like this:

                    inherit=jars

                    possible values include "all","jars","none","hard" and
                    "runtime"

While everything after the last field is ignored, you should not depend on this
behaviour, and not place any comments or other stuff there, because new fields
may be added in the future.

For example, if your project depends on commons-collections version 1.0 and
uses it during runtime as well (and you are okay with the jar being downloaded
from ibiblio if it doesn't exist in your local repository yet, add a line like
the following:

commons-collections 1.0 - - - runtime,inherit=runtime

If your project depends on the picocontainer tck and you want to grab it from
the dist.codehaus.org repository, add a line like this:

picocontainer-tck SNAPSHOT picocontainer http://dist.codehaus.org SKIP

(you need to use SKIP because picocontainer does not participate in gump
integration. Your project will probably fail during gump integration as a
result).

Gump integration
================
JBS can generate a gump descriptor for your project. To create one, create
your dependencies.list file, and make sure all the gump-specific fields are
correct as well. Then, invoke ant with the 'jbs:gump-descriptor' target:

    ant jbs:gump-descriptor

this will create a file named gump-${project.name}.xml. Include this file from
your main module definition (which you'll need to write by hand).

Overriding JBS behaviour
========================
You can override any part of the JBS default behaviour by defining targets in
your build.xml with the same names as the JBS targets. For example, if you
want to change how the 'compile' functionality works, define a new target
in your buildfile named 'jbs:java-compile':

<?xml version="1.0"?>

<project default="jar:install-snapshot" name="JBS-based build" basedir=".">
    <property file="project.properties"/>
    <property name="jbs.home" value="${user.home}/.jbs"/>
    <import file="${jbs.home}/build-project.xml"/>

    <target name="jbs:java-compile" if="java.src.present"
            depends="jbs:init,jbs:get-dependencies">
        <echo>=======================================================================
   Compiling Sources for ${project.name} (CUSTOM TARGET!)
=======================================================================</echo>

        <!-- we want to apply a commonly used trick: filter copyright info -->
        <mkdir dir="${jbs.build.dir}"/>
        <mkdir dir="${jbs.build.dir}/classes"/>
        <mkdir dir="${jbs.build.dir}/src/java"/>

        <tstamp>
            <format property="copyright.year" pattern="yyyy"/>
        </tstamp>
        <copy todir="${jbs.build.dir}/src/java">
            <fileset dir="${jbs.src.dir}/java"/>
            <filterset>
                <filter token="COYYRIGHTYEAR" value="1997-${copyright.year}"/>
                <filter token="COYYRIGHTHOLDER" value="The Apache Software Foundation"/>
            </filterset>
        </copy>

        <javac
                destdir="${jbs.build.dir}/classes"
                excludes="**/package.html"
                debug="${jbs.compile.debug}"
                deprecation="${jbs.compile.deprecation}"
                optimize="${jbs.compile.optimize}">

            <src>
                <!-- use the filtered sources -->
                <pathelement path="${jbs.build.dir}/src/java"/>
            </src>
            <classpath>
                <pathelement path="${dependency.classpath}"/>
            </classpath>
        </javac>
    </target>
</project>

This is a quite advanced technique, and I advise caution before using it. It's
also subject to breakage as JBS evolves. But its the only real alternative to
maven's <pregoal/> and <postgoal/> functionality at the moment.