/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.source.impl;

import java.io.IOException;
import java.io.InputStream;
import org.apache.excalibur.source.*;
import org.apache.excalibur.source.impl.validity.NOPValidity;

/**
 * Description of a source which is described by the resource protocol
 * which gets a resource from the classloader.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/19 09:05:37 $
 */

public final class ResourceSource
    implements Source
{
    /** The system identifier */
    private String systemId;

    /** Location of the resource */
    private String location;

    /**
     * Constructor
     */
    public ResourceSource( String systemId )
    {
        this.systemId = systemId;
        final int pos = systemId.indexOf( "://" );
        this.location = systemId.substring( pos + 3 );
    }

    /**
     * Return an <code>InputStream</code> object to read from the source.
     */
    public InputStream getInputStream()
        throws IOException
    {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if( loader == null )
        {
            loader = this.getClass().getClassLoader();
        }
        return loader.getResourceAsStream( this.location );
    }

    /**
     * Return the unique identifer for this source
     */
    public String getSystemId()
    {
        return this.systemId;
    }

    /**
     *  Get the Validity object. This can either wrap the last modification
     *  date or the expires information or...
     *  If it is currently not possible to calculate such an information
     *  <code>null</code> is returned.
     */
    public SourceValidity getValidity()
    {
        // we are always valid
        return NOPValidity.SHARED_INSTANCE;
    }

    /**
     * Refresh this object and update the last modified date
     * and content length.
     */
    public void discardValidity()
    {
        // nothing to do
    }

    /**
     * The mime-type of the content described by this object.
     * If the source is not able to determine the mime-type by itself
     * this can be null.
     */
    public String getMimeType()
    {
        // FIXME
        return null;
    }

}
