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

import java.util.List;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.thread.ThreadSafe;

import org.jaxen.NamespaceContext;
import org.jaxen.dom.DOMXPath;
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
 * &lt;xpath-processor class="org.apache.cocoon.components.xpath.JaxenProcessorImpl"&gt;
 * &lt;/xpath-processor&gt;
 * </pre>
 *
 * @author <a href="mailto:dims@yahoo.com">Davanum Srinivas</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 12:47:42 $ $Author: leosimons $
 */
public final class JaxenProcessorImpl
        extends AbstractProcessorImpl
        implements XPathProcessor, Component, ThreadSafe
{
    /**
     * Evaluate XPath expression within a context.
     *
     * @param contextNode The context node.
     * @param str A valid XPath string.
     * @param resolver a PrefixResolver, used for resolving namespace prefixes
     * @return expression result as boolean.
     */
    public boolean evaluateAsBoolean(Node contextNode, String str, PrefixResolver resolver)
    {
        try
        {
            final DOMXPath path = new DOMXPath( str );
            path.setNamespaceContext( new JaxenResolver(resolver) );
            return path.booleanValueOf( contextNode );
        }
        catch( final Exception e )
        {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Failed to evaluate '" + str + "'", e);
            }

            // ignore it
            return false;
        }
    }

    /**
     * Evaluate XPath expression within a context.
     *
     * @param contextNode The context node.
     * @param str A valid XPath string.
     * @param resolver a PrefixResolver, used for resolving namespace prefixes
     * @return expression result as number.
     */
    public Number evaluateAsNumber(Node contextNode, String str, PrefixResolver resolver)
    {
        try
        {
            final DOMXPath path = new DOMXPath( str );
            path.setNamespaceContext( new JaxenResolver(resolver) );
            return path.numberValueOf( contextNode );
        }
        catch( final Exception e )
        {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Failed to evaluate '" + str + "'", e);
            }

            // ignore it
            return null;
        }
    }

    /**
     * Evaluate XPath expression within a context.
     *
     * @param contextNode The context node.
     * @param str A valid XPath string.
     * @param resolver a PrefixResolver, used for resolving namespace prefixes
     * @return expression result as string.
     */
    public String evaluateAsString(Node contextNode, String str, PrefixResolver resolver)
    {
        try
        {
            final DOMXPath path = new DOMXPath( str );
            path.setNamespaceContext( new JaxenResolver(resolver) );
            return path.stringValueOf( contextNode );
        }
        catch( final Exception e )
        {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Failed to evaluate '" + str + "'", e);
            }

            // ignore it
            return null;
        }
    }

    /**
     * Use an XPath string to select a single node.
     *
     * @param contextNode The node to start searching from.
     * @param str A valid XPath string.
     * @param resolver a PrefixResolver, used for resolving namespace prefixes
     * @return The first node found that matches the XPath, or null.
     */
    public Node selectSingleNode(Node contextNode, String str, PrefixResolver resolver)
    {
        try
        {
            final DOMXPath path = new DOMXPath( str );
            path.setNamespaceContext( new JaxenResolver(resolver) );
            return (Node)path.selectSingleNode( contextNode );
        }
        catch( final Exception e )
        {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Failed to evaluate '" + str + "'", e);
            }

            // ignore it
            return null;
        }
    }

    /**
     *  Use an XPath string to select a nodelist.
     *
     * @param contextNode The node to start searching from.
     * @param str A valid XPath string.
     * @param resolver a PrefixResolver, used for resolving namespace prefixes
     * @return A List, should never be null.
     */
    public NodeList selectNodeList(Node contextNode, String str, PrefixResolver resolver)
    {
        try
        {
            final DOMXPath path = new DOMXPath( str );
            path.setNamespaceContext( new JaxenResolver(resolver) );
            final List list = path.selectNodes( contextNode );
            return new SimpleNodeList( list );
        }
        catch( final Exception e )
        {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Failed to evaluate '" + str + "'", e);
            }

            // ignore it
            return new EmptyNodeList();
        }
    }

    /**
     * A Jaxen-specific wrapper for the PrefixResolver.
     */
    private static class JaxenResolver implements NamespaceContext
    {
        private final PrefixResolver resolver;

        public JaxenResolver(PrefixResolver resolver)
        {
            this.resolver = resolver;
        }

        public String translateNamespacePrefixToUri(String prefix)
        {
            return resolver.prefixToNamespace(prefix);
        }
    }
}
