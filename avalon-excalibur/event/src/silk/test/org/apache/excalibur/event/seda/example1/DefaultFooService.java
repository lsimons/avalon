/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda.example1;

import java.util.Date;

import org.apache.avalon.framework.CascadingError;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.seda.AbstractLogEnabledStage;
import org.apache.excalibur.event.seda.NoSuchSinkException;
import org.apache.excalibur.event.seda.QueueTimer;

/**
 * A Foo Service implementation for the stage container test.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class DefaultFooService
    extends AbstractLogEnabledStage
    implements FooService, Serviceable
{
    /** The managing service manager */
    private ServiceManager m_serviceManager = null;

    /** A counter that keeps track of the handled events */
    private int counter = 0;

    /** The maximum of events this thing will handle */
    private static final int MAX = 10000000;

    //------------------------ FooService implementation
    /**
     * @see FooService#handle(FooException)
     */
    public void handle(BarException exception) throws FooException
    {
        if (getLogger().isInfoEnabled())
        {
            getLogger().info("++++++++++++++++++++++++++++++++++++++++++++++++");
            getLogger().info("BarException occurred!" + exception.getMessage());
            getLogger().info("++++++++++++++++++++++++++++++++++++++++++++++++");
        }
    }

    /**
     * @see FooService#process(FooMessage[])
     */
    public void process(FooMessage[] elem) throws FooException
    {
        if (getLogger().isInfoEnabled())
        {
            getLogger().info(elem.length + " Foo Messages to process.");
        }
        for (int i = 0; i < elem.length; i++)
        {
            final FooMessage element = elem[i];
            if (elem[i].isTimed())
            {
                // must be a timer event
                final QueueTimer timer;
                try
                {
                    timer = (QueueTimer) m_serviceManager.lookup(QueueTimer.ROLE);
                }
                catch (ServiceException e)
                {
                    throw new CascadingError(e.getMessage(), e);
                }

                try
                {
                    timer.registerTrigger(
                        2000,
                        new FooMessage(),
                        getSinkMap().getSink("foo-stage"));
                }
                catch (NoSuchSinkException e)
                {
                    getLogger().error(e.getMessage(), e);

                }
                finally
                {
                    m_serviceManager.release(timer);
                }
            }
            else 
            {
                if (counter++ > MAX)
                {
                    return;
                }
                //getLogger().info(counter + "");

                if (getLogger().isInfoEnabled())
                {
                    getLogger().info("Received:" + elem[i].toString());
                }

                // do some work here
                int x = 10000;
                for (int j = 0; j < x; j++)
                {
                    try
                    {
                        double f = Math.sin(Math.random());
                        Math.cos(f);
                    }
                    catch (Throwable e)
                    {
                    }
                }

                // tests whether the default sink (bar-stage) is returned
                //final Sink defaultSink = getSinkMap().getDefaultSink();
                final Sink defaultSink = getSinkMap().getSink("bar-stage");
                try
                {
                    defaultSink.enqueue(new BarMessage(
                        "HelloWorld from FooService at " + new Date()));
                }
                catch (SinkException e)
                {
                    getLogger().error(
                        "Could not enqueue. Queue Size=" + defaultSink.size(),
                        e);
                    throw new FooException("Could not enqueue", e);
                }
            }
        }
    }

    //------------------------ Serviceable implementation
    /**
     * @see Serviceable#service(ServiceManager)
     */
    public void service(ServiceManager serviceManager)
    {
        m_serviceManager = serviceManager;
    }

}