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
package org.apache.avalon.fortress.impl.lookup.test;

import junit.framework.TestCase;
import org.apache.avalon.fortress.Container;
import org.apache.avalon.fortress.impl.AbstractContainer;
import org.apache.avalon.fortress.impl.handler.ComponentHandler;
import org.apache.avalon.fortress.impl.lookup.FortressServiceManager;
import org.apache.avalon.fortress.impl.lookup.FortressServiceSelector;
import org.apache.avalon.fortress.test.data.Component1;
import org.apache.avalon.fortress.test.data.Role1;
import org.apache.avalon.framework.service.ServiceException;

/**
 * FortressServiceManagerTestCase does XYZ
 *
 * @author <a href="bloritsch.at.apache.org">Berin Loritsch</a>
 * @version CVS $ Revision: 1.1 $
 */
public class FortressServiceManagerTestCase extends TestCase
{
    TestContainer m_container = new TestContainer();

    public FortressServiceManagerTestCase( String name )
    {
        super( name );
    }

    public void testServiceManager() throws Exception
    {
        FortressServiceManager manager = new FortressServiceManager( m_container, null );

        m_container.setExpectedKey( Role1.ROLE );

        assertTrue( manager.hasService( Role1.ROLE ) );
        assertNotNull( manager.lookup( Role1.ROLE ) );

        String hint = "test";
        m_container.setExpectedHint( hint );
        assertTrue( manager.hasService( Role1.ROLE + "/" + hint ) );
        assertNotNull( manager.lookup( Role1.ROLE + "/" + hint ) );

        m_container.setExpectedHint( AbstractContainer.SELECTOR_ENTRY );
        assertTrue( manager.hasService( Role1.ROLE + "Selector" ) );
        assertNotNull( manager.lookup( Role1.ROLE + "Selector" ) );
    }

    public void testServiceSelector() throws Exception
    {
        FortressServiceSelector selector = new FortressServiceSelector( m_container, Role1.ROLE );

        m_container.setExpectedKey( Role1.ROLE );

        String hint = "test";
        m_container.setExpectedHint( hint );
        assertTrue( selector.isSelectable( hint ) );
        assertNotNull( selector.select( hint ) );
    }
}

class TestContainer implements Container
{
    private String m_key;
    private Object m_hint = AbstractContainer.DEFAULT_ENTRY;
    private TestComponentHandler m_component;

    public TestContainer()
    {
        m_component = new TestComponentHandler();
    }

    public void setExpectedKey( String key )
    {
        m_key = key;
    }

    public void setExpectedHint( Object hint )
    {
        m_hint = hint;
    }

    public Object get( String key, Object hint ) throws ServiceException
    {
        if ( exists( key, hint ) )
        {
            return m_component;
        }

        throw new ServiceException( m_key, "Unexpected key/hint combo" );
    }

    public boolean has( String key, Object hint )
    {
        if ( exists( key, hint ) )
        {
            return true;
        }

        return false;
    }

    private boolean exists( String key, Object hint )
    {
        boolean exists = false;

        if ( m_key.equals( key ) )
        {
            if ( null == m_hint )
            {
                exists = hint == null;
            }
            else
            {
                exists = m_hint.equals( hint );
            }
        }

        return exists;
    }
}

class TestComponentHandler implements ComponentHandler
{
    Object m_component = new Component1();

    public Class getComponentClass()
    {
        return m_component.getClass();
    }

    public void prepareHandler() throws Exception
    {
    }

    public Object get() throws Exception
    {
        return m_component;
    }

    public void put( Object component )
    {
        // do nothing
    }

}