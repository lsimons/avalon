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
 * @version CVS $Revision: 1.1 $ $Date: 2001/11/30 21:33:13 $
 */
public final class ComponentTestCase
    extends TestCase
{
    public ComponentTestCase( String test )
    {
        super( test );
    }

    public void testCorrectLifecycle()
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
            component.stop();
            component.dispose();
        }
        catch ( Exception ise )
        {
            fail(ise.getMessage());
        }
    }
}
