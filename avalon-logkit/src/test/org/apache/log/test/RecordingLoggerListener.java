/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.log.test;

import java.util.ArrayList;
import java.util.List;
import org.apache.log.Logger;
import org.apache.log.util.LoggerListener;

/**
 * A logger listener that records the log messages it receives.
 *
 * @author <a href="mailto:peter at realityforge.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003/02/03 19:39:39 $
 */
class RecordingLoggerListener
    extends LoggerListener
{
    //The listeners that have been created
    private final List m_loggers = new ArrayList();

    public void loggerCreated( final String category,
                               final Logger logger )
    {
        m_loggers.add( logger );
    }

    public Logger[] getLoggers()
    {
        return (Logger[])m_loggers.toArray( new Logger[ m_loggers.size() ] );
    }
}

