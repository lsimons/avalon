/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.fortress.test.util;

import junit.framework.TestCase;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.NullLogger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.DefaultServiceManager;

/**
 * This class provides basic facilities for enforcing Avalon's contracts
 * within your own code.
 *
 * @author <a href="bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/01/27 17:04:49 $
 */
public final class ComponentTestCase
    extends TestCase
{
    public ComponentTestCase( String test )
    {
        super( test );
    }

    public void testCorrectLifecycle()
        throws Exception
    {
        final org.apache.avalon.fortress.test.util.FullLifecycleComponent component = new org.apache.avalon.fortress.test.util.FullLifecycleComponent();
        component.enableLogging( new NullLogger() );
        component.contextualize( new DefaultContext() );
        component.service( new DefaultServiceManager() );
        component.configure( new DefaultConfiguration( "", "" ) );
        component.parameterize( new Parameters() );
        component.initialize();
        component.start();
        component.suspend();
        component.resume();
        component.stop();
        component.dispose();
    }

    public void testMissingLogger()
        throws Exception
    {
        org.apache.avalon.fortress.test.util.FullLifecycleComponent component = new org.apache.avalon.fortress.test.util.FullLifecycleComponent();

        try
        {
            component.contextualize( new DefaultContext() );
        }
        catch( Exception e )
        {
            return;
        }
        fail( "Did not detect missing logger" );
    }

    public void testOutOfOrderInitialize()
        throws Exception
    {
        final org.apache.avalon.fortress.test.util.FullLifecycleComponent component = new org.apache.avalon.fortress.test.util.FullLifecycleComponent();
        component.enableLogging( new NullLogger() );
        component.contextualize( new DefaultContext() );
        component.service( new DefaultServiceManager() );
        try
        {
            component.initialize();
            component.parameterize( new Parameters() );
        }
        catch( Exception e )
        {
            return;
        }
        fail( "Did not detect out of order initialization" );
    }

    public void testOutOfOrderDispose()
        throws Exception
    {
        final org.apache.avalon.fortress.test.util.FullLifecycleComponent component = new org.apache.avalon.fortress.test.util.FullLifecycleComponent();
        component.enableLogging( new NullLogger() );
        component.contextualize( new DefaultContext() );
        component.service( new DefaultServiceManager() );
        component.configure( new DefaultConfiguration( "", "" ) );
        component.parameterize( new Parameters() );
        component.initialize();
        component.start();
        component.suspend();
        component.resume();

        try
        {
            component.dispose();
            component.stop();
        }
        catch( Exception e )
        {
            return;
        }
        fail( "Did not detect out of order disposal" );
    }

    public void testDoubleAssignOfLogger()
    {
        final org.apache.avalon.fortress.test.util.FullLifecycleComponent component = new org.apache.avalon.fortress.test.util.FullLifecycleComponent();
        try
        {
            component.enableLogging( new NullLogger() );
            component.enableLogging( new NullLogger() );
        }
        catch( Exception e )
        {
            // test successfull
            return;
        }

        fail( "Did not detect double assignment of Logger" );
    }

    public void testDoubleAssignOfContext()
    {
        final org.apache.avalon.fortress.test.util.FullLifecycleComponent component = new org.apache.avalon.fortress.test.util.FullLifecycleComponent();
        component.enableLogging( new NullLogger() );
        try
        {
            component.contextualize( new DefaultContext() );
            component.contextualize( new DefaultContext() );
        }
        catch( Exception e )
        {
            // test successfull
            return;
        }

        fail( "Did not detect double assignment of Context" );
    }

    public void testDoubleAssignOfParameters()
        throws Exception
    {
        final org.apache.avalon.fortress.test.util.FullLifecycleComponent component = new org.apache.avalon.fortress.test.util.FullLifecycleComponent();
        component.enableLogging( new NullLogger() );
        component.contextualize( new DefaultContext() );
        component.service( new DefaultServiceManager() );
        component.configure( new DefaultConfiguration( "", "" ) );

        try
        {
            component.parameterize( new Parameters() );
            component.parameterize( new Parameters() );
        }
        catch( Exception e )
        {
            // test successfull
            return;
        }

        fail( "Did not detect double assignment of Parameters" );
    }

    public void testDoubleAssignOfConfiguration() throws Exception
    {
        final org.apache.avalon.fortress.test.util.FullLifecycleComponent component = new org.apache.avalon.fortress.test.util.FullLifecycleComponent();
        component.enableLogging( new NullLogger() );
        component.contextualize( new DefaultContext() );
        component.service( new DefaultServiceManager() );
        try
        {
            component.configure( new DefaultConfiguration( "", "" ) );
            component.configure( new DefaultConfiguration( "", "" ) );
        }
        catch( Exception e )
        {
            // test successfull
            return;
        }

        fail( "Did not detect double assignment of Configuration" );
    }

    public void testDoubleAssignOfComponentManger()
        throws Exception
    {
        final org.apache.avalon.fortress.test.util.FullLifecycleComponent component = new org.apache.avalon.fortress.test.util.FullLifecycleComponent();
        component.enableLogging( new NullLogger() );
        component.contextualize( new DefaultContext() );
        try
        {
            component.service( new DefaultServiceManager() );
            component.service( new DefaultServiceManager() );
        }
        catch( Exception e )
        {
            // test successfull
            return;
        }

        fail( "Did not detect double assignment of ComponentLocator" );
    }
}
