/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.component.test;

import junit.framework.TestCase;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.DefaultComponentSelector;

/**
 * Test the basic public methods of DefaultComponentSelector.
 *
 * @author <a href="mailto:rantene@hotmail.com">Ran Tene</a>
 */
public final class DefaultComponentSelectorTestCase
    extends TestCase
{
    class FeatureComponent
        implements Component
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

    private DefaultComponentSelector m_componentSelector;
    protected boolean m_exceptionThrown;

    public DefaultComponentSelectorTestCase()
    {
        this("DefaultComponentSelector Test Case");
    }

    public DefaultComponentSelectorTestCase( final String name )
    {
        super( name );
    }

    protected void setUp()
        throws Exception
    {
        m_componentSelector = new DefaultComponentSelector();
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
        Hint hintA = new Hint("a");
        Hint hintB = new Hint("b");
        m_componentSelector.put(hintA,new FeatureComponent(hintA));
        assertTrue(m_componentSelector.hasComponent(hintA));
        assertTrue(!m_componentSelector.hasComponent(hintB));
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






