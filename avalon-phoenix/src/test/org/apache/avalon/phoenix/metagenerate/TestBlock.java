/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.metagenerate;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * Blah!
 *
 * @phoenix:block
 * @phoenix:service name="blah.BlahService"
 * @phoenix:mx name="YeeeHaaa"
 *
 */
public class TestBlock implements Serviceable
{
    /**
     * @phoenix:dependency name="blah.OtherBlahService"
     */
    public void service( final ServiceManager serviceManager )
        throws ServiceException
    {

    }


}

