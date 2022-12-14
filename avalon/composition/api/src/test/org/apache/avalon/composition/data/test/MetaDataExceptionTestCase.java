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

package org.apache.avalon.composition.data.test;

import junit.framework.TestCase;
import org.apache.avalon.composition.data.MetaDataException;

/**
 * MetaDataExceptionTestCase does XYZ
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $ Revision: 1.1 $
 */
public class MetaDataExceptionTestCase extends TestCase
{
    public MetaDataExceptionTestCase( String name )
    {
        super( name );
    }

    public void testMetaDataException()
    {
        String message = "Original Message";
        Exception parent = new Exception("Parent Exception");
        MetaDataException me = new MetaDataException( message );

        assertNotNull( me );
        assertNull( me.getCause() );
        assertEquals( message, me.getMessage() );

        me = new MetaDataException( message, parent );

        assertNotNull( me );
        assertEquals( parent, me.getCause() );
        assertEquals( message, me.getMessage() );
    }
}