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

package org.apache.metro.transit;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.naming.directory.Attributes;

import junit.framework.TestCase;

/**
 * StandardLoaderArtifactTestCase 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: CacheUtilsTest.java 30977 2004-07-30 08:57:54Z niclas $
 */
public class StandardLoaderArtifactTestCase extends TestCase
{
    private File m_dir = new File( System.getProperty( "project.dir" ) );
    private File m_cache = new File( System.getProperty( "project.repository.cache.path" ) );
    private File m_root = new File( m_dir, "root" );

    private Artifact m_artifact = 
       Artifact.createArtifact( "@CACHE_MAIN_URI@" );

    public StandardLoaderArtifactTestCase( String arg )
    {
        super( arg );
    }

    protected void setUp() throws Exception
    {
        m_root.mkdirs();
    }

    /*
    public void testOffline() throws Exception
    {
        Repository repo = createRepository( false );
        try
        {
            URL local = repo.getResource( m_artifact );
            fail( "Offline mode did not fail." );
        }
        catch( IOException ce )
        {
            assertTrue( "offline fail expected", true );
        }
        assertTrue( true );
    }
    */

    public void testOnline() throws Exception
    {
        Repository repo = createRepository( true );
        URL local = repo.getResource( m_artifact );
        File target = new File( m_root, m_artifact.getPath() );
        assertTrue( "loading classic", target.exists() );
    }

    private Repository createRepository( boolean online ) throws IOException
    {
        String remote = m_cache.toURL().toString();
        Monitor monitor = new ConsoleMonitor( false );   
        InitialContext context = 
          new DefaultInitialContext( 
            monitor, m_root, new String[]{ remote  }, online, Policy.FAST, false );
        return new StandardLoader( context );
    }
}
