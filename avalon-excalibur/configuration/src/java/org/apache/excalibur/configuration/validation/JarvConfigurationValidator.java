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

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.excalibur.configuration.ConfigurationUtil;

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierHandler;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;

/**
 *
 * @author <a href="proyal@apache.org">peter royal</a>
 */
public class JarvConfigurationValidator implements ConfigurationValidator
{
    private final DefaultConfigurationSerializer m_serializer =
        new DefaultConfigurationSerializer();

    private final Logger m_logger;
    private final Schema m_schema;

    public JarvConfigurationValidator( Logger logger, Schema schema )
    {
        m_logger = logger;
        m_schema = schema;
    }

    public ValidationResult isFeasiblyValid( Configuration configuration )
        throws ConfigurationException
    {
        final ValidationResult result = new ValidationResult();

        result.setResult( true );

        return result;
    }

    public ValidationResult isValid( Configuration configuration )
        throws ConfigurationException
    {
        final ValidationResult result = new ValidationResult();
        final Configuration branched = ConfigurationUtil.branch( configuration, "root" );

        try
        {
            final Verifier verifier = m_schema.newVerifier();
            final VerifierHandler handler = verifier.getVerifierHandler();

            verifier.setErrorHandler( new ErrorHandler()
            {
                public void warning( SAXParseException exception )
                    throws SAXException
                {
                    result.addWarning( exception.getMessage() );
                }

                public void error( SAXParseException exception )
                    throws SAXException
                {
                    result.addError( exception.getMessage() );
                }

                public void fatalError( final SAXParseException exception )
                    throws SAXException
                {
                    result.addError( exception.getMessage() );
                }
            } );

            m_serializer.serialize( handler, branched );

            result.setResult( handler.isValid() );

            return result;
        }
        catch( final VerifierConfigurationException e )
        {
            final String message = "Unable to verify configuration";

            throw new ConfigurationException( message, e );
        }
        catch( final SAXException e )
        {
            final String message = "Unable to parse configuration";

            throw new ConfigurationException( message, e );
        }
        catch( final IllegalStateException e )
        {
            final String message = "Unable to parse configuration";

            throw new ConfigurationException( message, e );
        }
    }
}
