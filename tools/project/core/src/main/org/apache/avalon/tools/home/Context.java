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

package org.apache.avalon.tools.home;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Property;

/**
 * Organization descriptor.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class Context 
{
    public static final String KEY = "project.context";

    public static final String SRC_KEY = "project.src";
    public static final String SRC_VALUE = "src";

    public static final String SRC_MAIN_KEY = "project.src.main";
    public static final String SRC_MAIN_VALUE = "main";

    public static final String SRC_CONFIG_KEY = "project.src.config";
    public static final String SRC_CONFIG_VALUE = "config";

    public static final String SRC_TEST_KEY = "project.src.test";
    public static final String SRC_TEST_VALUE = "test";

    public static final String TARGET_KEY = "project.target";
    public static final String TARGET_VALUE = "target";

    private static final String USER_PROPERTIES = "user.properties";
    private static final String BUILD_PROPERTIES = "build.properties";

    public static final String DELIVERABLES_KEY = "project.target.deliverables";
    private static final String DELIVERABLES_VALUE = "deliverables";

    public static final String BUILD_KEY = "project.target.build";
    private static final String BUILD_VALUE = "build";

    public static final String TEMP_KEY = "project.target.temp";
    private static final String TEMP_VALUE = "temp";

    public static final String DOCS_KEY = "project.target.docs";
    private static final String DOCS_VALUE = "docs";

    public static Context getContext( Project project )
    {
        Context context = (Context) project.getReference( KEY );
        if( null == context )
        {
            context = new Context( project );
            project.addReference( KEY, context );
        }
        return context;
    }

    private final File m_src;
    private final File m_target;
    private final File m_build;
    private final File m_deliverables;
    private final File m_temp;
    private final File m_docs;
    
    private final Map m_map = new Hashtable();
    private final Map m_resources = new Hashtable();

    private Context( Project project )
    {
        setupProperties( project );

        project.setNewProperty( SRC_KEY, SRC_VALUE );
        project.setNewProperty( SRC_MAIN_KEY, SRC_MAIN_VALUE );
        project.setNewProperty( SRC_CONFIG_KEY, SRC_CONFIG_VALUE );
        project.setNewProperty( SRC_TEST_KEY, SRC_TEST_VALUE );
        project.setNewProperty( TARGET_KEY, TARGET_VALUE );
        project.setNewProperty( BUILD_KEY, BUILD_VALUE );
        project.setNewProperty( DELIVERABLES_KEY, DELIVERABLES_VALUE );
        project.setNewProperty( DOCS_KEY, DOCS_VALUE );
        project.setNewProperty( TEMP_KEY, TEMP_VALUE );

        File basedir = project.getBaseDir();
        String src = project.getProperty( SRC_KEY );
        String target = project.getProperty( TARGET_KEY );
        String build = project.getProperty( BUILD_KEY );
        String temp = project.getProperty( TEMP_KEY );
        String docs = project.getProperty( DOCS_KEY );
        String deliverables = project.getProperty( DELIVERABLES_KEY );

        m_src = setupSrc( basedir, src );
        m_target = setupTarget( basedir, target );

        m_build = 
          setBuildPath( BUILD_KEY, build );
        m_deliverables = 
          setBuildPath( DELIVERABLES_KEY, deliverables );
        m_temp = 
          setBuildPath( TEMP_KEY, temp );
        m_docs = 
          setBuildPath( DOCS_KEY, docs );
    }

    public File getSrcDirectory()
    {
        return m_src;
    }

    public File getTargetDirectory()
    {
        return m_target;
    }

    public File getBuildDirectory()
    {
        return m_build;
    }

    public File getDeliverablesDirectory()
    {
        return m_deliverables;
    }

    public File getTempDirectory()
    {
        return m_temp;
    }

    public File getDocsDirectory()
    {
        return m_docs;
    }


    public File setBuildPath( String key, String path )
    {
        if( null == key )
        {
            throw new NullPointerException( "key" );
        }
        if( null == path )
        {
            throw new NullPointerException( "null path for key: " + key );
        }
        if( m_map.containsKey( key ) )
        {
            final String error = 
              "Duplicate path registration request for key '" 
              + key + "'.";
            throw new BuildException( error );
        }
        File build = getFile( m_target, path );
        m_map.put( key, build );
        return build;
    }

    public File getBuildPath( String key )
    {
        return getBuildPath( key, true );
    }

    public File getBuildPath( String key, boolean fail )
    {
        if( m_map.containsKey( key ) )
        {
            return (File) m_map.get( key ) ;
        }
        else if( fail )
        {
            final String error = 
              "Unknown build key '" 
              + key + "'.";
            throw new BuildException( error );
        }
        else
        {
            return null;
        }
    }

    public File getTargetDirectory( String path )
    {
        return new File( m_target, path );
    }

    private File setupSrc( File basedir, String path )
    {
        if( null == path ) return new File( basedir, SRC_VALUE );
        return new File( basedir, path );
    }

    private File setupTarget( File basedir, String path )
    {
        if( null == path ) return new File( basedir, TARGET_VALUE );
        return new File( basedir, path );
    }

    private void setupProperties( Project project )
    {
        File basedir = project.getBaseDir();
        setupUserProperties( project, basedir );
        setupBuildProperties( project, basedir );
    }

    private void setupUserProperties( Project project, File basedir )
    {
        File user = new File( basedir, USER_PROPERTIES );
        readProperties( project, user );
    }

    private void setupBuildProperties( Project project, File basedir )
    {
        File build = new File( basedir, BUILD_PROPERTIES );
        readProperties( project, build );
    }

    private void readProperties( Project project, File file ) throws BuildException 
    {
        Property props = (Property) project.createTask( "property" );
        props.setFile( file );
        props.init();
        props.execute();
    }

    public static File getFile( File root, String path )
    {
        File file = new File( path );
        if( file.isAbsolute() ) return file;
        if( null == root )
        {
            throw new NullPointerException( "root" );
        }
        return new File( root, path );
    }
}
