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

package org.apache.avalon.meta.info.test;

import org.apache.avalon.meta.info.Descriptor;
import org.apache.avalon.meta.info.Service;
import org.apache.avalon.meta.info.ReferenceDescriptor;
import org.apache.avalon.meta.info.EntryDescriptor;
import org.apache.avalon.framework.Version;

/**
 * ServiceTestCase does XYZ
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $ Revision: 1.1 $
 */
public class ServiceTestCase extends AbstractDescriptorTestCase
{
    private ReferenceDescriptor m_reference;
    private EntryDescriptor[] m_entries;

    public ServiceTestCase( String name )
    {
        super( name );
    }

    protected Descriptor getDescriptor()
    {
        return new Service(m_reference, m_entries, getProperties());
    }

    public void setUp()
    {
        m_reference = new ReferenceDescriptor(ServiceTestCase.class.getName(), Version.getVersion("1.2.3"));
        m_entries = new EntryDescriptor[] {
            new EntryDescriptor("key", String.class.getName())
        };
    }

    public void testConstructor()
    {
        try
        {
            new Service(null);
            fail("Did not throw the expected NullPointerException");
        }
        catch(NullPointerException npe)
        {
            // Sucess!
        }
    }

    protected void checkDescriptor(Descriptor desc)
    {
        super.checkDescriptor(desc);
        Service service = (Service)desc;

        assertEquals( m_reference, service.getReference());
        assertEquals( m_reference.getClassname(), service.getClassname());
        assertEquals( m_reference.getVersion(), service.getVersion());

        assertEquals( m_entries.length, service.getEntries().length );
        assertTrue( service.matches(m_reference));

        EntryDescriptor[] serviceEntries = service.getEntries();
        for (int i = 0; i < m_entries.length; i++)
        {
            assertEquals( m_entries[i], serviceEntries[i]);
        }
    }
}
