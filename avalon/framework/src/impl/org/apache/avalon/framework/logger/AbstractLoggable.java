/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.logger;

import org.apache.log.Logger;

/**
 * Helper class to inherit from.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class AbstractLoggable
    implements Loggable
{
    private Logger    m_logger;

    public void setLogger( final Logger logger )
    {
        m_logger = logger;
    }

    protected final Logger getLogger()
    {
        return m_logger;
    }

    protected void setupLogger( final Object component )
    {
        setupLogger( component, (String)null );
    }

    protected void setupLogger( final Object component, final String subCategory )
    {
        if( component instanceof Loggable )
        {
            Logger logger = m_logger;

            if( null != subCategory )
            {
                logger = m_logger.getChildLogger( subCategory );
            }

            ((Loggable)component).setLogger( logger );
        }
    }

    protected void setupLogger( final Object component, final Logger logger )
    {
        if( component instanceof Loggable )
        {
            ((Loggable)component).setLogger( logger );
        }
    }
}
