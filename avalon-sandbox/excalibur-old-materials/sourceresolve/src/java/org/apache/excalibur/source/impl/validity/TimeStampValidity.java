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
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/19 09:05:37 $
 */
public final class TimeStampValidity
    implements SourceValidity
{

    private long timeStamp;

    public TimeStampValidity( long timeStamp )
    {
        this.timeStamp = timeStamp;
    }

    /**
     * Check if the component is still valid.
     * If <code>false</code> is returned the isValid(SourceValidity) must be
     * called afterwards!
     */
    public boolean isValid()
    {
        return false;
    }

    public boolean isValid( SourceValidity newValidity )
    {
        if( newValidity instanceof TimeStampValidity )
        {
            return this.timeStamp == ( (TimeStampValidity)newValidity ).getTimeStamp();
        }
        return false;
    }

    public long getTimeStamp()
    {
        return this.timeStamp;
    }

    public String toString()
    {
        return "TimeStampValidity: " + this.timeStamp;
    }

}
