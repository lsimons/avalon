/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
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
 
 4. The names "Jakarta", "Apache Avalon", "Avalon Cornerstone", "Avalon
    Framework" and "Apache Software Foundation"  must not be used to endorse
    or promote products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.
 
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

package org.apache.avalon.cornerstone.blocks.sax;

import javax.xml.parsers.ParserConfigurationException;
import org.apache.avalon.cornerstone.services.sax.SAXParserFactory;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * Block implementation of the SAXParserFactory service.  That service being
 * a non abstract/static clone of the javax.xml.parsers class of the same name.
 *
 * @phoenix:block
 * @phoenix:service name="org.apache.avalon.cornerstone.services.sax.SAXParserFactory"
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a>
 */
public class SAXParserFactoryImpl
    extends AbstractLogEnabled
    implements Configurable, SAXParserFactory
{
    protected javax.xml.parsers.SAXParserFactory m_saxParserFactory;

    public void dispose()
    {
        m_saxParserFactory = null;
    }

    public void configure( Configuration configuration )
        throws ConfigurationException
    {

        // org.apache.crimson.jaxp.SAXParserFactoryImpl is an example of a
        // string that's valid (classpath considered) as a parameter on config.xml

        final String saxClass =
            configuration.getChild( "saxClass" ).getValue();
        try
        {
            m_saxParserFactory =
                (javax.xml.parsers.SAXParserFactory)Class.forName( saxClass ).newInstance();
        }
        catch( final ClassNotFoundException cnfe )
        {
            throw new ConfigurationException( "ClassNotFoundException for SAX " +
                                              "parser factory",
                                              cnfe );
        }
        catch( final InstantiationException ie )
        {
            throw new ConfigurationException( "InstantiationException for SAX " +
                                              "parser factory",
                                              ie );
        }
        catch( final IllegalAccessException ie )
        {
            throw new ConfigurationException( "IllegalAccessException for SAX " +
                                              "parser factory",
                                              ie );
        }
    }

    public void setNamespaceAware( boolean awareness )
    {
        m_saxParserFactory.setNamespaceAware( awareness );
    }

    public void setValidating( boolean validating )
    {
        m_saxParserFactory.setValidating( validating );
    }

    public boolean isNamespaceAware()
    {
        return m_saxParserFactory.isNamespaceAware();
    }

    public boolean isValidating()
    {
        return m_saxParserFactory.isValidating();
    }

    public void setFeature( String name, boolean value )
        throws ParserConfigurationException,
        SAXNotRecognizedException, SAXNotSupportedException
    {
        m_saxParserFactory.setFeature( name, value );
    }

    public boolean getFeature( String name )
        throws ParserConfigurationException, SAXNotRecognizedException,
        SAXNotSupportedException
    {
        return m_saxParserFactory.getFeature( name );
    }
}

