/* 
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avalon.tools.tasks;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.taskdefs.optional.junit.FormatterElement;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTask;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import org.apache.avalon.tools.home.Home;
import org.apache.avalon.tools.project.Definition;

/**
 * Load a goal. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class TestTask extends HomeTask
{
    public static final String TEST_KEY = "avalon.target.test";
    public static final String TEST_VALUE = 
       PrepareTask.TARGET + "/test";

    public static final String TEST_CLASSES_KEY = "avalon.target.test.classes";
    public static final String TEST_CLASSES_VALUE = 
       TEST_VALUE + "/classes";

    public static final String TEST_ENV_KEY = "avalon.target.test.env";
    public static final String TEST_ENV_VALUE = 
       TEST_VALUE + "/env";

    public static final String TEST_TMP_KEY = "avalon.target.test.temp";
    public static final String TEST_TMP_VALUE = 
       TEST_VALUE + "/temp";

    public static final String DEBUG_KEY = "test.compile.debug";
    public static final boolean DEBUG_FLAG = true;

    public static final String FORK_KEY = "test.compile.fork";
    public static final boolean FORK_FLAG = false;

    public static File getTargetSrcTestDirectory( Project project )
    {
        String src = project.getProperty( PrepareTask.TARGET_SRC_TEST_KEY );
        return new File( project.getBaseDir(), src );
    }

    public static File getTargetTestClassesDirectory( Project project )
    {
        String classes = project.getProperty( TEST_CLASSES_KEY );
        return new File( project.getBaseDir(), classes );
    }

    public static File getTargetTestEnvDirectory( Project project )
    {
        String env = project.getProperty( TEST_ENV_KEY );
        return new File( project.getBaseDir(), env );
    }

    public static File getTargetTestTempDirectory( Project project )
    {
        String temp = project.getProperty( TEST_TMP_KEY );
        return new File( project.getBaseDir(), temp );
    }

    public void init() throws BuildException 
    {
        super.init();
        setProjectProperty( TEST_CLASSES_KEY, TEST_CLASSES_VALUE );
        setProjectProperty( TEST_ENV_KEY, TEST_ENV_VALUE );
        setProjectProperty( TEST_TMP_KEY, TEST_TMP_VALUE );
        setProjectProperty( DEBUG_KEY, "" + DEBUG_FLAG );
        setProjectProperty( FORK_KEY, "" + FORK_FLAG );
    }

    public void execute() throws BuildException 
    {
        File src = getTargetSrcTestDirectory();
        if( src.exists() )
        {
            File classes = getTargetTestClassesDirectory();
            if( !classes.exists() )
            {
                log( "creating target test classes directory" );
                createDirectory( classes );
            }

            Path classpath = 
              getHome().getRepository().createPath( 
                getProject(), getDefinition() );

            //
            // add the project jar to the classpath for the compilation
            // of the test classes and compile the test classes into the 
            // target/test-classes directory
            //

            File jar = JarTask.getJarFile( getProject(), getDefinition() );
            classpath.createPathElement().setLocation( jar );
            compile( src, classes, classpath );
            classpath.createPathElement().setLocation( classes );
            test( classpath );
        }
    }

    private void compile( File sources, File classes, Path classpath )
    {
        File basedir = getProject().getBaseDir();
        Javac javac = (Javac) getProject().createTask( "javac" );
        Path src = javac.createSrc();
        Path.PathElement element = src.createPathElement();
        element.setLocation( sources );
        javac.setDestdir( classes );
        javac.setDebug( getDebugProperty() );
        javac.setFork( getForkProperty() );
        javac.setClasspath( classpath );
        javac.init();
        javac.execute();
    }

    private void test( Path classpath )
    {
        FileSet fileset = new FileSet();
        fileset.setDir( getTargetSrcTestDirectory() );
        fileset.createInclude().setName( "**/*TestCase.java" );
        fileset.createExclude().setName( "**/Abstract*.java" );

        File base = getTargetTestTempDirectory();
        createDirectory( base );
        JUnitTask junit = (JUnitTask) getProject().createTask( "junit" );
        junit.setFork( getForkProperty() );

        JUnitTask.SummaryAttribute summary = new JUnitTask.SummaryAttribute();
        summary.setValue( "on" );
        junit.setPrintsummary( summary );
        junit.setHaltonfailure( true );
        junit.setHaltonerror( true );
        junit.setErrorProperty( "test-errors" );
        junit.setFailureProperty( "test-failures" );
        if( FORK_FLAG )
        {
            junit.setFork( true );
            junit.setDir( base );
        }
        junit.setShowOutput( true );
        junit.setTempdir( base );
        junit.setReloading( true );
        junit.setFiltertrace( true );
        junit.createClasspath().add( classpath );
        junit.createBatchTest().addFileSet( fileset );

        FormatterElement plainFormatter = new FormatterElement();
        FormatterElement.TypeAttribute plain = new FormatterElement.TypeAttribute();
        plain.setValue( "plain" );
        plainFormatter.setType( plain );
        junit.addFormatter( plainFormatter );

        FormatterElement xmlFormatter = new FormatterElement();
        FormatterElement.TypeAttribute xml = new FormatterElement.TypeAttribute();
        xml.setValue( "xml" );
        xmlFormatter.setType( xml );
        junit.addFormatter( xmlFormatter );

        Environment.Variable basedir = new Environment.Variable();
        basedir.setKey( "basedir" );
        basedir.setValue( getTargetTestEnvDirectory().toString() );

        junit.addSysproperty( basedir );

        junit.init();
        junit.execute();
    }

    private File getTargetSrcTestDirectory()
    {
        return getTargetSrcTestDirectory( getProject() );
    }

    private File getTargetTestClassesDirectory()
    {
        return getTargetTestClassesDirectory( getProject() );
    }

    private File getTargetTestEnvDirectory()
    {
        return getTargetTestEnvDirectory( getProject() );
    }

    private File getTargetTestTempDirectory()
    {
        return getTargetTestTempDirectory( getProject() );
    }

    private boolean getDebugProperty()
    {
        return getBooleanProperty( DEBUG_KEY, DEBUG_FLAG );
    }

    private boolean getForkProperty()
    {
        return getBooleanProperty( FORK_KEY, FORK_FLAG );
    }

    private boolean getBooleanProperty( String key, boolean fallback )
    {
        String value = getProject().getProperty( key );
        if( null == value )
        {
            return fallback;
        }
        else
        {
            return getProject().toBoolean( value );
        }
    }
}
