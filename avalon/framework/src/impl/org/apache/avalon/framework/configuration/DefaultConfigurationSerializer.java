/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.NamespaceSupport;

/**
 * A ConfigurationSerializer serializes configurations via SAX2 compliant parser.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
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
     * Internally set the output strream we will be using.
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
     */
    protected void serialize( final Configuration source )
        throws SAXException
    {
        m_namespaceSupport.reset();
        m_handler.startDocument();
        serializeElement(source);
        m_handler.endDocument();
    }

    /**
     * Serialize each Configuration element.  This method is called recursively.
     */
    protected void serializeElement( final Configuration element )
        throws SAXException
    {
        m_namespaceSupport.pushContext();

        AttributesImpl attr = new AttributesImpl();
        String[] attrNames = element.getAttributeNames();

        if (null != attrNames)
        {
            for (int i = 0; i < attrNames.length; i++)
            {
                attr.addAttribute("", attrNames[i], attrNames[i], "CDATA",
                                  element.getAttribute(attrNames[i], ""));
            }
        }

        final Namespace namespace = element.getNamespace();
        final String nsURI = namespace.getURI();
        final String nsPrefix = namespace.getPrefix();
        boolean nsWasDeclared = false;

        // Is this namespace already declared?
        final String existingURI = m_namespaceSupport.getURI( nsPrefix );
        if ( existingURI == null || !existingURI.equals( nsURI ) )
        {
            nsWasDeclared = true;
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
     */
    public void serializeToFile( final String filename, final Configuration source )
        throws SAXException, IOException, ConfigurationException
    {
        serializeToFile( new File( filename ), source );
    }

    /**
     * Serialize the configuration object to a file using a File object.
     */
    public void serializeToFile( final File file, final Configuration source )
        throws SAXException, IOException, ConfigurationException
    {
        serialize( new FileOutputStream( file ), source );
    }

    /**
     * Serialize the configuration object to an output stream.
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
     */
    public void serialize( final String uri, final Configuration source )
        throws SAXException, IOException, ConfigurationException
    {
        serialize( new URL( uri ).openConnection().getOutputStream(), source );
    }
}
