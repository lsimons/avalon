/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.
 
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
 
 4. The names "Jakarta", "Apache Avalon", "Avalon Excalibur", "Avalon
    Framework" and "Apache Software Foundation"  must not be used to endorse
    or promote products derived  from this  software without  prior written
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
 on  behalf of the Apache Software  Foundation and was  originally created by
 Stefano Mazzocchi  <stefano@apache.org>. For more  information on the Apache 
 Software Foundation, please see <http://www.apache.org/>.
 
*/
package org.apache.avalon.framework.component.test;

import junit.framework.TestCase;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.DefaultComponentManager;

/**
 * Test the basic public methods of DefaultComponentManager.
 *
 * @author <a href="mailto:rantene@hotmail.com">Ran Tene</a>
 */
public final class DefaultComponentManagerTestCase
    extends TestCase
{

    class DefaultRoleA
        implements Component,RoleA
    {
        public DefaultRoleA()
        {
        }
    }

    class DefaultRoleB
        implements Component,RoleB
    {
        public DefaultRoleB()
        {
        }
    }


    private DefaultComponentManager m_componentManager;

    protected boolean m_exceptionThrown;


    public DefaultComponentManagerTestCase()
    {
        this("DefaultComponentManager Test Case");
    }

    public DefaultComponentManagerTestCase( final String name )
    {
        super( name );
    }

    protected void setUp()
        throws Exception
    {
        m_componentManager = new DefaultComponentManager();
        m_exceptionThrown = false;
    }

    protected  void tearDown()
        throws Exception
    {
        m_componentManager = null;
    }

    /**
     * lookup contract:
     * return first component found for role
     * search in hirarchy from current componentManager up.
     * if no compnent exist for role a in hierarchy
     * throw ComponentException
     */


    public void testlookup1()
        throws Exception
    {
        DefaultRoleB roleBinBase = new DefaultRoleB();
        DefaultRoleB roleBinParent = new DefaultRoleB();
        DefaultRoleA roleAinParent = new DefaultRoleA();

        m_componentManager.put(RoleA.ROLE,roleAinParent);
        m_componentManager.put(RoleB.ROLE,roleBinParent);
        DefaultComponentManager baseComponentManager = new DefaultComponentManager(m_componentManager);
        baseComponentManager.put(RoleB.ROLE,roleBinBase);
        Object lookupAinBase = baseComponentManager.lookup(RoleA.ROLE);
        Object lookupBinBase = baseComponentManager.lookup(RoleB.ROLE);
        Object lookupBinParent = m_componentManager.lookup(RoleB.ROLE);
        assertTrue( lookupAinBase instanceof RoleA);
        assertEquals( lookupBinBase, roleBinBase );
        assertEquals( lookupBinParent, roleBinParent );
        assertEquals( lookupAinBase,roleAinParent);
    }

    public void testlookup2()
        throws Exception
    {
        m_componentManager.put(RoleA.ROLE,new DefaultRoleA());
        Object o = null;
        try
        {
            o = m_componentManager.lookup(RoleB.ROLE);
        }
        catch        (ComponentException ce)
        {
            m_exceptionThrown = true;
        }
        if (o == null)
            assertTrue("ComponentException was not thrown when component was not found by lookup." ,m_exceptionThrown );
        else
            assertTrue("component was found by lookup ,when there was no component.",false);

    }

    public void testhasComponent()
        throws Exception
    {
        m_componentManager.put(RoleA.ROLE,new DefaultRoleA());
        assertTrue(m_componentManager.hasComponent(RoleA.ROLE));
        assertTrue(!m_componentManager.hasComponent(RoleB.ROLE));
    }

    public void testmakeReadOnly()
        throws Exception
    {
        //before read only
        m_componentManager.put(RoleA.ROLE,new DefaultRoleA());
        Object a = m_componentManager.lookup(RoleA.ROLE);
        assertTrue( a instanceof RoleA);
        m_componentManager.makeReadOnly();
        //after read only
        try
        {
            m_componentManager.put(RoleB.ROLE,new DefaultRoleB());
        }
        catch        (IllegalStateException se)
        {
            m_exceptionThrown = true;
        }
        assertTrue("IllegalStateException was not thrown in  put after makeReadOnly." , m_exceptionThrown );
    }
}







