/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.xml.sax;

import org.apache.avalon.framework.activity.Initializable;
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
import org.apache.excalibur.xml.sax.Parser;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import java.io.IOException;
import java.util.Properties;

/**
 * Converter for transforming an input stream contain text/html data
 * to SAX events.
 *
 * @author <a href="mailto:mirceatoma@apache.org">Mircea Toma</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/12/06 22:08:50 $
 */
public final class JTidyHTMLParser extends AbstractLogEnabled
        implements Parser, Serviceable, Configurable, Initializable, ThreadSafe
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
}

