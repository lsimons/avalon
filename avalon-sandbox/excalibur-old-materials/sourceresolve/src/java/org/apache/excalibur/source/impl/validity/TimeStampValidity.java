/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.source.impl.validity;

import org.apache.excalibur.source.SourceValidity;

/**
 * A validation object for time-stamps.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.3 $ $Date: 2002/07/06 03:55:06 $
 */
public final class TimeStampValidity
    implements SourceValidity
{
    private long m_timeStamp;

    public TimeStampValidity( final long timeStamp )
    {
        m_timeStamp = timeStamp;
    }

    /**
     * Check if the component is still valid.
     * If <code>0</code> is returned the isValid(SourceValidity) must be
     * called afterwards!
     * If -1 is returned, the component is not valid anymore and if +1
     * is returnd, the component is valid.
     */
    public int isValid()
    {
        return 0;
    }

    public boolean isValid( SourceValidity newValidity )
    {
        if( newValidity instanceof TimeStampValidity )
        {
            final long timeStamp =
                ( (TimeStampValidity)newValidity ).getTimeStamp();
            return m_timeStamp == timeStamp;
        }
        return false;
    }

    public long getTimeStamp()
    {
        return m_timeStamp;
    }

    public String toString()
    {
        return "TimeStampValidity: " + m_timeStamp;
    }
}
