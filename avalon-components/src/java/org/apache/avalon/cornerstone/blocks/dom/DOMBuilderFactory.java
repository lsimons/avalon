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

package org.apache.avalon.cornerstone.blocks.dom;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.avalon.cornerstone.services.dom.DocumentBuilderFactory;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * Block implementation of the DocumentBuilderFactory service.  That service being
 * a non abstract/static clone of the javax.xml.parsers class of the same name.
 *
 * @phoenix:block
 * @phoenix:service name="org.apache.avalon.cornerstone.services.dom.DocumentBuilderFactory"
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a>
 */
public class DOMBuilderFactory
    extends AbstractLogEnabled
    implements Configurable, DocumentBuilderFactory
{
    protected javax.xml.parsers.DocumentBuilderFactory m_documentBuilderFactory;

    public void dispose()
    {
        m_documentBuilderFactory = null;
    }

    public void configure( Configuration configuration )
        throws ConfigurationException
    {

        // org.apache.crimson.jaxp.DocumentBuilderFactoryImpl is an example of a
        // string that's valid (classpath considered) as a parameter on config.xml
        // org.apache.xerces.jaxp.DocumentBuilderFactoryImpl is also valid

        final String domClass =
            configuration.getChild( "domClass" ).getValue();
        try
        {
            m_documentBuilderFactory =
                (javax.xml.parsers.DocumentBuilderFactory)Class.forName( domClass ).newInstance();
        }
        catch( final ClassNotFoundException cnfe )
        {
            throw new ConfigurationException( "ClassNotFoundException for DOM " +
                                              "builder factory",
                                              cnfe );
        }
        catch( final InstantiationException ie )
        {
            throw new ConfigurationException( "InstantiationException for DOM " +
                                              "builder factory",
                                              ie );
        }
        catch( final IllegalAccessException ie )
        {
            throw new ConfigurationException( "IllegalAccessException for DOM " +
                                              "builder factory",
                                              ie );
        }
    }

    public DocumentBuilder newDocumentBuilder()
        throws ParserConfigurationException
    {
        return m_documentBuilderFactory.newDocumentBuilder();
    }

    public void setNamespaceAware( boolean awareness )
    {
        m_documentBuilderFactory.setNamespaceAware( awareness );
    }

    public void setValidating( boolean validating )
    {
        m_documentBuilderFactory.setValidating( validating );
    }

    public void setIgnoringElementContentWhitespace( boolean whitespace )
    {
        m_documentBuilderFactory.setIgnoringElementContentWhitespace( whitespace );
    }

    public void setExpandEntityReferences( boolean expandEntityRef )
    {
        m_documentBuilderFactory.setExpandEntityReferences( expandEntityRef );
    }

    public void setIgnoringComments( boolean ignoreComments )
    {
        m_documentBuilderFactory.setIgnoringComments( ignoreComments );
    }

    public void setCoalescing( boolean coalescing )
    {
        m_documentBuilderFactory.setCoalescing( coalescing );
    }

    public boolean isNamespaceAware()
    {
        return m_documentBuilderFactory.isNamespaceAware();
    }

    public boolean isValidating()
    {
        return m_documentBuilderFactory.isValidating();
    }

    public boolean isIgnoringElementContentWhitespace()
    {
        return m_documentBuilderFactory.isIgnoringElementContentWhitespace();
    }

    public boolean isExpandEntityReferences()
    {
        return m_documentBuilderFactory.isExpandEntityReferences();
    }

    public boolean isIgnoringComments()
    {
        return m_documentBuilderFactory.isIgnoringComments();
    }

    public boolean isCoalescing()
    {
        return m_documentBuilderFactory.isCoalescing();
    }

    public void setAttribute( String name, Object value )
        throws IllegalArgumentException
    {
        m_documentBuilderFactory.setAttribute( name, value );
    }

    public Object getAttribute( String name )
        throws IllegalArgumentException
    {
        return m_documentBuilderFactory.getAttribute( name );
    }
}

