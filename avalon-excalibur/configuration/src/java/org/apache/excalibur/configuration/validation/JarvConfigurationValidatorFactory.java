/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.excalibur.configuration.validation;

import java.io.InputStream;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.VerifierFactory;
import org.xml.sax.SAXParseException;

/**
 * A validator that is capable of validating any schema supported by the JARV
 * engine. <a href="http://iso-relax.sourceforge.net/">http://iso-relax.sourceforge.net/</a>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public class JarvConfigurationValidatorFactory
    extends AbstractLogEnabled
    implements Configurable, Initializable, ConfigurationValidatorFactory
{
    private String m_schemaType;

    private String m_schemaLanguage;

    private String m_verifierFactoryClass;

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
        m_schemaType = configuration.getAttribute( "schema-type" );
        m_schemaLanguage = configuration.getChild( "schema-language" ).getValue( null );
        m_verifierFactoryClass =
            configuration.getChild( "verifier-factory-class" ).getValue( null );

        if( ( null == m_schemaLanguage && null == m_verifierFactoryClass )
            || ( null != m_schemaLanguage && null != m_verifierFactoryClass ) )
        {
            final String msg = "Must specify either schema-language or verifier-factory-class";

            throw new ConfigurationException( msg );
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
    }

    public ConfigurationValidator createValidator( String schemaType, InputStream schema )
        throws ConfigurationException
    {
        if( !m_schemaType.equals( schemaType ) )
        {
            final String msg = "Invalid schema type: " + schemaType
                + ". Validator only supports " + m_schemaType;

            throw new ConfigurationException( msg );
        }

        try
        {
            return new JarvConfigurationValidator(
                m_verifierFactory.compileSchema( schema ) );
        }
        catch( VerifierConfigurationException e )
        {
            final String msg = "Unable to create schema";

            throw new ConfigurationException( msg, e );
        }
        catch( SAXParseException e )
        {
            final String msg = "Unable to parse schema [line: " + e.getLineNumber()
                + ", column: " + e.getColumnNumber()
                + ", msg: " + e.getMessage() + "]";

            throw new ConfigurationException( msg, e );
        }
        catch( Exception e )
        {
            final String msg = "Unable to parse schema [url: " + schema
                + ", msg: " + e.getMessage() + "]";

            throw new ConfigurationException( msg, e );
        }
    }
}
