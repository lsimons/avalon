/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.punit;

import org.apache.avalon.framework.logger.Logger;

import java.util.ArrayList;

/**
 * PunitLogger
 * @author Paul Hammant
 */
public class PUnitLogger implements Logger
{

    private ArrayList m_log = new ArrayList();

    /**
     * Get a logged entry.
     * @param startsWith This term
     * @return The full term
     */
    public String get(String startsWith)
    {
        for (int i = 0; i < m_log.size(); i++)
        {
            String s = (String) m_log.get(i);
            if (s.startsWith(startsWith))
            {
                return s;
            }
        }
        return null;
    }

    /**
     * Contains a logged entry
     * @param s The term
     * @return true or not.
     */
    public boolean contains(String s)
    {
        return get(s) != null;
    }

    /**
     * Debug an entry as per Loggable
     * @param s the term
     */
    public void debug(String s)
    {
        m_log.add("D:" + s);
    }

    /**
     * Debug an entry as per Loggable
     * @param s the term
     * @param throwable An exception
     */
    public void debug(String s, Throwable throwable)
    {
        m_log.add("D:" + s + ":" + throwable != null ? throwable.getMessage() : "");
    }

    public boolean isDebugEnabled()
    {
        return true;
    }

    /**
     * Info an entry as per Loggable
     * @param s the term
     */
    public void info(String s)
    {
        m_log.add("I:" + s);
    }

    /**
     * Info an entry as per Loggable
     * @param s the term
     * @param throwable An exception
     */
    public void info(String s, Throwable throwable)
    {
        m_log.add("I:" + s + ":" + throwable != null ? throwable.getMessage() : "");
    }

    /**
     * Is Info Enabled
     * @return
     */
    public boolean isInfoEnabled()
    {
        return true;
    }

    /**
     * Warn an entry as per Loggable
     * @param s the term
     */
    public void warn(String s)
    {
        m_log.add("W:" + s);
    }

    /**
     * Warn an entry as per Loggable
     * @param s the term
     * @param throwable An exception
     */
    public void warn(String s, Throwable throwable)
    {
        m_log.add("W:" + s + ":" + throwable != null ? throwable.getMessage() : "");
    }

    /**
     * Is Warn Enabled
     * @return
     */
    public boolean isWarnEnabled()
    {
        return false;
    }

    /**
     * Error an entry as per Loggable
     * @param s the term
     */
    public void error(String s)
    {
        m_log.add("E:" + s);
    }

    /**
     * Error an entry as per Loggable
     * @param s the term
     * @param throwable An exception
     */
    public void error(String s, Throwable throwable)
    {
        m_log.add("E:" + s + ":" + throwable != null ? throwable.getMessage() : "");
    }

    /**
     * Is Error Enabled
     * @return
     */
    public boolean isErrorEnabled()
    {
        return true;
    }

    /**
     * Log a fatal error as per Loggable
     * @param s the term
     */
    public void fatalError(String s)
    {
        m_log.add("F:" + s);
    }

    /**
     * Log a fatal error entry as per Loggable
     * @param s the term
     * @param throwable An exception
     */
    public void fatalError(String s, Throwable throwable)
    {
        m_log.add("F:" + s + ":" + throwable != null ? throwable.getMessage() : "");
    }

    /**
     * Is Fatal Error Enabled
     * @return
     */
    public boolean isFatalErrorEnabled()
    {
        return true;
    }

    /**
     * Gtet the child logger
     * @param s The hint to use (ignored)
     * @return The child logger.
     */
    public Logger getChildLogger(String s)
    {
        return this;
    }
}
