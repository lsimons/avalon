/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.monitor;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Managed Writer.  This is convenient when you want to dynamically
 * set and get the information from the resource.  For instance, the Resource does
 * not need to be actively monitored if all access to the resource goes through
 * this type of Resource.  It can notify the change as soon as the Writer or
 * OutputStream has been closed.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version $Id: ResourceWriter.java,v 1.6 2002/05/13 12:17:40 donaldp Exp $
 */
final class ResourceWriter extends FilterWriter
{
    private final StreamResource m_resource;

    /**
     * Set up the ResourceOutputStream.
     */
    public ResourceWriter( final Writer out,
                           final StreamResource resource )
    {
        super( out );
        m_resource = resource;
    }

    /**
     * Override the close method so that we can be notified when the update is
     * complete.
     */
    public final void close()
        throws IOException
    {
        super.close();
        m_resource.streamClosedEvent();
    }
}
