/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included  with this distribution in *
 * the LICENSE.txt file.                                                         *
 *****************************************************************************/
package org.apache.avalon.excalibur.xml.xpath;

import java.util.HashMap;
import java.util.List;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.jaxen.dom.DOMXPath;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.jaxen.NamespaceContext;

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
 * @version CVS $Revision: 1.6 $ $Date: 2002/10/02 01:47:05 $ $Author: donaldp $
 */
public final class JaxenProcessorImpl extends AbstractLogEnabled implements XPathProcessor, Configurable, Component, ThreadSafe, NamespaceContext
{
    private final HashMap m_mappings = new HashMap();

    public void configure( Configuration configuration ) throws ConfigurationException
    {
        final Configuration namespaceMappings = configuration.getChild( "namespace-mappings", true );
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
            final DOMXPath path = new DOMXPath( str );
            path.setNamespaceContext( this );
            return (Node)path.selectSingleNode( contextNode );
        }
        catch( final Exception e )
        {
            // ignore it
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
            final DOMXPath path = new DOMXPath( str );
            path.setNamespaceContext( this );
            final List list = path.selectNodes( contextNode );
            return new SimpleNodeList( list );
        }
        catch( final Exception e )
        {
            // ignore it
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
            final DOMXPath path = new DOMXPath( str );
            path.setNamespaceContext( this );
            return path.booleanValueOf( contextNode );
        }
        catch( final Exception e )
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
            final DOMXPath path = new DOMXPath( str );
            path.setNamespaceContext( this );
            return path.numberValueOf( contextNode );
        }
        catch( final Exception e )
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
            final DOMXPath path = new DOMXPath( str );
            path.setNamespaceContext( this );
            return path.stringValueOf( contextNode );
        }
        catch( final Exception e )
        {
            return null;
        }
    }

    public String translateNamespacePrefixToUri( String prefix )
    {
        return (String)m_mappings.get( prefix );
    }
}
