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
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Property;

/**
 * Organization descriptor.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class Context extends Task
{
    public static final String TARGET = "target";
    public static final String BUILD = "build";
    public static final String TEMP = "temp";
    public static final String TEST = "test";
    public static final String DELIVERABLES = "deliverables";
    public static final String DOCS = "docs";

    private static final String USER_PROPERTIES = "user.properties";
    private static final String BUILD_PROPERTIES = "build.properties";

    public static final String KEY = "project.context";

    public static final String SRC_KEY = "project.src";
    public static final String SRC_VALUE = "src";

    public static final String SRC_MAIN = "main";
    public static final String SRC_MAIN_KEY = "project.src.main";

    public static final String SRC_CONFIG_KEY = "project.src.config";
    public static final String SRC_CONFIG = "config";

    public static final String SRC_TEST_KEY = "project.src.test";
    public static final String SRC_TEST = "test";

    public static final String ETC_KEY = "project.etc";
    public static final String ETC_VALUE = "etc";

    public static Context getContext( Project project )
    {
        if( null == project )
        {
            throw new NullPointerException( "project" );
        }

        Context context = (Context) project.getReference( KEY );
        if( null == context )
        {
            context = new Context();
            context.setProject( project );
            context.init();
            context.execute();
            project.addReference( KEY, context );
        }
        return context;
    }

    private final Map m_map = new Hashtable();
    private final Map m_resources = new Hashtable();

    private String m_key;
    private File m_file;

    private File m_src;
    private File m_etc;

    private File m_target;
    private File m_build;
    private File m_deliverables;
    private File m_temp;
    private File m_docs;
    private File m_test;
    
    public void setKey( String key )
    {
        m_key = key;
    }

    public void setFile( File file )
    {
        m_file = file;
    }

    public void execute()
    {
        Project project = getProject();
        setupProperties( project );

        project.setNewProperty( SRC_KEY, SRC_VALUE );
        project.setNewProperty( SRC_MAIN_KEY, SRC_MAIN );
        project.setNewProperty( SRC_CONFIG_KEY, SRC_CONFIG );
        project.setNewProperty( SRC_TEST_KEY, SRC_TEST );

        project.setNewProperty( ETC_KEY, ETC_VALUE );

        File basedir = project.getBaseDir();
        String src = project.getProperty( SRC_KEY );
        String etc = project.getProperty( ETC_KEY );

        m_src = setupSrc( basedir, src );
        m_etc = setupEtc( basedir, etc );

        m_target = new File( basedir, TARGET );
        m_build = setBuildPath( BUILD );
        m_deliverables = setBuildPath( DELIVERABLES );
        m_temp = setBuildPath( TEMP );
        m_test = setBuildPath( TEST );
        m_docs = setBuildPath( DOCS );

        project.addReference( KEY, this );
    }

    public String getKey()
    {
        if( null != m_key )
        {
            return m_key;
        }
        else
        {
            String name = getProject().getProperty( "project.name" );
            if( null != name )
            {
                return name;            
            }
            else
            {
                return getProject().getName();
            }
        }
    }

    public File getSrcDirectory()
    {
        return m_src;
    }

    public File getEtcDirectory()
    {
        return m_etc;
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

    public File getTestDirectory()
    {
        return m_test;
    }

    public File getDocsDirectory()
    {
        return m_docs;
    }

    public File setBuildPath( String path )
    {
        return setBuildPath( path, path );
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

    private File setupEtc( File basedir, String path )
    {
        if( null == path ) return new File( basedir, ETC_VALUE );
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
        File user = Context.getFile( basedir, USER_PROPERTIES );
        readProperties( project, user );
    }

    private void setupBuildProperties( Project project, File basedir )
    {
        if( null == m_file )
        {  
            File build = Context.getFile( basedir, BUILD_PROPERTIES );
            readProperties( project, build );
        }
        else
        {
            readProperties( project, m_file );
        }
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
        if( null == path )
        {
            throw new NullPointerException( "path" );
        }
        File file = new File( path );
        if( file.isAbsolute() ) return getCanonicalFile( file );
        if( null == root )
        {
            throw new NullPointerException( "root" );
        }
        return getCanonicalFile( new File( root, path ) );
    }

    public static File getCanonicalFile( File file ) throws BuildException
    {
        try
        {
            return file.getCanonicalFile();
        }
        catch( IOException ioe )
        {
            throw new BuildException( ioe );
        }
    }


    public static String getSignature()
    {
        return getSignature( new Date() );
    }

    public static String getSignature( Date date )
    {
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd.HHmmss" );
        sdf.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
        return sdf.format( date );
    }

}
