/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.xml;

import java.io.IOException;
import org.apache.avalon.framework.component.Component;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/**
 * The parser can be used to parse any XML document given
 * by a {@link InputSource} object.
 * It can either send XML events or create a DOM from
 * the parsed document.
 *
 * @deprecated Moved to org.apache.excalibur.xml.sax package. Removed dependency
 * on Component. Removed DOM functionality.
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.3 $ $Date: 2002/10/16 00:04:47 $
 */
public interface Parser
    extends Component
{
    String ROLE = Parser.class.getName();

    /**
     * Parse the {@link InputSource} and send
     * SAX events to the consumer.
     * Attention: the consumer can  implement the
     * {@link LexicalHandler} as well.
     * The parse should take care of this.
     */
    void parse( InputSource in, ContentHandler consumer )
        throws SAXException, IOException;

    /**
     * Parse the {@link InputSource} and send
     * SAX events to the content handler and
     * the lexical handler.
     */
    void parse( InputSource in,
                ContentHandler contentHandler,
                LexicalHandler lexicalHandler )
        throws SAXException, IOException;

    /**
     * Parse the {@link InputSource} and create
     * a DOM out of it.
     */
    Document parseDocument( InputSource in )
        throws SAXException, IOException;

    /**
     * Return a new {@link Document}.
     */
    Document createDocument() throws SAXException;
}
