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
package org.apache.avalon.fortress.util.test;

import junit.framework.TestCase;
import org.apache.avalon.fortress.util.LifecycleExtensionManager;
import org.apache.avalon.framework.logger.NullLogger;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.lifecycle.Creator;
import org.apache.avalon.lifecycle.Accessor;

import java.util.Iterator;

/**
 * LifecycleExtensionManagerTestCase does XYZ
 *
 * @author <a href="bloritsch.at.apache.org">Berin Loritsch</a>
 * @version CVS $ Revision: 1.1 $
 */
public class LifecycleExtensionManagerTestCase extends TestCase
{
    private LifecycleExtensionManager m_manager;

    public LifecycleExtensionManagerTestCase( String name )
    {
        super( name );
    }

    public void setUp()
    {
        m_manager = new LifecycleExtensionManager();
        m_manager.enableLogging(new NullLogger());
    }

    public void testCreators()
    {
        TestCreator testCreator = new TestCreator(0);
        assertEquals(0, m_manager.creatorExtensionsCount());

        m_manager.addCreatorExtension(testCreator);

        assertEquals(1, m_manager.creatorExtensionsCount());
        assertEquals( testCreator, m_manager.getCreatorExtension( 0 ) );

        int count = 0;
        Iterator it = m_manager.creatorExtensionsIterator();
        while (it.hasNext())
        {
            count++;
            TestCreator creator = (TestCreator)it.next();
            assertEquals(testCreator, creator);
            assertEquals( testCreator.m_id, creator.m_id );
        }
        assertEquals(1, count);

        TestCreator second = new TestCreator( 1 );
        m_manager.insertCreatorExtension( 0, second );

        assertEquals( 2, m_manager.creatorExtensionsCount() );
        assertEquals( second.m_id, ( (TestCreator) m_manager.getCreatorExtension( 0 ) ).m_id );
        assertEquals( testCreator.m_id, ( (TestCreator) m_manager.getCreatorExtension( 1 ) ).m_id );

        m_manager.removeCreatorExtension( 0 );
        assertEquals( 1, m_manager.creatorExtensionsCount() );
        assertEquals( testCreator.m_id, ( (TestCreator) m_manager.getCreatorExtension( 0 ) ).m_id );

        m_manager.clearCreatorExtensions();
        assertEquals(0, m_manager.creatorExtensionsCount());
    }

    public void testAccessors()
    {
        TestAccessor testAccessor = new TestAccessor(0);
        assertEquals( 0, m_manager.accessorExtensionsCount() );

        m_manager.addAccessorExtension( testAccessor );

        assertEquals(1, m_manager.accessorExtensionsCount());
        assertEquals( testAccessor, m_manager.getAccessorExtension( 0 ) );

        int count = 0;
        Iterator it = m_manager.accessorExtensionsIterator();
        while ( it.hasNext() )
        {
            count++;
            TestAccessor accessor = (TestAccessor) it.next();
            assertEquals( testAccessor, accessor );
            assertEquals( testAccessor.m_id, accessor.m_id);
        }
        assertEquals( 1, count );

        TestAccessor second = new TestAccessor(1);
        m_manager.insertAccessorExtension(0, second);

        assertEquals( 2, m_manager.accessorExtensionsCount());
        assertEquals( second.m_id, ( (TestAccessor) m_manager.getAccessorExtension( 0 ) ).m_id );
        assertEquals( testAccessor.m_id, ( (TestAccessor) m_manager.getAccessorExtension( 1 ) ).m_id );

        m_manager.removeAccessorExtension(0);
        assertEquals( 1, m_manager.accessorExtensionsCount() );
        assertEquals( testAccessor.m_id, ( (TestAccessor) m_manager.getAccessorExtension( 0 ) ).m_id );

        m_manager.clearAccessorExtensions();
        assertEquals( 0, m_manager.accessorExtensionsCount());
    }

    public void testLifecycle() throws Exception
    {
        Accessor testAccessor = new TestAccessor(1);
        Creator testCreator = new TestCreator(1);
        m_manager.addCreatorExtension( testCreator );
        m_manager.addAccessorExtension( testAccessor );

        TestComponent component = new TestComponent();
        Context context = new DefaultContext();
        m_manager.executeCreationExtensions( component, context );
        m_manager.executeAccessExtensions( component, context );
        m_manager.executeReleaseExtensions( component, context );
        m_manager.executeDestructionExtensions( component, context );
    }
}
