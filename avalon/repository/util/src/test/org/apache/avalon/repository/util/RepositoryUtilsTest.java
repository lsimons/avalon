/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.avalon.repository.util ;

import java.net.URL;
import java.util.Properties;

import javax.naming.directory.Attributes;

import junit.framework.TestCase;

import org.apache.avalon.repository.Artifact;


/**
 * @todo
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: mcconnell $
 * @version $Revision: 1.1 $
 */
public class RepositoryUtilsTest extends TestCase
{

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(RepositoryUtilsTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Constructor for RepositoryUtilsTest.
     * @param arg0
     */
    public RepositoryUtilsTest(String arg0)
    {
        super(arg0);
    }
    
    
    final public void testGetAsAttributes() throws Exception
    {
        Properties l_props = 
            RepositoryUtils.getProperties( 
              new URL( 
                "http://www.ibiblio.org/maven/avalon-repository/propertiess/repository.properties" ) ) ;
        Attributes l_attrs = RepositoryUtils.getAsAttributes( l_props ) ;
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
            RepositoryUtils.getProperties( new URL( "http://www.ibiblio.org/maven/avalon-repository/propertiess/repository.properties" ) ) ;
        assertEquals( ".repository", l_props.getProperty( "cache.dir" ) ) ;
        assertEquals( "org.apache.avalon.repository.impl.DefaultFactory", 
                l_props.getProperty( "factory" ) ) ;
        assertEquals( "http://ibiblio.org/maven", 
                l_props.getProperty( "remote.repository.url.0" ) ) ;
    }


    final public void testIsEnumerated()
    {
        assertFalse( "false for empty string \"\"", 
                RepositoryUtils.isEnumerated( "" ) ) ;

        assertFalse( "false for \".\"", 
                RepositoryUtils.isEnumerated( "." ) ) ;
        
        assertFalse( "false for \"nodot\"", 
                RepositoryUtils.isEnumerated( "nodot" ) ) ;
        
        assertFalse( "false for \"before.\"", 
                RepositoryUtils.isEnumerated( "before." ) ) ;
        
        assertFalse( "false for \".after\"", 
                RepositoryUtils.isEnumerated( ".after" ) ) ;
        
        assertFalse( "false for \"123.\"", 
                RepositoryUtils.isEnumerated( "123." ) ) ;
        
        assertFalse( "false for \".123\"", 
                RepositoryUtils.isEnumerated( ".123" ) ) ;
        
        assertFalse( "false for \"123.asdf\"", 
                RepositoryUtils.isEnumerated( "123.asdf" ) ) ;
        
        assertTrue( "true for \"asdf.123\"", 
                RepositoryUtils.isEnumerated( "asdf.123" ) ) ;
        
        assertTrue( "true for \"asdf.1\"", 
                RepositoryUtils.isEnumerated( "asdf.1" ) ) ;
    }

    
    final public void testGetEnumeratedBase()
    {
        assertEquals( "", 
                RepositoryUtils.getEnumeratedBase( "" ) ) ;

        assertEquals( ".", 
                RepositoryUtils.getEnumeratedBase( "." ) ) ;
        
        assertEquals( "nodot", 
                RepositoryUtils.getEnumeratedBase( "nodot" ) ) ;
        
        assertEquals( "before.", 
                RepositoryUtils.getEnumeratedBase( "before." ) ) ;
        
        assertEquals( ".after", 
                RepositoryUtils.getEnumeratedBase( ".after" ) ) ;
        
        assertEquals( "123.", 
                RepositoryUtils.getEnumeratedBase( "123." ) ) ;
        
        assertEquals( ".123", 
                RepositoryUtils.getEnumeratedBase( ".123" ) ) ;
        
        assertEquals( "123.asdf", 
                RepositoryUtils.getEnumeratedBase( "123.asdf" ) ) ;
        
        assertEquals( "asdf", 
                RepositoryUtils.getEnumeratedBase( "asdf.123" ) ) ;
        
        assertEquals( "asdf", 
                RepositoryUtils.getEnumeratedBase( "asdf.1" ) ) ;
    }
    
    
    public void testGetDelimited() throws Exception
    {
        String [] l_processed = null ;
        assertNull( RepositoryUtils.getDelimited( ',', null) ) ;
        assertNull( RepositoryUtils.getDelimited( ',', "") ) ;

        l_processed = RepositoryUtils.getDelimited( ',',
                "asdf" ) ;
        assertEquals( "asdf", l_processed[0] ) ;

        l_processed = RepositoryUtils.getDelimited( ',',
                "asdf,1234" ) ;
        assertEquals( "asdf", l_processed[0] ) ;
        assertEquals( "1234", l_processed[1] ) ;
        
        l_processed = RepositoryUtils.getDelimited( ',',
                "asdf,1234,abcd" ) ;
        assertEquals( "asdf", l_processed[0] ) ;
        assertEquals( "1234", l_processed[1] ) ;
        assertEquals( "abcd", l_processed[2] ) ;
    }
}
