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
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.NOPValidity;

/**
 * Description of a source which is described by the resource protocol
 * which gets a resource from the classloader.
 * FIXME: Get mime-type, content-length, lastModified
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.4 $ $Date: 2002/06/13 12:59:10 $
 */

public final class ResourceSource
    extends AbstractSource
    implements Source
{
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
            loader = getClass().getClassLoader();
        }
        return loader.getResourceAsStream( this.location );
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

}
