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
package org.apache.avalon.fortress.test;

import junit.framework.TestCase;
import org.apache.avalon.fortress.MetaInfoEntry;
import org.apache.avalon.fortress.RoleEntry;
import org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler;
import org.apache.avalon.fortress.impl.handler.PerThreadComponentHandler;
import org.apache.avalon.fortress.impl.handler.PoolableComponentHandler;
import org.apache.avalon.fortress.impl.handler.FactoryComponentHandler;
import org.apache.avalon.fortress.test.data.Component1;
import org.apache.avalon.fortress.test.data.Role1;
import org.apache.avalon.fortress.test.data.BaseRole;
import org.apache.avalon.fortress.test.data.Role2;

import java.util.*;

/**
 * MetaInfoEntryTestCase does tests the meta info entry class
 *
 * @author <a href="bloritsch.at.apache.org">Berin Loritsch</a>
 * @version CVS Revision: 1.1 $
 */
public class MetaInfoEntryTestCase extends TestCase
{
    private Class m_componentClass;
    private Properties m_properties;
    private Map m_lifecycleMap;

    public MetaInfoEntryTestCase( String name )
    {
        super( name );
    }

    public void setUp()
    {
        m_componentClass = Component1.class;
        m_properties = new Properties();
        m_properties.setProperty( "x-avalon.name", "component1" );
        m_properties.setProperty( "x-avalon.lifestyle", "singleton" );

        Map lifecycleMap = new HashMap();
        lifecycleMap.put( "singleton", ThreadSafeComponentHandler.class );
        lifecycleMap.put( "thread", PerThreadComponentHandler.class );
        lifecycleMap.put( "pooled", PoolableComponentHandler.class );
        lifecycleMap.put( "transient", FactoryComponentHandler.class );

        m_lifecycleMap = Collections.unmodifiableMap( lifecycleMap );
    }

    public void testFullySpecified() throws Exception
    {
        MetaInfoEntry entry = new MetaInfoEntry( m_componentClass, m_properties );
        checkMetaInfoEntry( entry, ThreadSafeComponentHandler.class, "component1", false );
    }

    public void testAutoDiscovery() throws Exception
    {
        m_properties.remove( "x-avalon.lifestyle" );
        m_properties.remove( "x-avalon.name" );
        m_properties.setProperty( "fortress.handler", ThreadSafeComponentHandler.class.getName() );
        m_componentClass = MetaInfoEntry.class;

        MetaInfoEntry entry = new MetaInfoEntry( m_componentClass, m_properties );

        checkMetaInfoEntry( entry, ThreadSafeComponentHandler.class, "meta-info-entry", false );
    }

    public void testLifestyleMarkers() throws Exception
    {
        String name = "component1";

        Iterator it = m_lifecycleMap.keySet().iterator();
        while ( it.hasNext() )
        {
            String type = (String) it.next();
            m_properties.setProperty( "x-avalon.lifestyle", type );
            MetaInfoEntry entry = new MetaInfoEntry( m_componentClass, m_properties );
            checkMetaInfoEntry( entry, (Class) m_lifecycleMap.get( type ), name, false );
        }
    }

    public void testRoleEntryParent() throws Exception
    {
        RoleEntry roleEntry = new RoleEntry( Role1.class.getName(), "component1",
            m_componentClass, ThreadSafeComponentHandler.class );

        MetaInfoEntry entry = new MetaInfoEntry( roleEntry );

        checkMetaInfoEntry( entry, ThreadSafeComponentHandler.class, "component1", true );
    }

    private void checkMetaInfoEntry( MetaInfoEntry entry, Class handler, String name, boolean oneRole )
    {
        assertEquals( m_componentClass, entry.getComponentClass() );
        assertEquals( name, entry.getConfigurationName() );
        assertEquals( handler, entry.getHandlerClass() );

        if ( oneRole )
        {
            checkSize( 1, entry.getRoles() );
            // only one test does this
        }
        else
        {
            checkSize( 0, entry.getRoles() );
            entry.addRole( Role1.class.getName() );
            checkSize( 1, entry.getRoles() );
            entry.addRole( BaseRole.class.getName() );
            checkSize( 2, entry.getRoles() );
            entry.makeReadOnly();

            assertTrue( entry.containsRole( BaseRole.class.getName() ) );
        }

        assertTrue( entry.containsRole( Role1.class.getName() ) );

        try
        {
            entry.addRole( Role2.class.getName() );
            fail( "Should not allow Role2 to be added" );
        }
        catch ( SecurityException se )
        {
            // SUCCESS!
        }
        catch ( Exception e )
        {
            fail( "Threw the wrong exception: " + e.getMessage() );
        }

        assertTrue( !entry.containsRole( Role2.class.getName() ) );
    }

    private void checkSize( int numRoles, Iterator roles )
    {
        int i = 0;
        while ( roles.hasNext() )
        {
            i++;
            roles.next();
        }

        assertEquals( numRoles, i );
    }
}