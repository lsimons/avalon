/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.
 
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
 
 4. The names "Jakarta", "Apache Avalon", "Avalon Excalibur", "Avalon
    Framework" and "Apache Software Foundation"  must not be used to endorse
    or promote products derived  from this  software without  prior written
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
 on  behalf of the Apache Software  Foundation and was  originally created by
 Stefano Mazzocchi  <stefano@apache.org>. For more  information on the Apache 
 Software Foundation, please see <http://www.apache.org/>.
 
*/
package org.apache.avalon.framework.configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.NamespaceSupport;

/**
 * A ConfigurationSerializer serializes configurations via SAX2 compliant parser.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version 1.0
 */
public class DefaultConfigurationSerializer
{
    private SAXTransformerFactory m_tfactory;
    private TransformerHandler    m_handler;
    private OutputStream          m_out;
    private Properties            m_format = new Properties();
    private NamespaceSupport      m_namespaceSupport = new NamespaceSupport();

    /**
     * Build a ConfigurationSerializer
     */
    public DefaultConfigurationSerializer()
    {
        getTransformerFactory();
    }

    /**
     * Sets the Serializer's use of indentation.  This will cause linefeeds to be added
     *  after each element, but it does not add any indentation via spaces.
     * @param indent a <code>boolean</code> value
     */
    public void setIndent( boolean indent )
    {
        if ( indent )
        {
            m_format.put( OutputKeys.INDENT, "yes" );
        }
        else
        {
            m_format.put( OutputKeys.INDENT, "no" );
        }
    }

    /**
     * Internally set the output strream we will be using.
     * @param out an <code>OutputStream</code> value
     */
    protected void setOutputStream( final OutputStream out )
    {
        try
        {
            m_out = out;
            m_handler = getTransformerFactory().newTransformerHandler();
            m_format.put(OutputKeys.METHOD,"xml");
            m_handler.setResult(new StreamResult(out));
            m_handler.getTransformer().setOutputProperties( m_format );
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
        if(m_tfactory == null)
        {
            m_tfactory = (SAXTransformerFactory) TransformerFactory.newInstance();
        }

        return m_tfactory;
    }

    /**
     * Start the serialization process.  The output stream <strong>must</strong>
     * be set before calling this method.
     * @param source a <code>Configuration</code> value
     * @throws SAXException if an error occurs
     * @throws ConfigurationException if an error occurs
     */
    protected void serialize( final Configuration source )
        throws SAXException, ConfigurationException
    {
        m_namespaceSupport.reset();
        m_handler.startDocument();
        serializeElement(source);
        m_handler.endDocument();
    }

    /**
     * Serialize each Configuration element.  This method is called recursively.
     * @param element a <code>Configuration</code> value
     * @throws SAXException if an error occurs
     * @throws ConfigurationException if an error occurs
     */
    protected void serializeElement( final Configuration element )
        throws SAXException, ConfigurationException
    {
        m_namespaceSupport.pushContext();

        AttributesImpl attr = new AttributesImpl();
        String[] attrNames = element.getAttributeNames();

        if (null != attrNames)
        {
            for (int i = 0; i < attrNames.length; i++)
            {
                attr.addAttribute("", // namespace URI
                                  attrNames[i], // local name
                                  attrNames[i], // qName
                                  "CDATA",  // type
                                   element.getAttribute(attrNames[i], "") // value
                                 );
            }
        }

        final String nsURI = element.getNamespace();
        String nsPrefix = "";

        if ( element instanceof AbstractConfiguration )
        {
            nsPrefix = ((AbstractConfiguration) element).getPrefix();
        }
        // nsPrefix is guaranteed to be non-null at this point.

        boolean nsWasDeclared = false;

        final String existingURI = m_namespaceSupport.getURI( nsPrefix );

        // ie, there is no existing URI declared for this prefix or we're
        // remapping the prefix to a different URI
        if ( existingURI == null || !existingURI.equals( nsURI ) )
        {
            nsWasDeclared = true;
            if (nsPrefix.equals("") && nsURI.equals(""))
            {
                // implicit mapping; don't need to declare
            }
            else if (nsPrefix.equals(""))
            {
                // (re)declare the default namespace
                attr.addAttribute("", "xmlns", "xmlns", "CDATA", nsURI);
            }
            else
            {
                // (re)declare a mapping from nsPrefix to nsURI
                attr.addAttribute("", "xmlns:"+nsPrefix, "xmlns:"+nsPrefix, "CDATA", nsURI);
            }
            m_handler.startPrefixMapping( nsPrefix, nsURI );
            m_namespaceSupport.declarePrefix( nsPrefix, nsURI );
        }

        String localName = element.getName();
        String qName = element.getName();
        if ( nsPrefix == null || nsPrefix.length() == 0 )
        {
            qName = localName;
        }
        else
        {
            qName = nsPrefix + ":" + localName;
        }

        m_handler.startElement(nsURI, localName, qName, attr);

        String value = element.getValue(null);

        if (null == value)
        {
            Configuration[] children = element.getChildren();

            for (int i = 0; i < children.length; i++)
            {
                serializeElement(children[i]);
            }
        }
        else
        {
            m_handler.characters(value.toCharArray(), 0, value.length());
        }

        m_handler.endElement(nsURI, localName, qName);

        if ( nsWasDeclared )
        {
            m_handler.endPrefixMapping( nsPrefix );
        }

        m_namespaceSupport.popContext();
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
        serialize( new FileOutputStream( file ), source );
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
        synchronized(this)
        {
            setOutputStream( outputStream );
            serialize( source );
        }
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
        serialize( new URL( uri ).openConnection().getOutputStream(), source );
    }
}
