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

package org.apache.avalon.composition.model.impl.fileset;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.avalon.composition.data.FilesetDirective;
import org.apache.avalon.composition.data.IncludeDirective;
import org.apache.avalon.composition.data.ExcludeDirective;
import org.apache.avalon.composition.model.impl.DefaultFilesetModel;
import org.apache.avalon.framework.logger.ConsoleLogger;

public class FilesetModelTestCase extends TestCase
{
    private DefaultFilesetModel m_model;
    private File m_root;

    public void setUp()
    {
        m_root = new File( System.getProperty( "basedir" ) );
        ConsoleLogger logger = new ConsoleLogger( ConsoleLogger.LEVEL_INFO );
        m_model = new DefaultFilesetModel( logger );
    }

    public void tearDown() throws Exception
    {
        m_model = null;
    }

    public void testBadBaseDirectory() throws Exception
    {
        // only testing a bad base directory -- no includes or excludes necessary
        IncludeDirective[] includes = new IncludeDirective[0];
        ExcludeDirective[] excludes = new ExcludeDirective[0];

        // make up a *bad* fileset directory attribute
        FilesetDirective fsd = new FilesetDirective( "junk", includes, excludes );

        // set the fileset model's anchor directory and set of includes/excludes
        File anchor = new File( m_root, fsd.getBaseDirectory() );
        m_model.setBaseDirectory( anchor );
        m_model.setIncludeDirectives( fsd.getIncludes() );
        m_model.setExcludeDirectives( fsd.getExcludes() );
        m_model.setDefaultIncludes( null );
        m_model.setDefaultExcludes( null );

        // do the test...
        try
        {
        	m_model.resolveFileset();
            fail("The test did not fail with an IllegalStateException");
        }
        catch( IllegalStateException ise )
        {
            // success
        }
        catch( IOException ioe )
        {
            fail("The exception thrown was an " + ioe.getClass().getName() );
        }
        catch( Exception e )
        {
            fail("The exception thrown was " + e.getClass().getName() );
        }
    }
}
