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
import org.apache.tools.ant.taskdefs.Mkdir;
import org.apache.tools.ant.taskdefs.optional.junit.FormatterElement;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTask;
import org.apache.tools.ant.taskdefs.optional.junit.BatchTest;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import org.apache.avalon.tools.home.Home;
import org.apache.avalon.tools.project.Definition;
import org.apache.avalon.tools.home.Context;

/**
 * Load a goal. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class JUnitTestTask extends SystemTask
{
    public static final String TEST_KEY = "project.test";
    public static final String TEST_VALUE = "test";

    public static final String TEST_SRC_KEY = "project.test.src";
    public static final String TEST_SRC_VALUE = "test";

    public static final String TEST_ENV_KEY = "project.test.env";
    public static final String TEST_ENV_VALUE = "env";

    public static final String TEST_TMP_KEY = "project.test.temp";
    public static final String TEST_TMP_VALUE = "temp";

    public static final String DEBUG_KEY = "project.test.compile.debug";
    public static final boolean DEBUG_VALUE = true;

    public static final String FORK_KEY = "project.test.compile.fork";
    public static final boolean FORK_VALUE = false;

    public static final String HALT_ON_ERROR_KEY = "project.test.halt-on-error";
    public static final boolean HALT_ON_ERROR_VALUE = false;

    public static final String HALT_ON_FAILURE_KEY = "project.test.halt-on-failure";
    public static final boolean HALT_ON_FAILURE_VALUE = true;

    private static final String ERROR_KEY = "project.test.error";
    private static final String FAILURE_KEY = "project.test.failure";

    private File m_test;

    public void init() throws BuildException 
    {
        if( !isInitialized() )
        {
            super.init();
            Project project = getProject();
            project.setNewProperty( DEBUG_KEY, "" + DEBUG_VALUE );
            project.setNewProperty( FORK_KEY, "" + FORK_VALUE );
            project.setNewProperty( TEST_SRC_KEY, "" + TEST_SRC_VALUE );
            project.setNewProperty( TEST_ENV_KEY, "" + TEST_ENV_VALUE );
            project.setNewProperty( TEST_TMP_KEY, "" + TEST_TMP_VALUE );
            project.setNewProperty( HALT_ON_ERROR_KEY, "" + HALT_ON_ERROR_VALUE );
            project.setNewProperty( HALT_ON_FAILURE_KEY, "" + HALT_ON_FAILURE_VALUE );
            getContext().setBuildPath( TEST_KEY, TEST_VALUE );
            m_test = getContext().getBuildPath( TEST_KEY );
        }
    }

    public void execute() throws BuildException 
    {
        Project project = getProject();
        File build = getContext().getBuildDirectory();

        String testPath = project.getProperty( TEST_SRC_KEY );
        File src = new File( build, testPath );

        if( src.exists() )
        {
            File classes = new File( m_test, "classes" );
            mkDir( classes );
            Definition definition = getHome().getDefinition( getKey() );
            Path classpath = 
              getHome().getRepository().createPath( project, definition );

            //
            // add the project jar to the classpath for the compilation
            // of the test classes and compile the test classes into the 
            // target/test-classes directory
            //

            File jar = getContext().getBuildPath( "jar" );
            classpath.createPathElement().setLocation( jar );
            compile( src, classes, classpath );
            copyCompileResource( src, classes );
            classpath.createPathElement().setLocation( classes );

            //
            // setup test resources
            //

            File temp = new File( m_test, "temp" );
            mkDir( temp );
            copyUnitTestResource( temp );
            test( src, classpath, temp );
        }

        System.out.println( 
           "error: [" 
           + project.getProperty( ERROR_KEY ) 
           + "]" ); 
        System.out.println( 
           "failure: [" 
           + project.getProperty( FAILURE_KEY ) 
           + "]" );
    }

    private void copyUnitTestResource( File dest )
    {
        File build = getContext().getBuildDirectory();
        File src = getUnitTestResourcesDirectory( build );
        if( src.exists() )
        {
            mkDir( dest );
            Copy copy = (Copy) getProject().createTask( "copy" );
            copy.setPreserveLastModified( true );
            copy.setTodir( dest );

            FileSet fileset = new FileSet();
            fileset.setDir( src );
            copy.addFileset( fileset );
            copy.init();
            copy.execute();
        }
    }

    private File getUnitTestResourcesDirectory( File build )
    {
        File etc = new File( build, "etc" );
        File test = new File( etc, "test" );
        File unit = new File( test, "unit" );
        return unit;
    }

    private void copyCompileResource( File src, File classes )
    {
        Copy copy = (Copy) getProject().createTask( "copy" );
        copy.setPreserveLastModified( true );
        copy.setTodir( classes );

        FileSet fileset = new FileSet();
        fileset.setDir( src );
        fileset.setIncludes( "**/**" );
        fileset.setExcludes( "**/*.java,**/package.html" );
        copy.addFileset( fileset );
        copy.init();
        copy.execute();
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

    private void test( File src, Path classpath, File base )
    {
        Project project = getProject();

        FileSet fileset = new FileSet();
        fileset.setDir( src );
        fileset.createInclude().setName( "**/*TestCase.java" );
        fileset.createExclude().setName( "**/Abstract*.java" );

        JUnitTask junit = (JUnitTask) getProject().createTask( "junit" );
        junit.init();

        junit.setErrorProperty( ERROR_KEY );
        junit.setFailureProperty( FAILURE_KEY );

        JUnitTask.SummaryAttribute summary = new JUnitTask.SummaryAttribute();
        summary.setValue( "on" );
        junit.setPrintsummary( summary );
        if( getForkProperty() )
        {
            junit.setFork( true );
            junit.setDir( base );
        }
        junit.setShowOutput( true );
        junit.setTempdir( base );
        junit.setReloading( true );
        junit.setFiltertrace( true );
        junit.createClasspath().add( classpath );
        junit.setHaltonerror( 
          getBooleanProperty( 
            HALT_ON_ERROR_KEY, HALT_ON_ERROR_VALUE ) );
        junit.setHaltonfailure( 
          getBooleanProperty( 
            HALT_ON_FAILURE_KEY, HALT_ON_FAILURE_VALUE ) );

        File reports = new File( m_test, "reports" );
        mkDir( reports );

        BatchTest batch = (BatchTest) junit.createBatchTest();
        batch.addFileSet( fileset );
        batch.setTodir( reports );

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
        basedir.setValue( base.toString() );
        junit.addSysproperty( basedir );

        junit.execute();
    }

    private boolean getDebugProperty()
    {
        return getBooleanProperty( DEBUG_KEY, DEBUG_VALUE );
    }

    private boolean getForkProperty()
    {
        return getBooleanProperty( FORK_KEY, FORK_VALUE );
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
