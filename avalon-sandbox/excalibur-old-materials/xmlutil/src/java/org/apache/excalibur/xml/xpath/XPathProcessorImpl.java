/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation. All rights
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
package org.apache.excalibur.xml.xpath;

import java.util.HashMap;

import javax.xml.transform.TransformerException;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class defines the implementation of the {@link XPathProcessor}
 * component.
 *
 * To configure it, add the following lines in the
 * <file>cocoon.xconf</file> file:
 *
 * <pre>
 * &lt;xpath-processor class="org.apache.cocoon.components.xpath.XPathProcessorImpl"&gt;
 * &lt;/xpath-processor&gt;
 * </pre>
 *
 * @author <a href="mailto:dims@yahoo.com">Davanum Srinivas</a>
 * @version CVS $Revision: 1.10 $ $Date: 2003/02/27 09:24:47 $ $Author: cziegeler $
 */
public final class XPathProcessorImpl extends AbstractLogEnabled implements XPathProcessor, Configurable, PrefixResolver, Component, ThreadSafe
{

    private String m_baseURI;
    private final HashMap m_mappings = new HashMap();

    public void configure( Configuration configuration ) throws ConfigurationException
    {
        final Configuration namespaceMappings = configuration.getChild( "namespace-mappings", true );
        m_baseURI = namespaceMappings.getAttribute( "base-uri", null );

        final Configuration[] namespaces = namespaceMappings.getChildren( "namespace" );
        for( int i = 0; i < namespaces.length; i++ )
        {
            final String prefix = namespaces[ i ].getAttribute( "prefix" );
            final String uri = namespaces[ i ].getAttribute( "uri" );
            m_mappings.put( prefix, uri );
        }
    }

    /**
     * Use an XPath string to select a single node. XPath namespace
     * prefixes are resolved from the context node, which may not
     * be what you want (see the next method).
     *
     * @param contextNode The node to start searching from.
     * @param str A valid XPath string.
     * @return The first node found that matches the XPath, or null.
     */
    public Node selectSingleNode( final Node contextNode,
                                  final String str )
    {
        try
        {
            final XObject result = XPathAPI.eval( contextNode, str, this );
            return result.nodeset().nextNode();
        }
        catch( final TransformerException te )
        {
            return null;
        }
    }

    /**
     *  Use an XPath string to select a nodelist.
     *  XPath namespace prefixes are resolved from the contextNode.
     *
     *  @param contextNode The node to start searching from.
     *  @param str A valid XPath string.
     *  @return A NodeList, should never be null.
     */
    public NodeList selectNodeList( final Node contextNode,
                                    final String str )
    {
        try
        {
            final XObject result = XPathAPI.eval( contextNode, str, this );
            return result.nodelist();
        }
        catch( final TransformerException te )
        {
            return new EmptyNodeList();
        }
    }

    /** Evaluate XPath expression within a context.
     *
     * @param contextNode The context node.
     * @param str A valid XPath string.
     * @return expression result as boolean.
     */
    public boolean evaluateAsBoolean( Node contextNode, String str )
    {
        try
        {
            final XObject result = XPathAPI.eval( contextNode, str, this );
            return result.bool();
        }
        catch( final TransformerException te )
        {
            return false;
        }
    }

    /** Evaluate XPath expression within a context.
     *
     * @param contextNode The context node.
     * @param str A valid XPath string.
     * @return expression result as number.
     */
    public Number evaluateAsNumber( Node contextNode, String str )
    {
        try
        {
            final XObject result = XPathAPI.eval( contextNode, str, this );
            return new Double( result.num() );
        }
        catch( final TransformerException te )
        {
            return null;
        }
    }

    /** Evaluate XPath expression within a context.
     *
     * @param contextNode The context node.
     * @param str A valid XPath string.
     * @return expression result as string.
     */
    public String evaluateAsString( Node contextNode, String str )
    {
        try
        {
            final XObject result = XPathAPI.eval( contextNode, str, this );
            return result.str();
        }
        catch( final TransformerException te )
        {
            return null;
        }
    }

    public String getBaseIdentifier()
    {
        return m_baseURI;
    }

    public String getNamespaceForPrefix( String prefix )
    {
        return (String)m_mappings.get( prefix );
    }

    public String getNamespaceForPrefix( String prefix, Node node )
    {
        return getNamespaceForPrefix( prefix );
    }

    public boolean handlesNullPrefixes()
    {
        return true;
    }
}

