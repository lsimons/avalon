/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
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
package org.apache.avalon.framework.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * A DefaultConfigurationBuilder builds <code>Configuration</code>s from XML,
 * via a SAX2 compliant parser.
 *
 * <p>
 * XML namespace support is optional, and disabled by default to preserve
 * backwards-compatibility. To enable it, pass the {@link
 * #DefaultConfigurationBuilder(boolean)} constructor the flag <code>true</code>, or pass
 * a namespace-enabled <code>XMLReader</code> to the {@link
 * #DefaultConfigurationBuilder(XMLReader)} constructor.
 * </p>
 * <p>
 * The mapping from XML namespaces to {@link Configuration} namespaces is pretty
 * straightforward, with one caveat: attribute namespaces are (deliberately) not
 * supported. Enabling namespace processing has the following effects:</p>
 * <ul>
 *  <li>Attributes starting with <code>xmlns:</code> are interpreted as
 *  declaring a prefix:namespaceURI mapping, and won't result in the creation of
 *  <code>xmlns</code>-prefixed attributes in the <code>Configuration</code>.
 *  </li>
 *  <li>
 *  Prefixed XML elements, like <tt>&lt;doc:title xmlns:doc="http://foo.com"&gt;,</tt>
 *  will result in a <code>Configuration</code> with <code>{@link
 *  Configuration#getName getName()}.equals("title")</code> and <code>{@link
 *  Configuration#getNamespace getNamespace()}.equals("http://foo.com")</code>.
 *  </li>
 * </ul>
 * <p>
 * Whitespace handling. Since mixed content is not allowed in the
 * configurations, whitespace is completely discarded in non-leaf nodes.
 * For the leaf nodes the default behavior is to trim the space
 * surrounding the value. This can be changed by specifying
 * <code>xml:space</code> attribute with value of <code>preserve</code>
 * in that case the whitespace is left intact.
 * </p>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.26 $ $Date: 2003/02/11 16:19:27 $
 */
public class DefaultConfigurationBuilder
{
    private SAXConfigurationHandler m_handler;
    private XMLReader m_parser;

    /**
     * Create a Configuration Builder with a default XMLReader that ignores
     * namespaces.  In order to enable namespaces, use either the constructor
     * that has a boolean or that allows you to pass in your own
     * namespace-enabled XMLReader.
     */
    public DefaultConfigurationBuilder()
    {
        this( false );
    }

    /**
     * Create a Configuration Builder, specifying a flag that determines
     * namespace support.
     *
     * @param enableNamespaces If <code>true</code>,  a namespace-aware
     * <code>SAXParser</code> is used. If <code>false</code>, the default JAXP
     * <code>SAXParser</code> (without namespace support) is used.
     * @since 4.1
     */
    public DefaultConfigurationBuilder( final boolean enableNamespaces )
    {
        //yaya the bugs with some compilers and final variables ..
        try
        {
            final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

            if( enableNamespaces )
            {
                saxParserFactory.setNamespaceAware( true );
            }

            final SAXParser saxParser = saxParserFactory.newSAXParser();
            this.setParser( saxParser.getXMLReader() );
        }
        catch( final Exception se )
        {
            throw new Error( "Unable to setup SAX parser" + se );
        }
    }

    /**
     * Create a Configuration Builder with your own XMLReader.
     * @param parser an <code>XMLReader</code>
     */
    public DefaultConfigurationBuilder( XMLReader parser )
    {
        this.setParser( parser );
    }

    /**
     * Internally sets up the XMLReader
     */
    private void setParser( XMLReader parser )
    {
        m_parser = parser;

        m_handler = getHandler();

        m_parser.setContentHandler( m_handler );
        m_parser.setErrorHandler( m_handler );
    }

    /**
     * Get a SAXConfigurationHandler for your configuration reading.
     * @return a <code>SAXConfigurationHandler</code>
     */
    protected SAXConfigurationHandler getHandler()
    {
        try
        {
            if( m_parser.getFeature( "http://xml.org/sax/features/namespaces" ) )
            {
                return new NamespacedSAXConfigurationHandler();
            }
        }
        catch( Exception e )
        {
            // ignore error and fall through to the non-namespaced version
        }

        return new SAXConfigurationHandler();
    }

    /**
     * Build a configuration object from a file using a filename.
     * @param filename name of the file
     * @return a <code>Configuration</code> object
     * @throws SAXException if a parsing error occurs
     * @throws IOException if an I/O error occurs
     * @throws ConfigurationException if an error occurs
     */
    public Configuration buildFromFile( final String filename )
        throws SAXException, IOException, ConfigurationException
    {
        return buildFromFile( new File( filename ) );
    }

    /**
     * Build a configuration object from a file using a File object.
     * @param file a <code>File</code> object
     * @return a <code>Configuration</code> object
     * @throws SAXException if a parsing error occurs
     * @throws IOException if an I/O error occurs
     * @throws ConfigurationException if an error occurs
     */
    public Configuration buildFromFile( final File file )
        throws SAXException, IOException, ConfigurationException
    {
        synchronized( this )
        {
            m_handler.clear();
            m_parser.parse( file.toURL().toString() );
            return m_handler.getConfiguration();
        }
    }

    /**
     * Build a configuration object using an InputStream.
     * @param inputStream an <code>InputStream</code> value
     * @return a <code>Configuration</code> object
     * @throws SAXException if a parsing error occurs
     * @throws IOException if an I/O error occurs
     * @throws ConfigurationException if an error occurs
     */
    public Configuration build( final InputStream inputStream )
        throws SAXException, IOException, ConfigurationException
    {
        return build( new InputSource( inputStream ) );
    }

    /**
     * Build a configuration object using an URI
     * @param uri a <code>String</code> value
     * @return a <code>Configuration</code> object
     * @throws SAXException if a parsing error occurs
     * @throws IOException if an I/O error occurs
     * @throws ConfigurationException if an error occurs
     */
    public Configuration build( final String uri )
        throws SAXException, IOException, ConfigurationException
    {
        return build( new InputSource( uri ) );
    }

    /**
     * Build a configuration object using an XML InputSource object
     * @param input an <code>InputSource</code> value
     * @return a <code>Configuration</code> object
     * @throws SAXException if a parsing error occurs
     * @throws IOException if an I/O error occurs
     * @throws ConfigurationException if an error occurs
     */
    public Configuration build( final InputSource input )
        throws SAXException, IOException, ConfigurationException
    {
        synchronized( this )
        {
            m_handler.clear();
            m_parser.parse( input );
            return m_handler.getConfiguration();
        }
    }
}
