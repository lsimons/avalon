/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2002 The Apache Software Foundation. All rights
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
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
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
            return new JarvConfigurationValidator( getLogger(),
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
