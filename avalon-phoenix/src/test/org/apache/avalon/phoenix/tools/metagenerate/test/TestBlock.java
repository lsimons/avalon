/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.metagenerate.test;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * Blah!
 *
 * @phoenix:block
 * @phoenix:service name="blah.BlahService" version="1.9"
 * @phoenix:mx name="YeeeHaaa"
 *
 */
public class TestBlock implements Serviceable, Configurable
{
    /**
     * @phoenix:dependency name="blah.OtherBlahService" version="1.2"
     */
    public void service( final ServiceManager serviceManager )
        throws ServiceException
    {

    }

    /**
     * @phoenix:configuration-schema type="abc"
     */
    public void configure(Configuration configuration) throws ConfigurationException {
    }


}

