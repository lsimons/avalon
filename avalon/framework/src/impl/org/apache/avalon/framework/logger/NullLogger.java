/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.logger;


/**
 * The Null Logger class.  This is useful for implementations where you need
 * to provide a logger to a utility class, but do not want any output from it.
 * It also helps when you have a utility that does not have a logger to supply.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public final class NullLogger implements Logger
{
    public NullLogger()
    {
    }

    public void debug(String message)
    {
    }

    public void debug(String message, Throwable throwable)
    {
    }

    public boolean isDebugEnabled()
    {
        return false;
    }

    public void info(String message)
    {
    }

    public void info(String message, Throwable throwable)
    {
    }

    public boolean isInfoEnabled()
    {
        return false;
    }

    public void warn(String message)
    {
    }

    public void warn(String message, Throwable throwable)
    {
    }

    public boolean isWarnEnabled()
    {
        return false;
    }

    public void error(String message)
    {
    }

    public void error(String message, Throwable throwable)
    {
    }

    public boolean isErrorEnabled()
    {
        return false;
    }

    public void fatalError(String message)
    {
    }

    public void fatalError(String message, Throwable throwable)
    {
    }

    public boolean isFatalErrorEnabled()
    {
        return false;
    }

    public Logger getChildLogger(String name)
    {
        return this;
    }
}