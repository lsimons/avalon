/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.configuration;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Properties;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;


/**
 * A ConfigurationSerializer serializes configurations via SAX2 compliant parser.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class DefaultConfigurationSerializer
{
    private SAXTransformerFactory m_tfactory = null;
    private TransformerHandler    m_handler;
    private OutputStream          m_out;
    private Properties            m_format = new Properties();
    private Namespace             m_currentNamespace = Namespace.getNamespace( null );

    /**
     * Build a ConfigurationSerializer
     */
    public DefaultConfigurationSerializer()
    {
        this.getTransformerFactory();
    }

    /**
     * Internally set the output strream we will be using.
     */
    protected void setOutputStream(OutputStream out)
    {
        try
        {
            this.m_out = out;
            this.m_handler = this.getTransformerFactory().newTransformerHandler();
            this.m_format.put(OutputKeys.METHOD,"xml");
            this.m_handler.setResult(new StreamResult(out));
            this.m_handler.getTransformer().setOutputProperties(this.m_format);
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
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
        this.m_handler.startDocument();
        this.serializeElement(source);
        this.m_handler.endDocument();
    }

    /**
     * Serialize each Configuration element.  This method is called recursively.
     */
    protected void serializeElement( final Configuration element )
    throws SAXException
    {
        AttributesImpl attr = new AttributesImpl();
        String[] attrNames = element.getAttributeNames();

        if (null != attrNames)
        {
            for (int i = 0; i < attrNames.length; i++)
            {
                attr.setAttribute(i, "", attrNames[i], attrNames[i], "CDATA",
                                  element.getAttribute(attrNames[i], ""));
            }
        }

        Namespace oldNamespace = null;
        if ( m_currentNamespace != element.getNamespace() )
        {
            oldNamespace = m_currentNamespace;
            m_currentNamespace = element.getNamespace();
            this.m_handler.startPrefixMapping( m_currentNamespace.getPrefix(), m_currentNamespace.getURI() );
        }

        this.m_handler.startElement("", element.getName(), element.getName(), attr);
        String value = element.getValue(null);

        if (null == value)
        {
            Configuration[] children = element.getChildren();

            for (int i = 0; i < children.length; i++)
            {
                this.serializeElement(children[i]);
            }
        }
        else
        {
            this.m_handler.characters(value.toCharArray(), 0, value.length());
        }

        this.m_handler.endElement("", element.getName(), element.getName());

        if ( null != oldNamespace )
        {
            this.m_handler.endPrefixMapping( m_currentNamespace.getPrefix() );
            m_currentNamespace = oldNamespace;
        }
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
        this.serialize( new FileOutputStream( file ), source );
    }

    /**
     * Serialize the configuration object to an output stream.
     */
    public void serialize( final OutputStream outputStream, final Configuration source )
        throws SAXException, IOException, ConfigurationException
    {
        synchronized(this)
        {
            this.setOutputStream( outputStream );
            this.serialize( source );
        }
    }

    /**
     * Serialize the configuration object to an output stream derived from an
     * URI.  The URI must be resolveable by the <code>java.net.URL</code> object.
     */
    public void serialize( final String uri, final Configuration source )
        throws SAXException, IOException, ConfigurationException
    {
        this.serialize( new URL( uri ).openConnection().getOutputStream(), source );
    }
}