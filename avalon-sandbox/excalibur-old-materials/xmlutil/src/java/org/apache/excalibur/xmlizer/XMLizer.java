/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.xmlizer;

import java.io.IOException;
import java.io.InputStream;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Converter for transforming any input stream with a given mime-type
 * into SAX events.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.5 $ $Date: 2002/10/02 01:52:25 $
 */

public interface XMLizer
    extends Component
{
    String ROLE = XMLizer.class.getName();

    /**
     * Generates SAX events from the given input stream
     * <b>NOTE</b> : if the implementation can produce lexical events, care should be taken
     * that <code>handler</code> can actually be a
     * {@link org.apache.avalon.excalibur.xml.XMLConsumer} that accepts such
     * events or directly implements the LexicalHandler interface!
     * @param stream    the data
     * @param mimeType  the mime-type for the data
     * @param systemID  the URI defining the data (this is optional and can be null)
     * @throws ComponentException if no suitable converter is found
     * @todo Remove ComponentException as it has no place being part of the worker interface
     */
    void toSAX( InputStream stream,
                String mimeType,
                String systemID,
                ContentHandler handler )
        throws SAXException, IOException, ComponentException;
}

