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

package org.apache.avalon.phoenix.components.configuration.validator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.excalibur.io.IOUtil;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.interfaces.ConfigurationValidator;
import org.apache.avalon.phoenix.interfaces.ConfigurationValidatorMBean;
import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.VerifierFactory;
import org.iso_relax.verifier.VerifierHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * A validator that is capable of validating any schema supported by the JARV
 * engine. <a href="http://iso-relax.sourceforge.net/">http://iso-relax.sourceforge.net/</a>
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public class JarvConfigurationValidator
    extends AbstractLogEnabled
    implements Configurable, Initializable, ConfigurationValidator,
    ConfigurationValidatorMBean
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
     * @see <a href="http://iso-relax.sourceforge.net/apiDoc/org/iso_relax/verifier/VerifierFactory.html#newInstance(java.lang.String)">
     *    http://iso-relax.sourceforge.net/apiDoc/org/iso_relax/verifier/VerifierFactory.html#newInstance(java.lang.String)</a>
     */
    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_debugPath = configuration.getChild( "debug-output-path" ).getValue( null );
        m_schemaType = configuration.getAttribute( "schema-type" );
        m_schemaLanguage = configuration.getChild( "schema-language" ).getValue( null );
        m_verifierFactoryClass =
            configuration.getChild( "verifier-factory-class" ).getValue( null );

        if( ( null == m_schemaLanguage && null == m_verifierFactoryClass )
            || ( null != m_schemaLanguage && null != m_verifierFactoryClass ) )
        {
            throw new ConfigurationException( REZ.getString( "jarv.error.badconfig" ) );
        }
    }

    public void initialize()
        throws Exception
    {
        if( null != m_schemaLanguage )
        {
            m_verifierFactory = VerifierFactory.newInstance( m_schemaLanguage );
        }
        else if( null != m_verifierFactoryClass )
        {
            m_verifierFactory =
                (VerifierFactory)Class.forName( m_verifierFactoryClass ).newInstance();
        }

        if( null != m_debugPath )
        {
            FileUtil.forceMkdir( new File( m_debugPath ) );
        }

        m_serializer.setIndent( true );
    }

    private String createKey( final String application, final String block )
    {
        return application + "." + block;
    }

    public void addSchema( final String application, final String block, final String schemaType, final String url )
        throws ConfigurationException
    {
        if( !m_schemaType.equals( schemaType ) )
        {
            final String msg = REZ.getString( "jarv.error.badtype", schemaType, m_schemaType );

            throw new ConfigurationException( msg );
        }

        try
        {
            final String key = createKey( application, block );

            m_schemas.put( key, m_verifierFactory.compileSchema( url ) );
            m_schemaURLs.put( key, url );
        }
        catch( VerifierConfigurationException e )
        {
            final String msg =
                REZ.getString( "jarv.error.schema.create", application, block, url );

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
    public boolean isFeasiblyValid( final String application,
                                    final String block,
                                    final Configuration configuration )
        throws ConfigurationException
    {
        return true;
    }

    public boolean isValid( final String application,
                            final String block,
                            final Configuration configuration )
        throws ConfigurationException
    {
        final Schema schema = (Schema)m_schemas.get( createKey( application, block ) );
        final DefaultConfiguration branched = new DefaultConfiguration( "root" );
        branched.addAll( configuration );
        branched.makeReadOnly();

        if( null == schema )
        {
            final String message =
                REZ.getString( "jarv.error.noschema", application, block );
            throw new ConfigurationException( message );
        }

        if( null != m_debugPath )
        {
            writeDebugConfiguration( application, block, branched );
        }

        try
        {
            final Verifier verifier = schema.newVerifier();
            final VerifierHandler handler = verifier.getVerifierHandler();

            verifier.setErrorHandler( new ErrorHandler()
            {
                public void warning( final SAXParseException exception )
                    throws SAXException
                {
                    if( getLogger().isWarnEnabled() )
                    {
                        getLogger().warn( "Valdating configuration [app: " + application
                                          + ", block: " + block
                                          + ", msg: " + exception.getMessage() + "]" );
                    }
                }

                public void error( final SAXParseException exception )
                    throws SAXException
                {
                    if( getLogger().isErrorEnabled() )
                        getLogger().error( "Valdating configuration [app: " + application
                                           + ", block: " + block
                                           + ", msg: " + exception.getMessage() + "]" );
                }

                public void fatalError( final SAXParseException exception )
                    throws SAXException
                {
                    if( getLogger().isFatalErrorEnabled() )
                    {
                        final String message = "Valdating configuration [app: " +
                            application + ", block: " + block + ", msg: " +
                            exception.getMessage() + "]";
                        getLogger().fatalError( message );
                    }
                }
            } );

            m_serializer.serialize( handler, branched );

            return handler.isValid();
        }
        catch( final VerifierConfigurationException e )
        {
            final String message =
                REZ.getString( "jarv.valid.schema", application, block );
            throw new ConfigurationException( message, e );
        }
        catch( final SAXException e )
        {
            final String message =
                REZ.getString( "jarv.valid.badparse", application, block );
            throw new ConfigurationException( message, e );
        }
        catch( final IllegalStateException e )
        {
            final String message =
                REZ.getString( "jarv.valid.badparse", application, block );
            throw new ConfigurationException( message, e );
        }
    }

    private void writeDebugConfiguration( final String application,
                                          final String block,
                                          final Configuration configuration )
    {
        try
        {
            final File temp =
                File.createTempFile( application + "-" + block + "-",
                                     ".xml",
                                     new File( m_debugPath ) );

            m_serializer.serializeToFile( temp, configuration );

            if( getLogger().isDebugEnabled() )
            {
                final String message = "Configuration written at: " + temp;
                getLogger().debug( message );
            }
        }
        catch( final Exception e )
        {
            final String message = "Unable to write debug output";
            getLogger().error( message, e );
        }
    }

    public void removeSchema( final String application, final String block )
    {
        final String key = createKey( application, block );
        m_schemaURLs.remove( key );

        if( null != m_schemas.remove( key ) &&
            getLogger().isDebugEnabled() )
        {
            final String message =
                "Removed schema [app: " + application + ", block: " + block + "]";
            getLogger().debug( message );
        }
    }

    public String getSchemaType( final String application, final String block )
    {
        return m_schemaType;
    }

    public String getSchema( final String application, final String block )
    {
        final String key = createKey( application, block );
        final String url = (String)m_schemaURLs.get( key );

        if( null != url )
        {
            try
            {
                return IOUtil.toString( new URL( url ).openStream() );
            }
            catch( IOException e )
            {
                final String message = "Unable to read schema [app: " + application +
                    ", block: " + block + ", url: " + url + "]";
                getLogger().error( message, e );
            }
        }

        return null;
    }
}
