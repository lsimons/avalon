/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.xml.xpath;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is the interface of the XPath processor.
 *
 * @author <a href="mailto:dims@yahoo.com">Davanum Srinivas</a>
 * @version CVS $Revision: 1.5 $ $Date: 2003/01/22 02:18:17 $ $Author: jefft $
 */
public interface XPathProcessor
{
    /**
     * The role implemented by an <code>XSLTProcessor</code>.
     */
    String ROLE = XPathProcessor.class.getName();

    /**
     * Evaluate XPath expression within a context.
     *
     * @param contextNode The context node.
     * @param str A valid XPath string.
     * @return expression result as boolean.
     */
    boolean evaluateAsBoolean( Node contextNode, String str );

    /**
     * Evaluate XPath expression within a context.
     *
     * @param contextNode The context node.
     * @param str A valid XPath string.
     * @return expression result as number.
     */
    Number evaluateAsNumber( Node contextNode, String str );

    /**
     * Evaluate XPath expression within a context.
     *
     * @param contextNode The context node.
     * @param str A valid XPath string.
     * @return expression result as string.
     */
    String evaluateAsString( Node contextNode, String str );

    /**
     * Use an XPath string to select a single node. XPath namespace
     * prefixes are resolved from the context node, which may not
     * be what you want (see the next method).
     *
     * @param contextNode The node to start searching from.
     * @param str A valid XPath string.
     * @return The first node found that matches the XPath, or null.
     */
    Node selectSingleNode( Node contextNode, String str );

    /**
     *  Use an XPath string to select a nodelist.
     *  XPath namespace prefixes are resolved from the contextNode.
     *
     *  @param contextNode The node to start searching from.
     *  @param str A valid XPath string.
     *  @return A List, should never be null.
     */
    NodeList selectNodeList( Node contextNode, String str );
}
