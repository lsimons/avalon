/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.logger;

/**
 * Utility class to allow construction of easy components that will perform logging.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class AbstractLoggable
    implements Loggable
{
    ///Base Logger instance
    private Logger    m_logger;

    /**
     * Set the components logger.
     *
     * @param logger the logger
     */
    public void setLogger( final Logger logger )
    {
        m_logger = logger;
    }

    /**
     * Set the component's logger.
     */
    public void setLogger( final org.apache.log.Logger logger )
    {
        setLogger( new LogKitLogger(logger) );
    }

    /**
     * Helper method to allow sub-classes to aquire logger.
     *
     * @return the Logger
     */
    protected final Logger getLogger()
    {
        return m_logger;
    }

    /**
     * Helper method to setup other components with same logger.
     *
     * @param component the component to pass logger object to
     */
    protected void setupLogger( final Object component )
    {
        setupLogger( component, (String)null );
    }

    /**
     * Helper method to setup other components with logger.
     * The logger has the subcategory of this components logger.
     *
     * @param component the component to pass logger object to
     * @param subCategory the subcategory to use (may be null)
     */
    protected void setupLogger( final Object component, final String subCategory )
    {
        Logger logger = m_logger;

        if( null != subCategory )
        {
            logger = m_logger.getChildLogger( subCategory );
        }

        setupLogger( component, logger );
    }

    /**
     * Helper method to setup other components with logger.
     *
     * @param component the component to pass logger object to
     * @param logger the Logger
     */
    protected void setupLogger( final Object component, final Logger logger )
    {
        if( component instanceof Loggable )
        {
            ((Loggable)component).setLogger( logger );
        }
    }
}
