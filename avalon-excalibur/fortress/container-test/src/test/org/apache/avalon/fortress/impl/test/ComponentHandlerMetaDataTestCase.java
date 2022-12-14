/* 
 * Copyright 2003-2004 The Apache Software Foundation
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

package org.apache.avalon.fortress.impl.test;

import junit.framework.TestCase;
import org.apache.avalon.fortress.impl.ComponentHandlerMetaData;
import org.apache.avalon.fortress.test.data.Component1;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

/**
 * ComponentHandlerMetaDataTestCase does XYZ
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $ Revision: 1.1 $
 */
public class ComponentHandlerMetaDataTestCase extends TestCase
{
    public ComponentHandlerMetaDataTestCase( String name )
    {
        super( name );
    }

    public void testMetaData()
    {
        ComponentHandlerMetaData meta = new ComponentHandlerMetaData( "component1",
            Component1.class.getName(), new DefaultConfiguration( "test" ), true );

        assertNotNull( meta );
        assertNotNull( meta.getClassname() );
        assertNotNull( meta.getConfiguration() );
        assertNotNull( meta.getName() );
        assertEquals( true, meta.isLazyActivation() );
        assertEquals( "component1", meta.getName() );
        assertEquals( Component1.class.getName(), meta.getClassname() );
        assertEquals( "test", meta.getConfiguration().getName() );
    }

    public void testNullPointerException()
    {
        try
        {
            new ComponentHandlerMetaData( null, Component1.class.getName(),
                new DefaultConfiguration( "test" ), false );

            fail( "No NullPointerException was thrown" );
        }
        catch ( NullPointerException npe )
        {
            // SUCCESS!!
        }
        catch ( Exception e )
        {
            fail( "Did not throw the correct exception: " + e.getClass().getName() );
        }

        try
        {
            new ComponentHandlerMetaData( "component1", null,
                new DefaultConfiguration( "test" ), false );

            fail( "No NullPointerException was thrown" );
        }
        catch ( NullPointerException npe )
        {
            // SUCCESS!!
        }
        catch ( Exception e )
        {
            fail( "Did not throw the correct exception: " + e.getClass().getName() );
        }

        try
        {
            new ComponentHandlerMetaData( "component1", Component1.class.getName(),
                null, false );

            fail( "No NullPointerException was thrown" );
        }
        catch ( NullPointerException npe )
        {
            // SUCCESS!!
        }
        catch ( Exception e )
        {
            fail( "Did not throw the correct exception: " + e.getClass().getName() );
        }
    }
}
