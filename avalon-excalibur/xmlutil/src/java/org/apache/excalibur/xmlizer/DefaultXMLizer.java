/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.excalibur.xmlizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.xml.sax.SAXParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Converter for transforming any input stream with a given mime-type
 * into SAX events.
 * This component acts like a selector. All XMLizer can "register"
 * themselfes for a given mime-type and this component forwards
 * the transformation to the registered on.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @author <a href="mailto:mirceatoma@apache.org">Mircea Toma</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 12:47:42 $
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
            final String role = parser.getAttribute("role");
            m_mimeTypes.put(mimeType, role);
            if ( getLogger().isDebugEnabled() )
            {
                getLogger().debug("XMLizer: Registering parser '"+role+"' for mime-type '"+mimeType+"'.");
            }
        }
        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug("XMLizer: Default parser is '"+SAXParser.ROLE+"'.");
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
            parserRole = SAXParser.ROLE;
        }

        SAXParser parser = null;
        try
        {
            parser = (SAXParser) m_serviceManager.lookup( parserRole );

            final InputSource inputSource = new InputSource( stream );
            inputSource.setSystemId( systemID );
            parser.parse( inputSource, handler, null );
        }
        catch ( ServiceException e )
        {
            throw new SAXException( "Cannot parse content of type " + mimeType, e );
        }
        finally 
        {
            m_serviceManager.release(parser);        
        }
    }
}

