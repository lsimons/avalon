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
import org.apache.avalon.framework.Version;
import org.apache.avalon.meta.info.ReferenceDescriptor;

import java.io.*;

/**
 * ReferenceDescriptorTestCase does XYZ
 *
 * @author <a href="bloritsch.at.apache.org">Berin Loritsch</a>
 * @version CVS $ Revision: 1.1 $
 */
public class ReferenceDescriptorTestCase extends TestCase
{
    private static final String m_classname = ReferenceDescriptorTestCase.class.getName();
    private static final Version m_version = Version.getVersion("1.2.3");

    public ReferenceDescriptorTestCase( String name )
    {
        super( name );
    }

    public void testConstructor()
    {
        try
        {
            new ReferenceDescriptor(null, m_version);
            fail("Did not throw the expected NullPointerException");
        }
        catch (NullPointerException npe)
        {
            // Success!
        }

        try
        {
            new ReferenceDescriptor( null );
            fail( "Did not throw the expected NullPointerException" );
        }
        catch ( NullPointerException npe )
        {
            // Success!
        }

        ReferenceDescriptor ref = new ReferenceDescriptor( m_classname, m_version );
        checkDescriptor( ref, m_classname, m_version );

        ref = new ReferenceDescriptor( m_classname + ":3.2.1" );
        checkDescriptor( ref, m_classname, Version.getVersion("3.2.1"));
    }

    private void checkDescriptor(ReferenceDescriptor ref, String classname, Version version)
    {
        assertNotNull(ref);
        assertNotNull(ref.getClassname());
        assertEquals(classname, ref.getClassname());
        assertNotNull(ref.getVersion());
        assertEquals(version, ref.getVersion());
    }

    public void testCompliance()
    {
        ReferenceDescriptor ref = new ReferenceDescriptor( m_classname, m_version );
        ReferenceDescriptor any = new ReferenceDescriptor( m_classname, new Version( -1, 0, 0 ) );

        assertTrue( "anything matches explicit", any.matches( ref ) );
        assertFalse( "explicit does not match anything", ref.matches( any ) );
    }

    public void testSerialization() throws IOException, ClassNotFoundException
    {
        ReferenceDescriptor entry = new ReferenceDescriptor( m_classname, m_version );
        checkDescriptor( entry, m_classname, m_version );

        File file = new File( "test.out" );
        ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream( file ) );
        oos.writeObject( entry );
        oos.close();

        ObjectInputStream ois = new ObjectInputStream( new FileInputStream( file ) );
        ReferenceDescriptor serialized = (ReferenceDescriptor) ois.readObject();
        ois.close();
        file.delete();

        checkDescriptor( entry, m_classname, m_version );

        assertEquals( entry, serialized );
        assertEquals( entry.hashCode(), serialized.hashCode() );
    }
}