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
 * @version 1.0
 */
public final class NullLogger implements Logger
{
    /**
     * Creates a new <code>NullLogger</code>.
     */
    public NullLogger()
    {
    }

    /**
     * No-op.
     *
     * @param message ignored
     */
    public void debug(String message)
    {
    }

    /**
     * No-op.
     *
     * @param message ignored
     * @param throwable ignored
     */
    public void debug(String message, Throwable throwable)
    {
    }

    /**
     * No-op.
     *
     * @return <code>false</code>
     */
    public boolean isDebugEnabled()
    {
        return false;
    }

    /**
     * No-op.
     *
     * @param message ignored
     */
    public void info(String message)
    {
    }

    /**
     * No-op.
     *
     * @param message ignored
     * @param throwable ignored
     */
    public void info(String message, Throwable throwable)
    {
    }

    /**
     * No-op.
     *
     * @return <code>false</code>
     */
    public boolean isInfoEnabled()
    {
        return false;
    }

    /**
     * No-op.
     *
     * @param message ignored
     */
    public void warn(String message)
    {
    }

    /**
     * No-op.
     *
     * @param message ignored
     * @param throwable ignored
     */
    public void warn(String message, Throwable throwable)
    {
    }

    /**
     * No-op.
     *
     * @return <code>false</code>
     */
    public boolean isWarnEnabled()
    {
        return false;
    }

    /**
     * No-op.
     *
     * @param message ignored
     */
    public void error(String message)
    {
    }

    /**
     * No-op.
     *
     * @param message ignored
     * @param throwable ignored
     */
    public void error(String message, Throwable throwable)
    {
    }

    /**
     * No-op.
     *
     * @return <code>false</code>
     */
    public boolean isErrorEnabled()
    {
        return false;
    }

    /**
     * No-op.
     *
     * @param message ignored
     */
    public void fatalError(String message)
    {
    }

    /**
     * No-op.
     *
     * @param message ignored
     * @param throwable ignored
     */
    public void fatalError(String message, Throwable throwable)
    {
    }

    /**
     * No-op.
     *
     * @return <code>false</code>
     */
    public boolean isFatalErrorEnabled()
    {
        return false;
    }

    /**
     * Returns this <code>NullLogger</code>.
     *
     * @param name ignored
     * @return this <code>NullLogger</code>
     */
    public Logger getChildLogger(String name)
    {
        return this;
    }
}