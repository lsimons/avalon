/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.xmlizer.impl;

import java.io.InputStream;
import java.io.IOException;
import org.apache.excalibur.xmlizer.XMLizer;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.excalibur.component.DefaultComponentSelector;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Converter for transforming any input stream with a given mime-type
 * into SAX events.
 * This component acts like a selector. All XMLizer can "register"
 * themselfes for a given mime-type and this component forwards
 * the transformation to the registered on.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/24 07:46:30 $
 */

public class XMLizerImpl
    extends DefaultComponentSelector
    implements XMLizer, ThreadSafe
{

    /** The default mimeType used when no mimeType is given */
    protected String defaultMimeType = "text/xml";

    /**
     * Generates SAX events from the given input stream
     * <b>NOTE</b> : if the implementation can produce lexical events, care should be taken
     * that <code>handler</code> can actually be a {@link XMLConsumer} that accepts such
     * events or directly implements the LexicalHandler interface!
     * @param stream    the data
     * @param mimeType  the mime-type for the data
     * @param systemID  the URI defining the data (this is optional and can be null)
     * @throws ComponentException if no suitable converter is found
     */
    public void toSAX( InputStream    stream,
                       String         mimeType,
                       String         systemID,
                       ContentHandler handler )
        throws SAXException, IOException, ComponentException
    {
        if ( null == stream ) {
            throw new ComponentException("Stream must not be null.");
        }
        if ( null == handler ) {
            throw new ComponentException("Handler must not be null.");
        }
        if ( null == mimeType ) {
            if ( this.getLogger().isDebugEnabled() ) {
                this.getLogger().debug("No mime-type for xmlizing " + systemID + ", guessing " + this.defaultMimeType);
            }
            mimeType = this.defaultMimeType;
        }

        if ( !this.hasComponent ( mimeType ) ) {
            throw new ComponentException("No XMLizer registered for mimeType " + mimeType);
        }

        XMLizer realXMLizer = null;
        try
        {
            realXMLizer = (XMLizer) this.select( mimeType );
            realXMLizer.toSAX( stream, mimeType, systemID, handler );
        } finally {
            this.release( realXMLizer );
        }

    }

}

