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
package org.apache.avalon.framework.service.test;

import junit.framework.TestCase;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.DefaultServiceSelector;

/**
 * Test the basic public methods of DefaultComponentSelector.
 *
 * @author <a href="mailto:rantene@hotmail.com">Ran Tene</a>
 */
public final class DefaultServiceSelectorTestCase
    extends TestCase
{
    class FeatureComponent
    {
        Object  m_feature;
        public FeatureComponent( final Object feature )
        {
            m_feature = feature;
        }

        public Object getFeature()
        {
            return m_feature;
        }
    }

    class Hint
    {
        String  m_name;

        public Hint( final String name )
        {
            m_name = name;
        }

        public String getName()
        {
            return m_name;
        }
    }

    private DefaultServiceSelector m_componentSelector;
    protected boolean m_exceptionThrown;

    public DefaultServiceSelectorTestCase()
    {
        this("DefaultComponentSelector Test Case");
    }

    public DefaultServiceSelectorTestCase( final String name )
    {
        super( name );
    }

    protected void setUp()
        throws Exception
    {
        m_componentSelector = new DefaultServiceSelector();
        m_exceptionThrown =false;
    }

    protected  void tearDown()
        throws Exception
    {
        m_componentSelector = null;
    }

    /**
     * lookup contract:
     * return  the component that was put with this hint
     * if no compnent exist for hint
     * throw ComponentException
     */
    public void testlookup()
        throws Exception
    {
        Hint hintA = new Hint("a");
        Hint hintB = new Hint("b");
        m_componentSelector.put(hintA,new FeatureComponent(hintA));
        m_componentSelector.put(hintB,new FeatureComponent(hintB));
        FeatureComponent  fComponent = (FeatureComponent)m_componentSelector.select(hintA);
        assertEquals( hintA, fComponent.getFeature() );
        Object o = null;
        try
        {
            o = (FeatureComponent)m_componentSelector.select(new Hint("no component"));
        }
        catch        (ServiceException ce)
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
        Hint hintA = new Hint("a");
        Hint hintB = new Hint("b");
        m_componentSelector.put(hintA,new FeatureComponent(hintA));
        assertTrue(m_componentSelector.isSelectable(hintA));
        assertTrue(!m_componentSelector.isSelectable(hintB));
    }

    //makeReadOnly contract:put after makeReadOnly throws IllegalStateException
    public void testmakeReadOnly()
        throws Exception
    {
        Hint hintA = new Hint("a");
        Hint hintB = new Hint("b");
        //before read only
        m_componentSelector.put(hintA,new FeatureComponent(hintA));
        FeatureComponent  fComponent = (FeatureComponent)m_componentSelector.select(hintA);
        assertEquals( hintA, fComponent.getFeature() );
        m_componentSelector.makeReadOnly();
        //after read only
        try
        {
            m_componentSelector.put(hintB,new FeatureComponent(hintB));
        }
        catch        (IllegalStateException se)
        {
            m_exceptionThrown = true;
        }
        assertTrue("IllegalStateException was not thrown in  put after makeReadOnly." , m_exceptionThrown );
    }
}






