/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.component.test;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.excalibur.component.ComponentProxyGenerator;
import junit.framework.TestCase;

/**
 * Create a Component proxy.  Requires JDK 1.3+
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public final class ComponentProxyGeneratorTestCase
    extends TestCase
{
    public ComponentProxyGeneratorTestCase( String name )
    {
        super( name );
    }

    public void testGenerateComponent()
        throws Exception
    {
        Integer testInt = new Integer( 7 );
        ComponentProxyGenerator proxyGen = new ComponentProxyGenerator();

        final Component component =
            proxyGen.getProxy( "java.lang.Comparable", testInt );
        assertTrue( component != null );
        assertTrue( component instanceof Comparable );

        Comparable comp = (Comparable)component;
        assertEquals( 0, comp.compareTo( testInt ) );

        /* Please note one important limitation of using the Proxy on final
         * classes like Integer.  I cannot create a proxy on Integer, but I
         * can on interfaces it implements like Comparable.  I can safely
         * compare the proxied class against the original Integer, but I
         * cannot compare the original Integer against the proxied class.
         * there ends up a class cast exception within the Integer.compareTo
         * method.
         */
    }
}
