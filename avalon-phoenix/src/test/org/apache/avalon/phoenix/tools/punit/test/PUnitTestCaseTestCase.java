/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.tools.punit.test;

import org.apache.avalon.phoenix.tools.punit.PUnitTestCase;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.Configuration;
import org.xml.sax.InputSource;

import java.io.StringReader;

public class PUnitTestCaseTestCase extends PUnitTestCase
{

    DefaultConfigurationBuilder m_defaultConfigurationBuilder = new DefaultConfigurationBuilder();


    public PUnitTestCaseTestCase(String name)
    {
        super(name);
    }

    public void testBasicBlock() throws Exception
    {
        TestBlock block = new TestBlock();
        Configuration configuration = m_defaultConfigurationBuilder.build(
                new InputSource(new StringReader("<hi>Hi</hi>")));
        addBlock("bl","block", block, configuration);
        startup();
        // check lifecycle run thru
        assertNotNull("Configuration null", block.m_configuration);
        assertNotNull("Context null", block.m_context);
        assertNotNull("Logger null", block.m_logger);
        assertNotNull("ServiceManager null", block.m_serviceManager);
        assertTrue("Not Initialized", block.m_initialized);
        // check lifecycle events logged
        assertTrue("Service Not logged", super.logHasEntry("I:service"));
        assertTrue("Initialize Not logged", super.logHasEntry("W:initialize"));
        assertTrue("Contextualize Not logged", super.logHasEntry("E:contextualize"));
        assertTrue("Configure Not logged", super.logHasEntry("F:configure"));
        shutdown();
    }

}
