/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.avalon.meta;

import java.io.IOException;
import java.net.MalformedURLException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.SAXConfigurationHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Utility class used to load Configuration trees from XML files.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 08:15:22 $
 */
public class ConfigurationBuilder
{
    private static final DTDInfo[] DTD_INFO = new DTDInfo[]
    {
        new DTDInfo( "-//AVALON/Component Type DTD Version 1.0//EN",
                     "http://avalon.apache.org/dtds/meta/type_1_0.dtd",
                     "org/apache/avalon/meta/type.dtd" ),
        new DTDInfo( "-//AVALON/Component Type DTD Version 1.1//EN",
                     "http://avalon.apache.org/dtds/meta/type_1_1.dtd",
                     "org/apache/avalon/meta/type.dtd" ),
        new DTDInfo( "-//AVALON/Service DTD Version 1.0//EN",
                     "http://avalon.apache.org/dtds/meta/service_1_0.dtd",
                     "org/apache/avalon/meta/service.dtd" ),
        new DTDInfo( "-//PHOENIX/Block Info DTD Version 1.0//EN",
                     "http://avalon.apache.org/dtds/phoenix/blockinfo_1.0.dtd",
                     "org/apache/avalon/meta/blockinfo.dtd" ),
    };

    private static final DTDResolver DTD_RESOLVER =
        new DTDResolver( DTD_INFO, ConfigurationBuilder.class.getClassLoader() );

    /**
     * Private constructor to block instantiation.
     */
    private ConfigurationBuilder()
    {
    }

    /**
     * Utility method to create a new XML reader.
     */
    private static XMLReader createXMLReader()
        throws SAXException, ParserConfigurationException
    {
        final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware( false );
        final SAXParser saxParser = saxParserFactory.newSAXParser();
        return saxParser.getXMLReader();
    }

    /**
     * Internally sets up the XMLReader
     */
    private static void setupXMLReader( final XMLReader reader,
                                        final SAXConfigurationHandler handler )
    {
        reader.setEntityResolver( DTD_RESOLVER );
        reader.setContentHandler( handler );
        reader.setErrorHandler( handler );
    }

    /**
     * Build a configuration object using an URI
     * @param uri an input source system identifier
     * @return the contfiguration instance
     * @exception SAXException is a parser exception is encountered
     * @exception ParserConfigurationException if a parser configuration failure occurs
     * @exception IOException if an IO exception occurs while attempting to read the
     *    resource identified by the system identifier
     */
    public static Configuration build( final String uri )
        throws SAXException, ParserConfigurationException, IOException
    {
        try
        {
            return build( new InputSource( uri ) );
        }
        catch( MalformedURLException mue )
        {
            final String error = 
              "Invalid input source uri: " + uri;
            throw new IOException( error );
        }
    }

    /**
     * Build a configuration object using an XML InputSource object
     * @param input an input source
     * @return the contfiguration instance
     * @exception SAXException is a parser exception is encountered
     * @exception ParserConfigurationException if a parser configuration failure occurs
     * @exception IOException if an IO exception occurs while attempting to read the
     *    resource associated with the input source
     */
    public static Configuration build( final InputSource input )
        throws SAXException, ParserConfigurationException, IOException
    {
        if( input == null )
        {
            throw new NullPointerException( "input" );
        }

        final XMLReader reader = createXMLReader();
        final SAXConfigurationHandler handler = new SAXConfigurationHandler();
        setupXMLReader( reader, handler );
        reader.parse( input );
        return handler.getConfiguration();
    }
}
