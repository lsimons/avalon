/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda.example1;

import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.seda.AbstractLogEnabledStage;

/**
 * A Bar Service implementation for the stage container test.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class DefaultBarService
    extends AbstractLogEnabledStage implements BarService
{

    //------------------------ BarService implementation
    /**
     * @see BarService#process(FooEvent)
     */
    public void process(BarMessage elem) throws BarException
    {
        if (getLogger().isInfoEnabled())
        {
            getLogger().info("Received:" + elem.toString());
        }
        
        
        // tests whether the default sink (foo-stage) is returned
        final Sink defaultSink = getSinkMap().getDefaultSink();
        try
        {
            // acknowledge receipt of bar message
            defaultSink.enqueue(
                new FooBarMessage("Acknowledge receipt of " + elem));
            defaultSink.enqueue(
                new FooBarBazMessage("Now prcessing " + elem));
            
            // Now do some work here
            int x = 50000;
            for (int i = 0; i < x; i++)
            {
                try
                {
                    Math.sin(Math.random());
                }
                catch (Throwable e)
                {
                }
            }

            final FooMessage[] elements = new FooMessage[2];
            for (int i = 0; i < elements.length; i++)
            {
                final long time = System.currentTimeMillis();
                elements[i] = new FooMessage("Request for processing: " + time);
            }
            defaultSink.enqueue(elements);
            
            throw new BarException("Test Bar Exception. Nothing happened.");
        }
        catch (SinkException e)
        {
            getLogger().error(
                "Could not enqueue. Queue Size=" + defaultSink.size(), e);
            throw new BarException("Could not enqueue", e);
        }
    }
}