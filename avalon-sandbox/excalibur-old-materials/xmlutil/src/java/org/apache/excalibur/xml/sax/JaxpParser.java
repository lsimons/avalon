/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.xml.sax;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.excalibur.xml.EntityResolver;

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
 * </ul>
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @author <a href="mailto:sylvain@apache.org">Sylvain Wallez</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/11/07 04:42:09 $
 */
public final class JaxpParser extends AbstractLogEnabled implements Parser, Poolable, Component, Parameterizable, Serviceable, ErrorHandler
{
    /** the SAX Parser factory */
    private SAXParserFactory m_factory;

    /** The SAX reader. It is created lazily by {@link #setupXMLReader()}
     and cleared if a parsing error occurs. */
    private XMLReader m_reader;

    /** the Entity Resolver */
    private EntityResolver m_resolver;

    /** do we want namespaces also as attributes ? */
    private boolean m_nsPrefixes;

    /** do we want to reuse parsers ? */
    private boolean m_reuseParsers;

    /** do we stop on warnings ? */
    private boolean m_stopOnWarning;

    /** do we stop on recoverable errors ? */
    private boolean m_stopOnRecoverableError;

    /**
     * Get the Entity Resolver from the component m_manager
     *
     * @avalon.service interface="EntityResolver" optional="true"
     */
    public void service( final ServiceManager manager )
        throws ServiceException
    {
        if( manager.hasService( EntityResolver.ROLE ) )
        {
            m_resolver = (EntityResolver)manager.lookup( EntityResolver.ROLE );
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "JaxpParser: Using EntityResolver: " + m_resolver );
            }
        }
    }

    public void parameterize( final Parameters params )
        throws ParameterException
    {
        // Validation and namespace prefixes parameters
        boolean validate = params.getParameterAsBoolean( "validate", false );
        m_nsPrefixes = params.getParameterAsBoolean( "namespace-prefixes", false );
        m_reuseParsers = params.getParameterAsBoolean( "reuse-parsers", true );
        m_stopOnWarning = params.getParameterAsBoolean( "stop-on-warning", true );
        m_stopOnRecoverableError = params.getParameterAsBoolean( "stop-on-recoverable-error", true );

        // Get the SAXFactory
        final String saxParserFactoryName = params.getParameter( "sax-parser-factory",
                                                                 "javax.xml.parsers.SAXParserFactory" );
        if( "javax.xml.parsers.SAXParserFactory".equals( saxParserFactoryName ) )
        {
            m_factory = SAXParserFactory.newInstance();
        }
        else
        {
            try
            {
                final Class factoryClass = loadClass( saxParserFactoryName );
                m_factory = (SAXParserFactory)factoryClass.newInstance();
            }
            catch( Exception e )
            {
                throw new ParameterException( "Cannot load SAXParserFactory class " + saxParserFactoryName, e );
            }
        }
        m_factory.setNamespaceAware( true );
        m_factory.setValidating( validate );

        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "JaxpParser: validating: " + validate +
                               ", namespace-prefixes: " + m_nsPrefixes +
                               ", reuse parser: " + m_reuseParsers +
                               ", stop on warning: " + m_stopOnWarning +
                               ", stop on recoverable-error: " + m_stopOnRecoverableError +
                               ", saxParserFactory: " + saxParserFactoryName );
        }
    }

    /**
     * Load a class
     */
    private Class loadClass( String name ) throws Exception
    {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if( loader == null )
        {
            loader = getClass().getClassLoader();
        }
        return loader.loadClass( name );
    }

    /**
     * Parse the <code>InputSource</code> and send
     * SAX events to the consumer.
     * Attention: the consumer can  implement the
     * <code>LexicalHandler</code> as well.
     * The parse should take care of this.
     */
    public void parse( final InputSource in,
                       final ContentHandler contentHandler,
                       final LexicalHandler lexicalHandler )
        throws SAXException, IOException
    {
        setupXMLReader();

        // Ensure we will use a fresh new parser at next parse in case of failure
        XMLReader tmpReader = m_reader;
        m_reader = null;

        try
        {
            if( null != lexicalHandler )
            {
                tmpReader.setProperty( "http://xml.org/sax/properties/lexical-handler",
                                       lexicalHandler );
            }
        }
        catch( final SAXException e )
        {
            final String message =
                "SAX2 driver does not support property: " +
                "'http://xml.org/sax/properties/lexical-handler'";
            getLogger().warn( message );
        }

        tmpReader.setErrorHandler( this );
        tmpReader.setContentHandler( contentHandler );
        if( null != m_resolver )
        {
            tmpReader.setEntityResolver( m_resolver );
        }

        tmpReader.parse( in );

        // Here, parsing was successful : restore reader
        if( m_reuseParsers )
        {
            m_reader = tmpReader;
        }
    }

    /**
     * Creates a new {@link XMLReader} if needed.
     */
    private void setupXMLReader()
        throws SAXException
    {
        if( null == m_reader )
        {
            // Create the XMLReader
            try
            {
                m_reader = m_factory.newSAXParser().getXMLReader();
            }
            catch( final ParserConfigurationException pce )
            {
                final String message = "Cannot produce a valid parser";
                throw new SAXException( message, pce );
            }
            if( m_nsPrefixes )
            {
                try
                {
                    m_reader.setFeature( "http://xml.org/sax/features/namespace-prefixes",
                                         m_nsPrefixes );
                }
                catch( final SAXException se )
                {
                    final String message =
                        "SAX2 XMLReader does not support setting feature: " +
                        "'http://xml.org/sax/features/namespace-prefixes'";
                    getLogger().warn( message );
                }
            }
        }
    }

    /**
     * Receive notification of a recoverable error.
     */
    public void error( final SAXParseException spe )
        throws SAXException
    {
        final String message =
            "Error parsing " + spe.getSystemId() + " (line " +
            spe.getLineNumber() + " col. " + spe.getColumnNumber() +
            "): " + spe.getMessage();
        if( m_stopOnRecoverableError )
        {
            throw new SAXException( message, spe );
        }
        getLogger().error( message, spe );
    }

    /**
     * Receive notification of a fatal error.
     */
    public void fatalError( final SAXParseException spe )
        throws SAXException
    {
        final String message =
            "Fatal error parsing " + spe.getSystemId() + " (line " +
            spe.getLineNumber() + " col. " + spe.getColumnNumber() +
            "): " + spe.getMessage();
        throw new SAXException( message, spe );
    }

    /**
     * Receive notification of a warning.
     */
    public void warning( final SAXParseException spe )
        throws SAXException
    {
        final String message =
            "Warning parsing " + spe.getSystemId() + " (line " +
            spe.getLineNumber() + " col. " + spe.getColumnNumber() +
            "): " + spe.getMessage();

        if( m_stopOnWarning )
        {
            throw new SAXException( message, spe );
        }
        getLogger().warn( message, spe );
    }
}
