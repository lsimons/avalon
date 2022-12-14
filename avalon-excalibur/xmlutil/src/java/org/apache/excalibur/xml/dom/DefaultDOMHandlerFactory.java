/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.excalibur.xml.dom;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.5 $ $Date: 2004/02/28 11:47:31 $
 * @avalon.component
 */
public class DefaultDOMHandlerFactory
    extends AbstractLogEnabled
    implements DOMHandlerFactory, Initializable, Component, ThreadSafe
{
    private final SAXTransformerFactory m_transformerFactory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
    private final DocumentBuilderFactory m_documentBuilderFactory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder m_documentBuilder;

    public void initialize()
        throws Exception
    {
        m_documentBuilderFactory.setNamespaceAware( true );
        m_documentBuilder = m_documentBuilderFactory.newDocumentBuilder();
    }

    public DOMHandler createDOMHandler()
        throws Exception
    {
        final Document document = m_documentBuilder.newDocument();
        return createDOMHandler( document );
    }

    public DOMHandler createDOMHandler( final Document document )
        throws Exception
    {
        final TransformerHandler transformerHandler =
            m_transformerFactory.newTransformerHandler();
        return new DefaultDOMHandler( transformerHandler, document );
    }
}
