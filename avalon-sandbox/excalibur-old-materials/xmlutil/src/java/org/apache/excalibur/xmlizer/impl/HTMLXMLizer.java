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
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.avalon.excalibur.xml.Parser;
import org.apache.excalibur.xmlizer.XMLizer;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.thread.ThreadSafe;
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
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/24 07:46:30 $
 */

public class HTMLXMLizer
    extends AbstractLogEnabled
    implements XMLizer, ThreadSafe, Composable
{

    /** The component manager */
    protected ComponentManager manager;

    /** Used for converting DOM -> SAX */
    protected static Properties format;

    static {
        Properties format = new Properties();
        format.put(OutputKeys.METHOD, "xml");
        format.put(OutputKeys.OMIT_XML_DECLARATION, "no");
        format.put(OutputKeys.INDENT, "yes");
    }

    /**
     * Composable interface
     */
    public void compose(ComponentManager manager)
    {
        this.manager = manager;
    }

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
                this.getLogger().debug("No mime-type for xmlizing " + systemID +
                                       ", guessing text/html");
            }
        } else if ( !mimeType.equalsIgnoreCase("text/html") ) {
            if ( this.getLogger().isDebugEnabled() ) {
                this.getLogger().debug("Mime-type " + mimeType +
                                       "not supported for xmlizing " + systemID +
                                       ", guessing text/html");
            }
        }

        final Tidy xhtmlconvert = new Tidy();
        xhtmlconvert.setXmlOut(true);
        xhtmlconvert.setXHTML(true);
        xhtmlconvert.setShowWarnings(false);
        final StringWriter writer = new StringWriter();
        try {
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperties(format);
            transformer.transform(new DOMSource(xhtmlconvert.parseDOM(stream, null)),
                              new StreamResult(writer));
        } catch (TransformerException te) {
            throw new SAXException("Exception during transformation.", te);
        }
        final InputSource inputSource =
              new InputSource( new java.io.StringReader(writer.toString()) );
        if ( null != systemID) inputSource.setSystemId( systemID );

        Parser parser = null;
        try
        {
            parser = (Parser)this.manager.lookup( Parser.ROLE );

            parser.parse( inputSource, handler );
        }
        finally
        {
            if( parser != null ) this.manager.release( parser );
        }
    }

}

