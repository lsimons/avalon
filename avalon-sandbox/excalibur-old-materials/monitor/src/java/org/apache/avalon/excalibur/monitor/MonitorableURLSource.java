/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.monitor;

import org.apache.excalibur.source.impl.URLSource;

/**
 * This adds the <code>Monitorable</code> interface to the URLSource.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.4 $ $Date: 2002/06/04 06:26:03 $
 */

public class MonitorableURLSource
    extends URLSource
    implements Monitorable
{

    /**
     * Constructor
     */
    public MonitorableURLSource()
    {
    }

    /**
     *  Get the corresponding Resource object for monitoring.
     */
    public Resource getResource()
        throws Exception
    {
        this.getInfos();
        if( null == this.file )
        {
            return new FileResource( this.file.getAbsolutePath() );
        }
        else
        {
            return new SourceResource( this );
        }
    }

}
