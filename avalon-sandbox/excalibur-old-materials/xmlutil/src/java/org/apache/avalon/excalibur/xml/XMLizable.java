/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.xml;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * This interface can be implemented by classes willing to provide an XML representation
 * of their current state as SAX events.
 *
 * @author <a href="mailto:sylvain@apache.org">Sylvain Wallez</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/22 10:06:04 $
 */

public interface XMLizable
{
    /**
     * Generates SAX events representing the object's state.
     * <b>NOTE</b> : if the implementation can produce lexical events, care should be taken
     * that <code>handler</code> can actually be a {@link XMLConsumer} that accepts such
     * events or directly implements the LexicalHandler interface!
     */
    void toSAX( ContentHandler handler ) throws SAXException;

}
