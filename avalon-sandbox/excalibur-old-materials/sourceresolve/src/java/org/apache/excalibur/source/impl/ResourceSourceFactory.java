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
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceFactory;

/**
 * A factory for the Resource protocol
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version $Id: ResourceSourceFactory.java,v 1.5 2002/06/13 12:59:10 bloritsch Exp $
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
        throws MalformedURLException, IOException, SourceException
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Creating source object for " + location );
        }
        return new ResourceSource( location );
    }

}
