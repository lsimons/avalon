/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.test;

import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.parameters.ParameterException;

import org.apache.log.Hierarchy;

import junit.framework.TestCase;

/**
 * This class provides basic facilities for enforcing Avalon's contracts
 * within your own code.
 *
 * @author <a href="bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.2 $ $Date: 2001/11/30 21:49:32 $
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
        FullLifecycleComponent component = new FullLifecycleComponent();

        component.enableLogging(new LogKitLogger(Hierarchy.getDefaultHierarchy().getLoggerFor("")));
        component.contextualize(new DefaultContext());
        component.parameterize(new Parameters());
        component.configure(new DefaultConfiguration("", ""));
        component.compose(new DefaultComponentManager());
        component.initialize();
        component.start();
        component.suspend();
        component.resume();
        component.stop();
        component.dispose();
    }

    public void testOutOfOrderInitialize()
    {
       FullLifecycleComponent component = new FullLifecycleComponent();

       try
       {
           component.enableLogging(new LogKitLogger(Hierarchy.getDefaultHierarchy().getLoggerFor("")));
           component.contextualize(new DefaultContext());
           component.initialize();
           component.parameterize(new Parameters());
       }
       catch ( Exception e )
       {
           return;
       }
       fail("Did not detect out of order initialization");
    }

    public void testOutOfOrderDispose()
    {
       FullLifecycleComponent component = new FullLifecycleComponent();

       try
       {
            component.enableLogging(new LogKitLogger(Hierarchy.getDefaultHierarchy().getLoggerFor("")));
            component.contextualize(new DefaultContext());
            component.parameterize(new Parameters());
            component.configure(new DefaultConfiguration("", ""));
            component.compose(new DefaultComponentManager());
            component.initialize();
            component.start();
            component.suspend();
            component.resume();
            component.dispose();
            component.stop();
       }
       catch ( Exception e )
       {
           return;
       }
       fail("Did not detect out of order disposal");
    }

    public void testDoubleAssignOfLogger()
    {
        FullLifecycleComponent component = new FullLifecycleComponent();

        try
        {
            component.enableLogging(new LogKitLogger(Hierarchy.getDefaultHierarchy().getLoggerFor("")));
            component.enableLogging(new LogKitLogger(Hierarchy.getDefaultHierarchy().getLoggerFor("")));
        }
        catch (Exception e)
        {
            // test successfull
            return;
        }

        fail("Did not detect double assignment of Logger");
    }

    public void testDoubleAssignOfContext()
    {
        FullLifecycleComponent component = new FullLifecycleComponent();

        try
        {
            component.enableLogging(new LogKitLogger(Hierarchy.getDefaultHierarchy().getLoggerFor("")));
            component.contextualize(new DefaultContext());
            component.contextualize(new DefaultContext());
        }
        catch (Exception e)
        {
            // test successfull
            return;
        }

        fail("Did not detect double assignment of Context");
    }

    public void testDoubleAssignOfParameters()
    {
        FullLifecycleComponent component = new FullLifecycleComponent();

        try
        {
            component.enableLogging(new LogKitLogger(Hierarchy.getDefaultHierarchy().getLoggerFor("")));
            component.contextualize(new DefaultContext());
            component.parameterize(new Parameters());
            component.parameterize(new Parameters());
        }
        catch (Exception e)
        {
            // test successfull
            return;
        }

        fail("Did not detect double assignment of Parameters");
    }

    public void testDoubleAssignOfConfiguration()
    {
        FullLifecycleComponent component = new FullLifecycleComponent();

        try
        {
            component.enableLogging(new LogKitLogger(Hierarchy.getDefaultHierarchy().getLoggerFor("")));
            component.contextualize(new DefaultContext());
            component.parameterize(new Parameters());
            component.configure(new DefaultConfiguration("", ""));
            component.configure(new DefaultConfiguration("", ""));
        }
        catch (Exception e)
        {
            // test successfull
            return;
        }

        fail("Did not detect double assignment of Configuration");
    }

    public void testDoubleAssignOfComponentManger()
    {
        FullLifecycleComponent component = new FullLifecycleComponent();

        try
        {
            component.enableLogging(new LogKitLogger(Hierarchy.getDefaultHierarchy().getLoggerFor("")));
            component.contextualize(new DefaultContext());
            component.parameterize(new Parameters());
            component.configure(new DefaultConfiguration("", ""));
            component.compose(new DefaultComponentManager());
            component.compose(new DefaultComponentManager());
        }
        catch (Exception e)
        {
            // test successfull
            return;
        }

        fail("Did not detect double assignment of ComponentManager");
    }
}
