/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

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
package org.apache.avalon.meta.info.test;

import org.apache.avalon.meta.info.Descriptor;

import java.io.*;
import java.util.Properties;

import junit.framework.TestCase;


/**
 * AbstractDescriptorTestCase does XYZ
 *
 * @author <a href="bloritsch.at.apache.org">Berin Loritsch</a>
 * @version CVS $ Revision: 1.1 $
 */
public abstract class AbstractDescriptorTestCase extends TestCase
{
    protected static final String VALID_KEY = "key";
    protected static final String VALID_VALUE = "value";
    protected static final String INVALID_KEY = "bad-key";
    protected static final String DEFAULT_VALUE = "default";

    public AbstractDescriptorTestCase( String name )
    {
        super( name );
    }

    protected Properties getProperties()
    {
        Properties props = new Properties();
        props.put( VALID_KEY, VALID_VALUE );

        return props;
    }

    protected abstract Descriptor getDescriptor();

    protected void checkDescriptor( Descriptor desc )
    {
        assertEquals( VALID_VALUE, desc.getAttribute( VALID_KEY ) );
        assertEquals( DEFAULT_VALUE, desc.getAttribute( INVALID_KEY, DEFAULT_VALUE ) );

        boolean hasValid = false;
        boolean hasInvalid = false;
        String[] names = desc.getAttributeNames();

        assertNotNull( names );
        assertTrue( names.length > 0 );

        for ( int i = 0; i < names.length; i++ )
        {
            if ( VALID_KEY.equals( names[i] ) ) hasValid = true;
            if ( INVALID_KEY.equals( names[i] ) ) hasInvalid = true;
        }

        assertTrue( hasValid );
        assertTrue( !hasInvalid );
    }

    public void testSerialization() throws IOException, ClassNotFoundException
    {
        Descriptor desc = getDescriptor();
        checkDescriptor( desc );

        File file = new File( "test.file" );
        ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream( file ) );
        oos.writeObject( desc );
        oos.close();

        ObjectInputStream ois = new ObjectInputStream( new FileInputStream( file ) );
        Descriptor serialized = (Descriptor) ois.readObject();
        ois.close();
        file.delete();

        assertTrue( desc != serialized ); // Ensure this is not the same instance
        checkDescriptor( serialized );

        assertEquals( desc, serialized );
        assertEquals( desc.hashCode(), serialized.hashCode() );
    }
}