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

package org.apache.avalon.repository.meta;

import java.util.NoSuchElementException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.Attribute;
import javax.naming.NamingException;

import junit.framework.TestCase;


/**
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id$
 */
public class MetaTest extends TestCase
{

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run( MetaTest.class );
    }

    /**
     * Constructor for ArtifactReferenceTest.
     * @param arg0
     */
    public MetaTest( String name )
    {
        super( name );
    }
    
    public void testConstructor() throws Exception
    {
        try
        {
            ArtifactDescriptor meta = new ArtifactDescriptor( null );
            fail( "constructor using null should throw an NPE" );
        }
        catch( NullPointerException e )
        {
            assertTrue( true );
        }
        catch( Throwable e )
        {
            fail( "NPE expected by encountered: " + e );
        }
    }

    public void testEmptyAttribute() throws Exception
    {
        Attributes attributes = new BasicAttributes();
        try
        {
            ArtifactDescriptor meta = new ArtifactDescriptor( attributes );
            fail( "missing attributes should fail" );
        }
        catch( MetaException e )
        {
            assertTrue( true );
        }
        catch( Throwable e )
        {
            fail( "Unexpected error: " + e );
        }
    }

    public void testIntegrity() throws Exception
    {
        String domain = "aaa";
        String version = "123";

        Attributes attributes = new BasicAttributes();
        attributes.put( ArtifactDescriptor.DOMAIN_KEY, domain );
        attributes.put( ArtifactDescriptor.VERSION_KEY, version );

        try
        {
            ArtifactDescriptor meta = new ArtifactDescriptor( attributes );
            assertEquals( "domain", meta.getDomain(), domain );
            assertEquals( "version", meta.getVersion(), version );
            assertEquals( "equals", meta, meta );
        }
        catch( Throwable e )
        {
            fail( "unexpected error: " + e );
        }
    }
}
