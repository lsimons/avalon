/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.xml.dom;

import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.TransformerHandler;
import org.apache.avalon.excalibur.xml.ContentHandlerWrapper;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:mirceatoma@apache.org">Mircea Toma</a>
 * @version CVS $Revision: 1.7 $ $Date: 2002/11/12 23:35:34 $
 */
public class DefaultDOMHandler
    extends ContentHandlerWrapper
    implements DOMHandler
{
    private final Document m_document;

    public DefaultDOMHandler( TransformerHandler handler,
                              Document document )
    {
        super( handler, handler );
        m_document = document;
        handler.setResult( new DOMResult( m_document ) );
    }

    public Document getDocument()
    {
        return m_document;
    }
}
