/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

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

package org.apache.avalon.phoenix.tools.configuration;

import java.io.IOException;
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
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.14 $ $Date: 2003/03/22 12:07:15 $
 */
public class ConfigurationBuilder
{
    private static final DTDInfo[] c_dtdInfo = new DTDInfo[]
    {
        new DTDInfo( "-//PHOENIX/Assembly DTD Version 1.0//EN",
                     "http://jakarta.apache.org/phoenix/assembly_1_0.dtd",
                     "org/apache/avalon/phoenix/tools/assembly.dtd" ),
        new DTDInfo( "-//PHOENIX/Mx Info DTD Version 1.0//EN",
                     "http://jakarta.apache.org/phoenix/mxinfo_1_0.dtd",
                     "org/apache/avalon/phoenix/tools/mxinfo.dtd" ),
        new DTDInfo( "-//PHOENIX/Assembly DTD Version 1.0//EN",
                     "http://jakarta.apache.org/avalon/dtds/phoenix/assembly_1_0.dtd",
                     "org/apache/avalon/phoenix/tools/assembly.dtd" ),
        new DTDInfo( "-//PHOENIX/Mx Info DTD Version 1.0//EN",
                     "http://jakarta.apache.org/avalon/dtds/phoenix/mxinfo_1_0.dtd",
                     "org/apache/avalon/phoenix/tools/mxinfo.dtd" ),
        new DTDInfo( "-//PHOENIX/Assembly DTD Version 1.0//EN",
                     "http://jakarta.apache.org/phoenix/assembly_1.0.dtd",
                     "org/apache/avalon/phoenix/tools/assembly.dtd" ),
        new DTDInfo( "-//PHOENIX/Mx Info DTD Version 1.0//EN",
                     "http://jakarta.apache.org/phoenix/mxinfo_1.0.dtd",
                     "org/apache/avalon/phoenix/tools/mxinfo.dtd" ),
        new DTDInfo( "-//PHOENIX/Assembly DTD Version 1.0//EN",
                     "http://jakarta.apache.org/avalon/dtds/phoenix/assembly_1.0.dtd",
                     "org/apache/avalon/phoenix/tools/assembly.dtd" ),
        new DTDInfo( "-//PHOENIX/Mx Info DTD Version 1.0//EN",
                     "http://jakarta.apache.org/avalon/dtds/phoenix/mxinfo_1.0.dtd",
                     "org/apache/avalon/phoenix/tools/mxinfo.dtd" ),
        new DTDInfo( "-//PHOENIX/Assembly DTD Version 1.1//EN",
                     "http://jakarta.apache.org/phoenix/assembly_1.1.dtd",
                     "org/apache/avalon/phoenix/tools/assembly.dtd" )
    };

    private static final DTDResolver c_resolver =
        new DTDResolver( c_dtdInfo, ConfigurationBuilder.class.getClassLoader() );

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
                                        final SAXConfigurationHandler handler,
                                        final boolean validate )
        throws SAXException
    {
        reader.setEntityResolver( c_resolver );
        reader.setContentHandler( handler );
        reader.setErrorHandler( handler );

        if( validate )
        {
            // Request validation
            reader.setFeature( "http://xml.org/sax/features/validation", true );
        }
    }

    /**
     * Build a configuration object using an URI
     */
    public static Configuration build( final String uri )
        throws SAXException, ParserConfigurationException, IOException
    {
        return build( new InputSource( uri ) );
    }

    /**
     * Build a configuration object using an URI, and
     * optionally validate the xml against the DTD.
     */
    public static Configuration build( final String uri, final boolean validate )
        throws SAXException, ParserConfigurationException, IOException
    {
        return build( new InputSource( uri ), validate );
    }

    /**
     * Build a configuration object using an XML InputSource object
     */
    public static Configuration build( final InputSource input )
        throws SAXException, ParserConfigurationException, IOException
    {
        return build( input, false );
    }

    /**
     * Build a configuration object using an XML InputSource object, and
     * optionally validate the xml against the DTD.
     */
    public static Configuration build( final InputSource input, final boolean validate )
        throws SAXException, ParserConfigurationException, IOException
    {
        final XMLReader reader = createXMLReader();
        final SAXConfigurationHandler handler = new SAXConfigurationHandler();
        setupXMLReader( reader, handler, validate );
        reader.parse( input );
        return handler.getConfiguration();
    }
}
