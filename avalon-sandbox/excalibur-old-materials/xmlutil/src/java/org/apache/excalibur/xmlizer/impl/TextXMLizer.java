/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.xmlizer.impl;

import java.io.IOException;
import java.io.InputStream;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Converter for transforming an input stream contain text/xml data
 * to SAX events.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.6 $ $Date: 2002/08/04 04:19:58 $
 */
public final class TextXMLizer
    extends AbstractXMLizer
{
    private static final String XML_MIME_TYPE = "text/xml";

    public TextXMLizer()
    {
        super( XML_MIME_TYPE );
    }

    protected void toSAX( final InputStream stream,
                          final String systemID,
                          final ContentHandler handler )
        throws SAXException, IOException
    {
        final InputSource inputSource = new InputSource( stream );
        if( null != systemID )
        {
            inputSource.setSystemId( systemID );
        }

        getParser().parse( inputSource, handler );
    }
}