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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Properties;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.NamespaceSupport;

/**
 * A ConfigurationSerializer serializes configurations via SAX2 compliant parser.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.24 $ $Date: 2003/02/11 15:58:39 $
 */
public class DefaultConfigurationSerializer
{
    private SAXTransformerFactory m_tfactory;
    private Properties m_format = new Properties();

    /**
     * Sets the Serializer's use of indentation.  This will cause linefeeds to be added
     *  after each element, but it does not add any indentation via spaces.
     * @param indent a <code>boolean</code> value
     */
    public void setIndent( boolean indent )
    {
        if( indent )
        {
            m_format.put( OutputKeys.INDENT, "yes" );
        }
        else
        {
            m_format.put( OutputKeys.INDENT, "no" );
        }
    }

    /**
     * Create a ContentHandler for an OutputStream
     * @param result the result
     * @return contenthandler that goes to specified OutputStream
     */
    protected ContentHandler createContentHandler( final Result result )
    {
        try
        {
            TransformerHandler handler = getTransformerFactory().newTransformerHandler();

            m_format.put( OutputKeys.METHOD, "xml" );
            handler.setResult( result );
            handler.getTransformer().setOutputProperties( m_format );

            return handler;
        }
        catch( final Exception e )
        {
            throw new RuntimeException( e.toString() );
        }
    }

    /**
     * Get the SAXTransformerFactory so we can get a serializer without being
     * tied to one vendor.
     * @return a <code>SAXTransformerFactory</code> value
     */
    protected SAXTransformerFactory getTransformerFactory()
    {
        if( m_tfactory == null )
        {
            m_tfactory = (SAXTransformerFactory)TransformerFactory.newInstance();
        }

        return m_tfactory;
    }

    /**
     * Serialize the configuration to a ContentHandler
     * @param handler a <code>ContentHandler</code> to serialize to
     * @param source a <code>Configuration</code> value
     * @throws SAXException if an error occurs
     * @throws ConfigurationException if an error occurs
     */
    public void serialize( final ContentHandler handler, final Configuration source )
        throws SAXException, ConfigurationException
    {
        handler.startDocument();
        serializeElement( handler, new NamespaceSupport(), source );
        handler.endDocument();
    }

    /**
     * Serialize each Configuration element.  This method is called recursively.
     * @param handler a <code>ContentHandler</code> to use
     * @param namespaceSupport a <code>NamespaceSupport</code> to use
     * @param element a <code>Configuration</code> value
     * @throws SAXException if an error occurs
     * @throws ConfigurationException if an error occurs
     */
    protected void serializeElement( final ContentHandler handler,
                                     final NamespaceSupport namespaceSupport,
                                     final Configuration element )
        throws SAXException, ConfigurationException
    {
        namespaceSupport.pushContext();

        AttributesImpl attr = new AttributesImpl();
        String[] attrNames = element.getAttributeNames();

        if( null != attrNames )
        {
            for( int i = 0; i < attrNames.length; i++ )
            {
                attr.addAttribute( "", // namespace URI
                                   attrNames[ i ], // local name
                                   attrNames[ i ], // qName
                                   "CDATA", // type
                                   element.getAttribute( attrNames[ i ], "" ) // value
                );
            }
        }

        final String nsURI = element.getNamespace();
        String nsPrefix = "";

        if( element instanceof AbstractConfiguration )
        {
            nsPrefix = ( (AbstractConfiguration)element ).getPrefix();
        }
        // nsPrefix is guaranteed to be non-null at this point.

        boolean nsWasDeclared = false;

        final String existingURI = namespaceSupport.getURI( nsPrefix );

        // ie, there is no existing URI declared for this prefix or we're
        // remapping the prefix to a different URI
        if( existingURI == null || !existingURI.equals( nsURI ) )
        {
            nsWasDeclared = true;
            if( nsPrefix.equals( "" ) && nsURI.equals( "" ) )
            {
                // implicit mapping; don't need to declare
            }
            else if( nsPrefix.equals( "" ) )
            {
                // (re)declare the default namespace
                attr.addAttribute( "", "xmlns", "xmlns", "CDATA", nsURI );
            }
            else
            {
                // (re)declare a mapping from nsPrefix to nsURI
                attr.addAttribute( "", "xmlns:" + nsPrefix, "xmlns:" + nsPrefix, "CDATA", nsURI );
            }
            handler.startPrefixMapping( nsPrefix, nsURI );
            namespaceSupport.declarePrefix( nsPrefix, nsURI );
        }

        String localName = element.getName();
        String qName = element.getName();
        if( nsPrefix == null || nsPrefix.length() == 0 )
        {
            qName = localName;
        }
        else
        {
            qName = nsPrefix + ":" + localName;
        }

        handler.startElement( nsURI, localName, qName, attr );

        String value = element.getValue( null );

        if( null == value )
        {
            Configuration[] children = element.getChildren();

            for( int i = 0; i < children.length; i++ )
            {
                serializeElement( handler, namespaceSupport, children[ i ] );
            }
        }
        else
        {
            handler.characters( value.toCharArray(), 0, value.length() );
        }

        handler.endElement( nsURI, localName, qName );

        if( nsWasDeclared )
        {
            handler.endPrefixMapping( nsPrefix );
        }

        namespaceSupport.popContext();
    }

    /**
     * Serialize the configuration object to a file using a filename.
     * @param filename a <code>String</code> value
     * @param source a <code>Configuration</code> value
     * @throws SAXException if an error occurs
     * @throws IOException if an error occurs
     * @throws ConfigurationException if an error occurs
     */
    public void serializeToFile( final String filename, final Configuration source )
        throws SAXException, IOException, ConfigurationException
    {
        serializeToFile( new File( filename ), source );
    }

    /**
     * Serialize the configuration object to a file using a File object.
     * @param file a <code>File</code> value
     * @param source a <code>Configuration</code> value
     * @throws SAXException if an error occurs
     * @throws IOException if an error occurs
     * @throws ConfigurationException if an error occurs
     */
    public void serializeToFile( final File file, final Configuration source )
        throws SAXException, IOException, ConfigurationException
    {
        OutputStream outputStream = null;
        try
        {
            outputStream = new FileOutputStream( file );
            serialize( outputStream, source );
        }
        finally
        {
            if( outputStream != null )
            {
                outputStream.close();
            }
        }
    }

    /**
     * Serialize the configuration object to an output stream.
     * @param outputStream an <code>OutputStream</code> value
     * @param source a <code>Configuration</code> value
     * @throws SAXException if an error occurs
     * @throws IOException if an error occurs
     * @throws ConfigurationException if an error occurs
     */
    public void serialize( final OutputStream outputStream, final Configuration source )
        throws SAXException, IOException, ConfigurationException
    {
        serialize( createContentHandler( new StreamResult( outputStream ) ), source );
    }

    /**
     * Serialize the configuration object to an output stream derived from an
     * URI.  The URI must be resolveable by the <code>java.net.URL</code> object.
     * @param uri a <code>String</code> value
     * @param source a <code>Configuration</code> value
     * @throws SAXException if an error occurs
     * @throws IOException if an error occurs
     * @throws ConfigurationException if an error occurs
     */
    public void serialize( final String uri, final Configuration source )
        throws SAXException, IOException, ConfigurationException
    {
        OutputStream outputStream = null;
        try
        {
            outputStream = new URL( uri ).openConnection().getOutputStream();
            serialize( outputStream, source );
        }
        finally
        {
            if( outputStream != null )
            {
                outputStream.close();
            }
        }
    }

    /**
     * Serialize the configuration object to a string
     * @param source a <code>Configuration</code> value
     * @return configuration serialized as a string.
     * @throws SAXException if an error occurs
     * @throws ConfigurationException if an error occurs
     */
    public String serialize( final Configuration source )
        throws SAXException, ConfigurationException
    {
        final StringWriter writer = new StringWriter();

        serialize( createContentHandler( new StreamResult( writer ) ), source );

        return writer.toString();
    }
}
