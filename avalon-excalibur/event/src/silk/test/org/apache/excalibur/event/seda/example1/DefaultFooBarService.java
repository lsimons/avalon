/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda.example1;

import org.apache.excalibur.event.seda.AbstractLogEnabledStage;

/**
 * A Foo Bar Service implementation for the stage container test.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class DefaultFooBarService
    extends AbstractLogEnabledStage implements FooBarService
{

    //------------------------ FooBarService implementation
    /**
     * @see FooBarService#report(FooBarMessage)
     */
    public void report(FooBarMessage elem) throws FooBarException
    {
        if (getLogger().isInfoEnabled())
        {
            getLogger().info("FooBarService reporting!");
            if(elem instanceof FooBarBazMessage)
            {
                getLogger().info("FooBarBaz received: " + elem.toString());
            }
            else
            {
                getLogger().info("FooBar Received: " + elem.toString());
            }
        }
    }
}