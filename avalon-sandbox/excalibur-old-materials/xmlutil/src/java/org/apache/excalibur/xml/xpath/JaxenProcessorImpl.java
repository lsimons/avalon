/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included  with this distribution in *
 * the LICENSE.txt file.                                                         *
 *****************************************************************************/
package org.apache.avalon.excalibur.xml.xpath;

import java.util.List;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.thread.ThreadSafe;
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
 * @version CVS $Revision: 1.4 $ $Date: 2002/07/10 08:53:17 $ $Author: donaldp $
 */
public final class JaxenProcessorImpl
    extends AbstractLoggable
    implements XPathProcessor, ThreadSafe
{
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
            return (Node)path.selectSingleNode( (Object)contextNode );
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
            final List list = path.selectNodes( (Object)contextNode );
            return new SimpleNodeList( list );
        }
        catch( final Exception e )
        {
            // ignore it
            return new EmptyNodeList();
        }
    }
}
