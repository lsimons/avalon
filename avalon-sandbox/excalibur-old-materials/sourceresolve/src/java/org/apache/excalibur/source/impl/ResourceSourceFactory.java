/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.source.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.source.*;

/**
 * A factory for the Resource protocol
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version $Id: ResourceSourceFactory.java,v 1.1 2002/04/19 09:05:37 cziegeler Exp $
 */
public class ResourceSourceFactory
    extends AbstractLogEnabled
    implements SourceFactory, ThreadSafe
{

    /**
     * Get a <code>Source</code> object.
     * @param parameters This is optional.
     */
    public Source getSource( String location, Map parameters )
        throws MalformedURLException, IOException
    {
        if( this.getLogger().isDebugEnabled() )
        {
            this.getLogger().debug( "Creating source object for " + location );
        }
        return new ResourceSource( location );
    }

}
