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

package org.apache.avalon.fortress.test;

import junit.framework.TestCase;
import org.apache.avalon.fortress.InitializationException;

/**
 * InitializationExceptionTestCase does XYZ
 *
 * @author <a href="bloritsch.at.apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.4 $
 */
public class InitializationExceptionTestCase extends TestCase
{
    public InitializationExceptionTestCase( String name )
    {
        super( name );
    }

    public void testRegularCreation()
    {
        InitializationException exc = new InitializationException( "Message" );
        assertNotNull( exc );
        assertNotNull( exc.getMessage() );
        assertNotNull( exc.getLocalizedMessage() );
        assertEquals( "Message", exc.getMessage() );
        assertTrue( null == exc.getCause() );
    }

    public void testNestedCreation()
    {
        Exception nest = new RuntimeException();
        InitializationException exc = new InitializationException( "Message", nest );
        assertNotNull( exc );
        assertNotNull( exc.getCause() );
        assertNotNull( exc.getMessage() );
        assertNotNull( exc.getLocalizedMessage() );
        assertEquals( "Message", exc.getMessage() );
        assertEquals( nest, exc.getCause() );
        assertSame( nest, exc.getCause() );
    }
}
