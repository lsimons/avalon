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

import junit.framework.TestCase;


/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: CacheUtilsTest.java 30977 2004-07-30 08:57:54Z niclas $
 */
public class CacheExceptionTestCase extends TestCase
{

    /**
     * Constructor for CacheExceptionTestCase.
     * @param arg0
     */
    public CacheExceptionTestCase( String arg )
    {
        super( arg );
    }
    
    final public void testMessageConstructor() throws Exception
    {
        try
        {
            CacheException exception = new CacheException( (String) null );
            fail( "NPE not thrown on null subject." );
        }
        catch( NullPointerException npe )
        {
            assertTrue( "NPE captured on null subject.", true );
        }
    }

    final public void testThrowableConstructor() throws Exception
    {
        final String message = "message";
        Throwable cause = new Exception( "abc" );
        CacheException exception = new CacheException( message, cause );
        assertEquals( "exception message", message, exception.getMessage() );
        assertEquals( "exception cause", cause, exception.getCause() );
    }

}
