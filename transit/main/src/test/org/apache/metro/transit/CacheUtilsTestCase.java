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

import java.net.URL;
import java.util.Properties;

import javax.naming.directory.Attributes;

import junit.framework.TestCase;

/**
 * @todo
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: CacheUtilsTest.java 30977 2004-07-30 08:57:54Z niclas $
 */
public class CacheUtilsTestCase extends TestCase
{
    /**
     * Constructor for CacheUtilsTest.
     * @param arg0
     */
    public CacheUtilsTestCase( String arg )
    {
        super( arg );
    }
    
    
    final public void testGetAsAttributes() throws Exception
    {
        Properties l_props = 
            CacheUtils.getProperties( 
              new URL( "@TEST_PROPERTIES_URL@" ) ) ;
        Attributes l_attrs = CacheUtils.getAsAttributes( l_props ) ;
        assertEquals( ".repository", l_attrs.get( "cache.dir" ).get( 0 ) ) ; 

        assertEquals( "org.apache.avalon.repository.impl.DefaultFactory", 
                l_attrs.get( "factory" ).get( 0 ) ) ;
        assertEquals( "http://ibiblio.org/maven", 
                l_attrs.get( "remote.repository.url" ).get( 0 ) ) ;

        assertNotNull( l_attrs.get( "url" ).get( 0 ) ) ;
        assertNotNull( l_attrs.get( "url" ).get( 1 ) ) ;
        assertNotNull( l_attrs.get( "url" ).get( 2 ) ) ;
    }

    final public void testGetProperties() throws Exception
    {
        /* Test for these properties
         * cache.dir=.repository
         * factory=org.apache.avalon.repository.impl.DefaultFactory
         * remote.repository.url.0=http://ibiblio.org/maven 
         */
        Properties l_props = 
            CacheUtils.getProperties( new URL( "@TEST_PROPERTIES_URL@"  ) ) ;
        assertEquals( ".repository", l_props.getProperty( "cache.dir" ) ) ;
        assertEquals( "org.apache.avalon.repository.impl.DefaultFactory", 
                l_props.getProperty( "factory" ) ) ;
        assertEquals( "http://ibiblio.org/maven", 
                l_props.getProperty( "remote.repository.url.0" ) ) ;
    }


    final public void testIsEnumerated()
    {
        assertFalse( "false for empty string \"\"", 
                CacheUtils.isEnumerated( "" ) ) ;

        assertFalse( "false for \".\"", 
                CacheUtils.isEnumerated( "." ) ) ;
        
        assertFalse( "false for \"nodot\"", 
                CacheUtils.isEnumerated( "nodot" ) ) ;
        
        assertFalse( "false for \"before.\"", 
                CacheUtils.isEnumerated( "before." ) ) ;
        
        assertFalse( "false for \".after\"", 
                CacheUtils.isEnumerated( ".after" ) ) ;
        
        assertFalse( "false for \"123.\"", 
                CacheUtils.isEnumerated( "123." ) ) ;
        
        assertFalse( "false for \".123\"", 
                CacheUtils.isEnumerated( ".123" ) ) ;
        
        assertFalse( "false for \"123.asdf\"", 
                CacheUtils.isEnumerated( "123.asdf" ) ) ;
        
        assertTrue( "true for \"asdf.123\"", 
                CacheUtils.isEnumerated( "asdf.123" ) ) ;
        
        assertTrue( "true for \"asdf.1\"", 
                CacheUtils.isEnumerated( "asdf.1" ) ) ;
    }

    
    final public void testGetEnumeratedBase()
    {
        assertEquals( "", 
                CacheUtils.getEnumeratedBase( "" ) ) ;

        assertEquals( ".", 
                CacheUtils.getEnumeratedBase( "." ) ) ;
        
        assertEquals( "nodot", 
                CacheUtils.getEnumeratedBase( "nodot" ) ) ;
        
        assertEquals( "before.", 
                CacheUtils.getEnumeratedBase( "before." ) ) ;
        
        assertEquals( ".after", 
                CacheUtils.getEnumeratedBase( ".after" ) ) ;
        
        assertEquals( "123.", 
                CacheUtils.getEnumeratedBase( "123." ) ) ;
        
        assertEquals( ".123", 
                CacheUtils.getEnumeratedBase( ".123" ) ) ;
        
        assertEquals( "123.asdf", 
                CacheUtils.getEnumeratedBase( "123.asdf" ) ) ;
        
        assertEquals( "asdf", 
                CacheUtils.getEnumeratedBase( "asdf.123" ) ) ;
        
        assertEquals( "asdf", 
                CacheUtils.getEnumeratedBase( "asdf.1" ) ) ;
    }
    
    
    public void testGetDelimited() throws Exception
    {
        String [] l_processed = null ;
        assertNull( CacheUtils.getDelimited( ',', null) ) ;
        assertNull( CacheUtils.getDelimited( ',', "") ) ;

        l_processed = CacheUtils.getDelimited( ',',
                "asdf" ) ;
        assertEquals( "asdf", l_processed[0] ) ;

        l_processed = CacheUtils.getDelimited( ',',
                "asdf,1234" ) ;
        assertEquals( "asdf", l_processed[0] ) ;
        assertEquals( "1234", l_processed[1] ) ;
        
        l_processed = CacheUtils.getDelimited( ',',
                "asdf,1234,abcd" ) ;
        assertEquals( "asdf", l_processed[0] ) ;
        assertEquals( "1234", l_processed[1] ) ;
        assertEquals( "abcd", l_processed[2] ) ;
    }
}
