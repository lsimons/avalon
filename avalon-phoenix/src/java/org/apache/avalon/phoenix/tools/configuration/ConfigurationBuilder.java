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

package org.apache.avalon.phoenix.tools.configuration;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.SAXConfigurationHandler;
import org.apache.avalon.framework.logger.Logger;
import org.realityforge.configkit.ResolverFactory;
import org.realityforge.configkit.ConfigValidatorFactory;
import org.realityforge.configkit.ConfigValidator;
import org.realityforge.configkit.ValidationResult;
import org.realityforge.configkit.ValidationIssue;
import org.realityforge.configkit.ValidateException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;

/**
 * Utility class used to load Configuration trees from XML files.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.18 $ $Date: 2003/04/06 11:23:22 $
 */
public class ConfigurationBuilder
{
    public static final String COMPONENTINFO_SCHEMA = "-//AVALON/Component Info DTD Version 1.0//EN";
    public static final String BLOCKINFO_SCHEMA = "-//PHOENIX/Block Info DTD Version 1.0//EN";
    public static final String MXINFO_SCHEMA = "-//PHOENIX/Mx Info DTD Version 1.0//EN";
    public static final String ASSEMBLY_SCHEMA = "-//PHOENIX/Assembly DTD Version 1.0//EN";

    /**
     * The resolver that builder uses.
     */
    private static EntityResolver c_resolver;

    /**
     * Build a configuration object using an XML InputSource object, and
     * optionally validate the xml against the DTD.
     */
    public static Configuration build( final InputSource input,
                                       final String publicId,
                                       final Logger logger )
        throws Exception
    {
        setupResolver();
        final SAXConfigurationHandler handler = new SAXConfigurationHandler();
        if( null == publicId )
        {
            final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware( false );
            final SAXParser saxParser = saxParserFactory.newSAXParser();
            final XMLReader reader = saxParser.getXMLReader();
            reader.setEntityResolver( c_resolver );
            reader.setContentHandler( handler );
            reader.setErrorHandler( handler );
            reader.parse( input );
        }
        else
        {
            final InputSource inputSource = c_resolver.resolveEntity( publicId, null );
            if( null == inputSource )
            {
                final String message = "Unable to locate schema with publicID=" + publicId;
                throw new IllegalStateException( message );
            }

            final ConfigValidator validator =
                ConfigValidatorFactory.create( inputSource, c_resolver );
            final ValidationResult result = validator.validate( input, (ContentHandler)handler );
            processValidationResults( result, logger );
        }
        return handler.getConfiguration();
    }

    /**
     * Process validation results. Print out any warnings or
     * errors and if validation failed then throw an exception.
     *
     * @param result the validation results
     * @param logger the logger to print messages to
     * @throws Exception if validation failed
     */
    public static void processValidationResults( final ValidationResult result,
                                                 final Logger logger )
        throws Exception
    {
        if( !result.isValid() )
        {
            final ValidationIssue[] issues = result.getIssues();
            for( int i = 0; i < issues.length; i++ )
            {
                final ValidationIssue issue = issues[ i ];
                final String message = issue.getException().getMessage();
                if( issue.isWarning() )
                {
                    logger.info( message );
                }
                else if( issue.isError() )
                {
                    logger.warn( message );
                }
                else if( issue.isFatalError() )
                {
                    logger.error( message );
                }
            }
            final ValidateException exception = result.getException();
            throw new Exception( exception.getMessage(), exception );
        }
    }

    private static void setupResolver()
        throws ParserConfigurationException, SAXException, IOException
    {
        if( null == c_resolver )
        {
            c_resolver =
                ResolverFactory.createResolver( ConfigurationBuilder.class.getClassLoader() );
        }
    }
}
