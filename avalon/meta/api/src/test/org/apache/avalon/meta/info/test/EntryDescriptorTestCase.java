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

import junit.framework.TestCase;
import org.apache.avalon.meta.info.EntryDescriptor;

import java.io.*;

/**
 * EntryDescriptorTestCase does XYZ
 *
 * @author <a href="bloritsch.at.apache.org">Berin Loritsch</a>
 * @version CVS $ Revision: 1.1 $
 */
public class EntryDescriptorTestCase extends TestCase
{
    private static final String m_key = "key";
    private static final String m_alias = "otherVal";
    private static final String m_type = EntryDescriptor.class.getName();
    private static final boolean m_optional = true;
    private static final boolean m_volatile = true;

    public EntryDescriptorTestCase( String name )
    {
        super( name );
    }

    public void testEntryDescriptor()
    {
        EntryDescriptor entry = new EntryDescriptor(m_key, m_type, m_optional, m_volatile, m_alias);
        checkEntry(entry, m_key, m_type, m_optional, m_volatile, m_alias );

        entry = new EntryDescriptor(m_key, m_type);
        checkEntry(entry, m_key, m_type, false, false, null );

        entry = new EntryDescriptor(m_key, m_type, m_optional);
        checkEntry(entry, m_key, m_type, m_optional, false, null );

        entry = new EntryDescriptor( m_key, m_type, m_optional, m_volatile );
        checkEntry( entry, m_key, m_type, m_optional, m_volatile, null );

        try
        {
            new EntryDescriptor(null, m_type);
            fail("Did not throw expected NullPointerException");
        }
        catch(NullPointerException npe)
        {
            // Success!!
        }

        try
        {
            new EntryDescriptor( m_key, null );
            fail( "Did not throw expected NullPointerException" );
        }
        catch ( NullPointerException npe )
        {
            // Success!!
        }
    }

    private void checkEntry(EntryDescriptor desc, String key, String type, boolean isOptional, boolean isVolatile, String alias)
    {
        assertNotNull( desc );
        assertEquals( key, desc.getKey() );
        if( alias == null )
        {
            assertEquals( key, desc.getAlias() );
        }
        else
        {
            assertEquals( alias, desc.getAlias() );
        }
        assertEquals( type, desc.getClassname() );
        assertEquals( isOptional, desc.isOptional() );
        assertEquals( ! isOptional, desc.isRequired() );
        assertEquals( isVolatile, desc.isVolatile() );
    }

    public void testSerialization() throws IOException, ClassNotFoundException
    {
        EntryDescriptor entry = new EntryDescriptor( m_key, m_type, m_optional, m_volatile, m_alias );
        checkEntry( entry, m_key, m_type, m_optional, m_volatile, m_alias );

        File file = new File( "test.out" );
        ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream( file ) );
        oos.writeObject( entry );
        oos.close();

        ObjectInputStream ois = new ObjectInputStream( new FileInputStream( file ) );
        EntryDescriptor serialized = (EntryDescriptor) ois.readObject();
        ois.close();
        file.delete();

        checkEntry( serialized, m_key, m_type, m_optional, m_volatile, m_alias );

        assertEquals( entry, serialized );
        assertEquals( entry.hashCode(), serialized.hashCode() );
    }
}