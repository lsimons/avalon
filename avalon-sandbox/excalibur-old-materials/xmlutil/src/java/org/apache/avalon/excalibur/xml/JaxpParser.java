/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.xml;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

/**
 * An XMLParser that is only dependant on JAXP 1.1 compliant parsers.
 *
 * The configuration can contain the following parameters :
 * <ul>
 * <li>validate (boolean, default = <code>false</code>) : should the parser
 *     validate parsed documents ?
 * </li>
 * <li>namespace-prefixes (boolean, default = <code>false</code>) : do we want
 *     namespaces declarations also as 'xmlns:' attributes ?<br>
 *     <i>Note</i> : setting this to <code>true</code> confuses some XSL
 *     processors (e.g. Saxon).
 * </li>
 * <li>stop-on-warning (boolean, default = <code>true</code>) : should the parser
 *     stop parsing if a warning occurs ?
 * </li>
 * <li>stop-on-recoverable-error (boolean, default = <code>true</code>) : should the parser
 *     stop parsing if a recoverable error occurs ?
 * </li>
 * <li>reuse-parsers (boolean, default = <code>true</code>) : do we want to reuse
 *     parsers or create a new parser for each parse ?<br>
 *     <i>Note</i> : even if this parameter is <code>true</code>, parsers are not
 *     recycled in case of parsing errors : some parsers (e.g. Xerces) don't like
 *     to be reused after failure.
 * </li>
 * <li>sax-parser-factory (string, optional) : the name of the <code>SAXParserFactory</code>
 *     implementation class to be used instead of using the standard JAXP mechanism
 *     (<code>SAXParserFactory.newInstance()</code>). This allows to choose
 *     unambiguously the JAXP implementation to be used when several of them are
 *     available in the classpath.
 * </li>
 * <li>document-builder-factory (string, optional) : the name of the
 *     <code>DocumentBuilderFactory</code> implementation to be used (similar to
 *     <code>sax-parser-factory</code> for DOM).
 * </li>
 * </ul>
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @author <a href="mailto:sylvain@apache.org">Sylvain Wallez</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/22 10:06:04 $
 */
public class JaxpParser
    extends AbstractLogEnabled
    implements Parser, ErrorHandler, Composable, Parameterizable, Poolable
{

    /** the SAX Parser factory */
    protected SAXParserFactory factory;

    /** the Document Builder factory */
    protected DocumentBuilderFactory docFactory;

    /** The SAX reader. It is created lazily by {@link #setupXMLReader()}
     and cleared if a parsing error occurs. */
    protected XMLReader reader;

    /** The DOM builder. It is created lazily by {@link #setupDocumentBuilder()}
     and cleared if a parsing error occurs. */
    protected DocumentBuilder docBuilder;

    /** the component manager */
    protected ComponentManager manager;

    /** the Entity Resolver */
    protected EntityResolver resolver;

    /** do we want namespaces also as attributes ? */
    protected boolean nsPrefixes;

    /** do we want to reuse parsers ? */
    protected boolean reuseParsers;

    /** do we stop on warnings ? */
    protected boolean stopOnWarning;

    /** do we stop on recoverable errors ? */
    protected boolean stopOnRecoverableError;

    /**
     * Get the Entity Resolver from the component manager
     */
    public void compose( ComponentManager manager )
        throws ComponentException
    {
        this.manager = manager;
        if( this.manager.hasComponent( EntityResolver.ROLE ) )
        {
            this.resolver = (EntityResolver)this.manager.lookup( EntityResolver.ROLE );
            if( this.getLogger().isDebugEnabled() )
            {
                this.getLogger().debug( "JaxpParser: Using EntityResolver: " + this.resolver );
            }
        }
    }

    /**
     * Configure
     */
    public void parameterize( Parameters params )
        throws ParameterException
    {
        // Validation and namespace prefixes parameters
        boolean validate = params.getParameterAsBoolean( "validate", false );
        this.nsPrefixes = params.getParameterAsBoolean( "namespace-prefixes", false );
        this.reuseParsers = params.getParameterAsBoolean( "reuse-parsers", true );
        this.stopOnWarning = params.getParameterAsBoolean( "stop-on-warning", true );
        this.stopOnRecoverableError = params.getParameterAsBoolean( "stop-on-recoverable-error", true );

        // Get the SAXFactory
        final String saxParserFactoryName = params.getParameter( "sax-parser-factory",
                                                                 "javax.xml.parsers.SAXParserFactory" );
        if( "javax.xml.parsers.SAXParserFactory".equals( saxParserFactoryName ) )
        {
            this.factory = SAXParserFactory.newInstance();
        }
        else
        {
            try
            {
                final Class factoryClass = this.loadClass( saxParserFactoryName );
                this.factory = (SAXParserFactory)factoryClass.newInstance();
            }
            catch( Exception e )
            {
                throw new ParameterException( "Cannot load SAXParserFactory class " + saxParserFactoryName, e );
            }
        }
        this.factory.setNamespaceAware( true );
        this.factory.setValidating( validate );

        // Get the DocumentFactory
        final String documentBuilderFactoryName = params.getParameter( "document-builder-factory",
                                                                       "javax.xml.parsers.DocumentBuilderFactory" );
        if( "javax.xml.parsers.DocumentBuilderFactory".equals( documentBuilderFactoryName ) )
        {
            this.docFactory = DocumentBuilderFactory.newInstance();
        }
        else
        {
            try
            {
                final Class factoryClass = this.loadClass( documentBuilderFactoryName );
                this.docFactory = (DocumentBuilderFactory)factoryClass.newInstance();
            }
            catch( Exception e )
            {
                throw new ParameterException( "Cannot load DocumentBuilderFactory class " + documentBuilderFactoryName, e );
            }
        }
        this.docFactory.setNamespaceAware( true );
        this.docFactory.setValidating( validate );

        if( this.getLogger().isDebugEnabled() )
        {
            this.getLogger().debug( "JaxpParser: validating: " + validate +
                                    ", namespace-prefixes: " + this.nsPrefixes +
                                    ", reuse parser: " + this.reuseParsers +
                                    ", stop on warning: " + this.stopOnWarning +
                                    ", stop on recoverable-error: " + this.stopOnRecoverableError +
                                    ", saxParserFactory: " + saxParserFactoryName +
                                    ", documentBuilderFactory: " + documentBuilderFactoryName );
        }
    }

    /**
     * Load a class
     */
    protected Class loadClass( String name ) throws Exception
    {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if( loader == null )
        {
            loader = this.getClass().getClassLoader();
        }
        return loader.loadClass( name );
    }

    /**
     * Parse the <code>InputSource</code> and send
     * SAX events to the consumer.
     * Attention: the consumer can either be an XMLConsumer
     * or implement the <code>LexicalHandler</code> as well.
     * The parse should take care of this.
     */
    public void parse( InputSource in, ContentHandler consumer )
        throws SAXException, IOException
    {
        if( consumer instanceof LexicalHandler )
        {
            this.parse( in, consumer, (LexicalHandler)consumer );
        }
        else
        {
            this.parse( in, consumer, null );
        }
    }

    /**
     * Parse the <code>InputSource</code> and send
     * SAX events to the consumer.
     * Attention: the consumer can  implement the
     * <code>LexicalHandler</code> as well.
     * The parse should take care of this.
     */
    public void parse( InputSource in,
                       ContentHandler contentHandler,
                       LexicalHandler lexicalHandler )
        throws SAXException, IOException
    {
        this.setupXMLReader();

        // Ensure we will use a fresh new parser at next parse in case of failure
        XMLReader tmpReader = this.reader;
        this.reader = null;

        try
        {
            if( null != lexicalHandler )
            {
                tmpReader.setProperty( "http://xml.org/sax/properties/lexical-handler",
                                       lexicalHandler );
            }
        }
        catch( SAXException e )
        {
            this.getLogger().warn( "SAX2 driver does not support property: " +
                                   "'http://xml.org/sax/properties/lexical-handler'" );
        }

        tmpReader.setErrorHandler( this );
        tmpReader.setContentHandler( contentHandler );
        if( null != this.resolver )
        {
            tmpReader.setEntityResolver( this.resolver );
        }

        tmpReader.parse( in );

        // Here, parsing was successful : restore this.reader
        if( this.reuseParsers )
            this.reader = tmpReader;
    }

    /**
     * Parses a new Document object from the given InputSource.
     */
    public Document parseDocument( InputSource input )
        throws SAXException, IOException
    {
        this.setupDocumentBuilder();

        // Ensure we will use a fresh new parser at next parse in case of failure
        DocumentBuilder tmpBuilder = this.docBuilder;
        this.docBuilder = null;

        if( null != this.resolver )
        {
            tmpBuilder.setEntityResolver( this.resolver );
        }

        Document result = tmpBuilder.parse( input );

        // Here, parsing was successful : restore this.builder
        if( this.reuseParsers )
            this.docBuilder = tmpBuilder;

        return result;
    }

    /**
     * Creates a new <code>XMLReader</code> if needed.
     */
    protected void setupXMLReader()
        throws SAXException
    {
        if( null == this.reader )
        {
            // Create the XMLReader
            try
            {
                this.reader = factory.newSAXParser().getXMLReader();
            }
            catch( ParserConfigurationException pce )
            {
                throw new SAXException( "Cannot produce a valid parser", pce );
            }
            if( this.nsPrefixes )
            {
                try
                {
                    this.reader.setFeature( "http://xml.org/sax/features/namespace-prefixes", this.nsPrefixes );
                }
                catch( SAXException e )
                {
                    this.getLogger().warn( "SAX2 XMLReader does not support setting feature: " +
                                           "'http://xml.org/sax/features/namespace-prefixes'" );
                }
            }
        }
    }

    /**
     * Creates a new <code>DocumentBuilder</code> if needed.
     */
    protected void setupDocumentBuilder()
        throws SAXException
    {
        if( null == this.docBuilder )
        {
            try
            {
                this.docBuilder = this.docFactory.newDocumentBuilder();
            }
            catch( ParserConfigurationException pce )
            {
                throw new SAXException( "Could not create DocumentBuilder", pce );
            }
        }
    }

    /**
     * Return a new <code>Document</code>.
     */
    public Document createDocument()
        throws SAXException
    {
        this.setupDocumentBuilder();
        return this.docBuilder.newDocument();
    }

    /**
     * Receive notification of a recoverable error.
     */
    public void error( SAXParseException e )
        throws SAXException
    {
        final String msg = "Error parsing " + e.getSystemId() + " (line " +
            e.getLineNumber() + " col. " + e.getColumnNumber() +
            "): " + e.getMessage();
        if( this.stopOnRecoverableError )
        {
            throw new SAXException( msg, e );
        }
        this.getLogger().error( msg, e );
    }

    /**
     * Receive notification of a fatal error.
     */
    public void fatalError( SAXParseException e )
        throws SAXException
    {
        throw new SAXException( "Fatal error parsing " + e.getSystemId() + " (line " +
                                e.getLineNumber() + " col. " + e.getColumnNumber() +
                                "): " + e.getMessage(), e );
    }

    /**
     * Receive notification of a warning.
     */
    public void warning( SAXParseException e )
        throws SAXException
    {
        final String msg = "Warning parsing " + e.getSystemId() + " (line " +
            e.getLineNumber() + " col. " + e.getColumnNumber() +
            "): " + e.getMessage();
        if( this.stopOnWarning )
        {
            throw new SAXException( msg, e );
        }
        this.getLogger().warn( msg, e );
    }
}
