/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.xml.dom;

import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/**
 * Converts a DOM document to a stream of SAX events.
 *
 * @author <a href="mailto:mirceatoma@apache.org">Mircea Toma</a>
 * @version CVS $Revision: 1.4 $ $Date: 2002/11/12 23:35:34 $
 */
public interface DOMSerializer
{
    String ROLE = DOMSerializer.class.getName();

    void serialize( Document document,
                    ContentHandler contentHandler,
                    LexicalHandler lexicalHandler )
        throws SAXException;
}
