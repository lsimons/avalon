/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.monitor.impl;

import org.apache.avalon.excalibur.monitor.Resource;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

/**
 * A passive monitor will check the reosurce each time it
 * is accessed.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/09/07 12:14:02 $
 */
public class PassiveMonitor
    extends AbstractMonitor
{
    private Map m_lastModified = Collections.synchronizedMap( new HashMap() );

    /**
     * Find a monitored resource.  If no resource is available, return null
     */
    public final Resource getResource( final String key )
    {
        final Resource resource = super.getResource( key );
        if( resource != null )
        {
            final Long lastModified = (Long)m_lastModified.get( key );

            if( lastModified != null )
            {
                resource.testModifiedAfter( lastModified.longValue() );
            }

            m_lastModified.put( key,
                                new Long( System.currentTimeMillis() ) );
        }

        return resource;
    }
}
