/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.xml.dom;

import java.io.IOException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The parser can be used to parse any XML document given
 * by a {@link InputSource} object.
 * It can create a DOM from the parsed document.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/01/15 08:35:56 $
 */
public interface DOMParser
{
    String ROLE = DOMParser.class.getName();

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
