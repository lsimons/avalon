/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.configuration.validator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.excalibur.io.IOUtil;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.interfaces.ConfigurationValidator;
import org.apache.avalon.phoenix.interfaces.ConfigurationValidatorMBean;
import org.apache.excalibur.configuration.ConfigurationUtil;

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.VerifierFactory;
import org.iso_relax.verifier.VerifierHandler;

/**
 * A validator that is capable of validating any schema supported by the JARV
 * engine. <a href="http://iso-relax.sourceforge.net/">http://iso-relax.sourceforge.net/</a>
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public class JarvConfigurationValidator extends AbstractLogEnabled
    implements Configurable, Initializable, ConfigurationValidator, ConfigurationValidatorMBean
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( JarvConfigurationValidator.class );

    private String m_schemaType;
    private String m_schemaLanguage;
    private String m_verifierFactoryClass;
    private String m_debugPath;

    private final DefaultConfigurationSerializer m_serializer =
        new DefaultConfigurationSerializer();

    private final Map m_schemaURLs = Collections.synchronizedMap( new HashMap() );
    private final Map m_schemas = Collections.synchronizedMap( new HashMap() );
    private VerifierFactory m_verifierFactory;

    /**
     * There are two possible configuration options for this class. They are mutually exclusive.
     * <ol>
     *   <li>&lt;schema-language&gt;<i>schema language uri</i>&lt;/schema-language&gt;</li>
     *   <li>&lt;verifier-factory-class&gt;<i>classname</i>&lt;/verifier-factory-class&gt;<br>
     *      The fully-qualified classname to use as a verifier factory.
     *   </li>
     * </ol>
     *
     * @see http://iso-relax.sourceforge.net/apiDoc/org/iso_relax/verifier/VerifierFactory.html#newInstance(java.lang.String)
     */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        this.m_debugPath = configuration.getChild( "debug-output-path" ).getValue( null );
        this.m_schemaType = configuration.getAttribute( "schema-type" );
        this.m_schemaLanguage = configuration.getChild( "schema-language" ).getValue( null );
        this.m_verifierFactoryClass =
            configuration.getChild( "verifier-factory-class" ).getValue( null );

        if( ( null == this.m_schemaLanguage && null == this.m_verifierFactoryClass )
            || ( null != this.m_schemaLanguage && null != this.m_verifierFactoryClass ) )
        {
            throw new ConfigurationException( REZ.getString( "jarv.error.badconfig" ) );
        }
    }

    public void initialize()
        throws Exception
    {
        if( null != this.m_schemaLanguage )
        {
            this.m_verifierFactory = VerifierFactory.newInstance( this.m_schemaLanguage );
        }
        else if( null != this.m_verifierFactoryClass )
        {
            this.m_verifierFactory =
                ( VerifierFactory ) Class.forName( this.m_verifierFactoryClass ).newInstance();
        }

        if( null != this.m_debugPath )
        {
            FileUtil.forceMkdir( new File( m_debugPath ) );
        }

        this.m_serializer.setIndent( true );
    }

    private String createKey( String application, String block )
    {
        return application + "." + block;
    }

    public void addSchema( String application, String block, String schemaType, String url )
        throws ConfigurationException
    {
        if( !this.m_schemaType.equals( schemaType ) )
        {
            final String msg = REZ.getString( "jarv.error.badtype", schemaType, this.m_schemaType );

            throw new ConfigurationException( msg );
        }

        try
        {
            final String key = createKey( application, block );

            this.m_schemas.put( key, this.m_verifierFactory.compileSchema( url ) );
            this.m_schemaURLs.put( key, url );
        }
        catch( VerifierConfigurationException e )
        {
            final String msg = REZ.getString( "jarv.error.schema.create", application, block, url );

            throw new ConfigurationException( msg, e );
        }
        catch( SAXParseException e )
        {
            final String msg = REZ.getString( "jarv.error.schema.parse",
                                              application,
                                              block,
                                              new Integer( e.getLineNumber() ),
                                              new Integer( e.getColumnNumber() ) );

            throw new ConfigurationException( msg, e );
        }
        catch( Exception e )
        {
            final String msg = REZ.getString( "jarv.error.schema", application, block, url );

            throw new ConfigurationException( msg, e );
        }

        if( getLogger().isDebugEnabled() )
            getLogger().debug( "Created schema [app: " + application + ", block: " + block
                               + ", url: " + url + "]" );
    }

    //JARV does not support feasability validation
    public boolean isFeasiblyValid( String application, String block, Configuration configuration )
        throws ConfigurationException
    {
        return true;
    }

    public boolean isValid( final String application,
                            final String block,
                            final Configuration configuration )
        throws ConfigurationException
    {
        final Schema schema = ( Schema ) this.m_schemas.get( createKey( application, block ) );

        if( null == schema )
        {
            final String msg = REZ.getString( "jarv.error.noschema", application, block );

            throw new ConfigurationException( msg );
        }

        if( null != this.m_debugPath )
        {
            writeDebugConfiguration( application, block, configuration );
        }

        try
        {
            final Verifier verifier = schema.newVerifier();
            final VerifierHandler handler = verifier.getVerifierHandler();

            verifier.setErrorHandler( new ErrorHandler()
            {
                public void warning( SAXParseException exception )
                    throws SAXException
                {
                    if( getLogger().isWarnEnabled() )
                        getLogger().warn( "Valdating configuration [app: " + application
                                          + ", block: " + block
                                          + ", msg: " + exception.getMessage() + "]" );
                }

                public void error( SAXParseException exception )
                    throws SAXException
                {
                    if( getLogger().isErrorEnabled() )
                        getLogger().error( "Valdating configuration [app: " + application
                                           + ", block: " + block
                                           + ", msg: " + exception.getMessage() + "]" );
                }

                public void fatalError( SAXParseException exception )
                    throws SAXException
                {
                    if( getLogger().isFatalErrorEnabled() )
                        getLogger().fatalError( "Valdating configuration [app: " + application
                                                + ", block: " + block
                                                + ", msg: " + exception.getMessage() + "]" );

                }
            } );

            this.m_serializer.serialize( handler,
                                         ConfigurationUtil.branch( configuration, "root" ) );

            return handler.isValid();
        }
        catch( VerifierConfigurationException e )
        {
            final String msg = REZ.getString( "jarv.valid.schema", application, block );

            throw new ConfigurationException( msg, e );
        }
        catch( SAXException e )
        {
            final String msg = REZ.getString( "jarv.valid.badparse", application, block );

            throw new ConfigurationException( msg, e );
        }
        catch( IllegalStateException e )
        {
            final String msg = REZ.getString( "jarv.valid.badparse", application, block );

            throw new ConfigurationException( msg, e );
        }
    }

    private void writeDebugConfiguration( final String application,
                                          final String block,
                                          final Configuration configuration )
    {
        try
        {
            final File temp = File.createTempFile( application + "-" + block + "-",
                                                   ".xml",
                                                   new File( this.m_debugPath ) );

            this.m_serializer.serializeToFile( temp, configuration );

            if( getLogger().isDebugEnabled() )
                getLogger().debug( "Configuration written at: " + temp );
        }
        catch( Exception e )
        {
            getLogger().error( "Unable to write debug output", e );
        }
    }

    public void removeSchema( String application, String block )
    {
        this.m_schemaURLs.remove( createKey( application, block ) );

        if( null != this.m_schemas.remove( createKey( application, block ) )
            && getLogger().isDebugEnabled() )
            getLogger().debug( "Removed schema [app: " + application + ", block: " + block + "]" );
    }

    public String getSchemaType( String application, String block )
    {
        return this.m_schemaType;
    }

    public String getSchema( String application, String block )
    {
        final String key = createKey( application, block );
        final String url = ( String ) this.m_schemaURLs.get( key );

        if( null != url )
        {
            try
            {
                return IOUtil.toString( new URL( url ).openStream() );
            }
            catch( IOException e )
            {
                getLogger().error( "Unable to read schema [app: " + application
                                   + ", block: " + block + ", url: " + url + "]", e );
            }
        }

        return null;
    }
}
