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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.VerifierHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public class JarvConfigurationValidator implements ConfigurationValidator
{
    private final DefaultConfigurationSerializer m_serializer =
        new DefaultConfigurationSerializer();

    private final Schema m_schema;

    public JarvConfigurationValidator( Schema schema )
    {
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
        final DefaultConfiguration branched =
            new DefaultConfiguration( "root", configuration.getLocation() );
        branched.addAll( configuration );
        branched.makeReadOnly();

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
