/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.xmlizer;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.xml.sax.Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Converter for transforming any input stream with a given mime-type
 * into SAX events.
 * This component acts like a selector. All XMLizer can "register"
 * themselfes for a given mime-type and this component forwards
 * the transformation to the registered on.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/12/06 22:11:05 $
 */
public final class DefaultXMLizer extends AbstractLogEnabled
        implements XMLizer, Serviceable, Configurable, ThreadSafe, Component
{
    private ServiceManager m_serviceManager;
    private Map m_mimeTypes = new HashMap();

    public void service( ServiceManager serviceManager ) throws ServiceException
    {
        m_serviceManager = serviceManager;
    }

    public void configure( Configuration configuration ) throws ConfigurationException
    {
        final Configuration[] parsers = configuration.getChildren("parser");
        for ( int i = 0; i < parsers.length; i++ )
        {
            final Configuration parser = parsers[i];
            final String mimeType = parser.getAttribute("mime-type");
            final String role = parser.getAttribute("parser");
            m_mimeTypes.put(mimeType, role);
        }
    }

    /**
     * Generates SAX events from the given input stream
     * @param stream    the data
     * @param mimeType  the mime-type for the data
     * @param systemID  the URI defining the data (this is optional and can be null)
     */
    public void toSAX( final InputStream stream,
                       final String mimeType,
                       final String systemID,
                       final ContentHandler handler )
            throws SAXException, IOException
    {
        if ( null == stream )
        {
            throw new NullPointerException( "stream" );
        }
        if ( null == handler )
        {
            throw new NullPointerException( "handler" );
        }

        final String parserRole;
        if ( m_mimeTypes.containsKey(mimeType) )
        {
            parserRole = (String) m_mimeTypes.get(mimeType);
        }
        else
        {
            if ( getLogger().isDebugEnabled() )
            {
                final String message = "No mime-type for xmlizing " + systemID +
                        ", guessing text/xml";
                getLogger().debug( message );
            }
            parserRole = Parser.ROLE;
        }

        try
        {
            final Parser parser = (Parser) m_serviceManager.lookup( parserRole );

            final InputSource inputSource = new InputSource( stream );
            inputSource.setSystemId( systemID );
            parser.parse( inputSource, handler, null );
        }
        catch ( ServiceException e )
        {
            throw new SAXException( "Cannot parse content of type" + mimeType );
        }
    }
}

