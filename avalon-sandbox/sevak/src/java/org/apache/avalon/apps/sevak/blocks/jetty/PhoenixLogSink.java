/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.apps.sevak.blocks.jetty;

import org.mortbay.util.LogSink;
import org.mortbay.util.Frame;
import org.mortbay.util.Log;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * Jetty Log redirection
 *
 *
 * @see <a href="http://jetty.mortbay.com/">Jetty Project Page</a>
 *
 * @author  Bruno Dumon & Paul Hammant
 * @version 1.0
 */
public class PhoenixLogSink extends AbstractLogEnabled implements LogSink
{
    public void setOptions(String s)
    {
    }

    public String getOptions()
    {
        return "";
    }

    public void log(String type, Object message, Frame frame, long time)
    {
        if (type.equals(Log.DEBUG))
        {
            getLogger().info("time=" + time + " message=" + message + " frame=" + frame);
        }
        else if (type.equals(Log.FAIL))
        {
            getLogger().error("time=" + time + " message=" + message + " frame=" + frame);
        }
        else if (type.equals(Log.WARN))
        {
            getLogger().warn("time=" + time + " message=" + message + " frame=" + frame);
        }
        else if (type.equals(Log.ASSERT))
        {
            getLogger().info("ASSERT time=" + time + " message=" + message + " frame=" + frame);
        }
        else
        {
            getLogger().info("time=" + time + " message=" + message + " frame=" + frame);
        }
    }

    public void log(String message)
    {
        getLogger().info(message);
    }

    public void start() throws Exception
    {
    }

    public void stop() throws InterruptedException
    {
    }

    public boolean isStarted()
    {
        return true;
    }
}
