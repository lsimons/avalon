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
package org.apache.excalibur.xml.sax;

import java.io.IOException;
import java.util.Properties;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.xml.dom.DOMSerializer;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/**
 * Converter for transforming an input stream contain text/html data
 * to SAX events.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.4 $ $Date: 2004/02/28 11:47:20 $
 */
public final class JTidyHTMLParser extends AbstractLogEnabled
        implements SAXParser, Serviceable, Configurable, Initializable, ThreadSafe, Component
{
    private DOMSerializer m_serializer;
    private Tidy m_tidy;
    private Properties m_properties;

    public void service( ServiceManager serviceManager ) throws ServiceException
    {
        m_serializer = (DOMSerializer) serviceManager.lookup( DOMSerializer.ROLE );
    }

    public void configure( Configuration configuration ) throws ConfigurationException
    {
        final Parameters parameters = Parameters.fromConfiguration( configuration );
        m_properties = Parameters.toProperties( parameters );
    }

    public void initialize() throws Exception
    {
        m_tidy = new Tidy();

        //default options.
        m_tidy.setXmlOut( true );
        m_tidy.setXHTML( true );
        m_tidy.setShowWarnings( false );

        m_tidy.setConfigurationFromProps( m_properties );
    }

    public void parse( InputSource in,
                       ContentHandler contentHandler,
                       LexicalHandler lexicalHandler )
            throws SAXException, IOException
    {
        final Document document = m_tidy.parseDOM( in.getByteStream(), null );
        m_serializer.serialize( document, contentHandler, lexicalHandler );
    }
    
    /**
     * Parse the {@link InputSource} and send
     * SAX events to the consumer.
     * Attention: the consumer can  implement the
     * {@link LexicalHandler} as well.
     * The parse should take care of this.
     */
    public void parse( InputSource in, ContentHandler consumer )
        throws SAXException, IOException
    {
        this.parse( in, consumer, 
                    (consumer instanceof LexicalHandler ? (LexicalHandler)consumer : null));
    }
    
}

