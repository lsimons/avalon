/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.avalon.excalibur.xml;

import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;

/**
 * ContentHandler encapsulating a DOM document. The document tree is built
 * from SAX events sent to the handler.
 *
 * @author <a href="mailto:mirceatoma@apache.org">Mircea Toma</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/08/02 05:00:18 $
 */
public interface DOMHandler extends ContentHandler {
    
    Document getDocument();
    
}
