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
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.tidy.Tidy;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Converter for transforming an input stream contain text/html data
 * to SAX events.
 * This class uses jtidy.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.11 $ $Date: 2002/08/04 04:19:58 $
 */
public final class HTMLXMLizer
    extends AbstractXMLizer
{
    private static final String HTML_MIME_TYPE = "text/html";

    /** Used for converting DOM -> SAX */
    private static final Properties c_format = createFormatProperties();

    public HTMLXMLizer()
    {
        super( HTML_MIME_TYPE );
    }

    protected void toSAX( final InputStream stream,
                          final String systemID,
                          final ContentHandler handler )
        throws SAXException, IOException
    {
        final Tidy xhtmlconvert = new Tidy();
        xhtmlconvert.setXmlOut( true );
        xhtmlconvert.setXHTML( true );
        xhtmlconvert.setShowWarnings( false );

        final StringWriter writer = new StringWriter();
        try
        {
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperties( c_format );
            final DOMSource domSource = new DOMSource( xhtmlconvert.parseDOM( stream, null ) );
            final StreamResult streamResult = new StreamResult( writer );
            transformer.transform( domSource, streamResult );
        }
        catch( final TransformerException te )
        {
            final String message = "Exception during transformation.";
            throw new SAXException( message, te );
        }

        final InputSource inputSource =
            new InputSource( new StringReader( writer.toString() ) );
        if( null != systemID )
        {
            inputSource.setSystemId( systemID );
        }

        getParser().parse( inputSource, handler );
    }

    /**
     * Utility method to create format properties for XMLizer.
     */
    private static Properties createFormatProperties()
    {
        final Properties format = new Properties();
        format.put( OutputKeys.METHOD, "xml" );
        format.put( OutputKeys.OMIT_XML_DECLARATION, "no" );
        format.put( OutputKeys.INDENT, "yes" );
        return format;
    }
}

