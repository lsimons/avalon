/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.xml.xpath;

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
 * @version CVS $Revision: 1.7 $ $Date: 2002/10/02 01:52:25 $ $Author: donaldp $
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

