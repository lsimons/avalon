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

import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.avalon.framework.component.Component;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.Item;
import net.sf.saxon.xpath.XPathException;
import net.sf.saxon.xpath.StandaloneContext;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.tinytree.TinyBuilder;
import net.sf.saxon.value.Type;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.DoubleValue;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import java.util.ArrayList;

/**
 * This class defines the implementation of the {@link XPathProcessor}
 * component. This implementation depends on Saxon 7.X XSLT processor.
 * This implementation was tested with Saxon 7.5 release.
 *
 * To configure it, add the following lines in the
 * <file>cocoon.xconf</file> file:
 *
 * <pre>
 * &lt;xslt-processor class="org.apache.cocoon.components.xpath.Saxon7ProcessorImpl"&gt;
 * &lt;/xslt-processor&gt;
 * </pre>
 *
 * @author <a href="mailto:vgritsenko@apache.org">Vadim Gritsenko</a>
 * @version CVS $Id: Saxon7ProcessorImpl.java,v 1.1 2003/08/06 21:44:37 vgritsenko Exp $
 */
public class Saxon7ProcessorImpl
        extends AbstractProcessorImpl
        implements XPathProcessor, Component, ThreadSafe
{
    private static final TransformerFactory factory = new TransformerFactoryImpl();

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
            Item item = evaluateSingle(contextNode, str, resolver);
            if (item == null)
            {
                return false;
            }

            if (item.getItemType() == Type.BOOLEAN)
            {
                return ((BooleanValue)item).getValue();
            }

            return Boolean.valueOf(item.getStringValue()).booleanValue();
        }
        catch (final Exception e)
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
            Item item = evaluateSingle(contextNode, str, resolver);
            if (item == null)
            {
                return null;
            }

            if (item.getItemType() == Type.NUMBER)
            {
                return new Double(((DoubleValue)item).getValue());
            }

            return Double.valueOf(item.getStringValue());
        }
        catch (final Exception e)
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
    public String evaluateAsString(Node contextNode, String str, PrefixResolver resolver) {
        try
        {
            Item item = evaluateSingle(contextNode, str, resolver);
            if (item == null)
            {
                return null;
            }

            return item.getStringValue();
        }
        catch (final Exception e)
        {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Failed to evaluate '" + str + "'", e);
            }

            // ignore it
            return null;
        }
    }

    public Node selectSingleNode(Node contextNode, String str, PrefixResolver resolver)
    {
        try
        {
            Item item = evaluateSingle(contextNode, str, resolver);

            return (Node)item;
        }
        catch (final Exception e)
        {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Failed to evaluate '" + str + "'", e);
            }

            // ignore it
            return null;
        }
    }

    public NodeList selectNodeList(Node contextNode, String str, PrefixResolver resolver)
    {
        try
        {
            SequenceIterator iterator = evaluate(contextNode, str, resolver);
            ArrayList nodes = new ArrayList();
            while (iterator.hasNext())
            {
                Node node = (Node)iterator.current();
                nodes.add(node);
            }

            return new NodeListImpl((Node[])nodes.toArray());
        } catch (final Exception e) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Failed to evaluate '" + str + "'", e);
            }

            // ignore it
            return null;
        }
    }

    private Item evaluateSingle(Node contextNode, String str, PrefixResolver resolver)
    {
        try
        {
            SequenceIterator iterator = evaluate(contextNode, str, resolver);
            if (iterator == null)
            {
                return null;
            }

            return iterator.current();
        }
        catch (final Exception e)
        {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Failed to evaluate '" + str + "'", e);
            }

            // ignore it
            return null;
        }
    }

    private SequenceIterator evaluate(Node contextNode, String str, PrefixResolver resolver)
    {
        try
        {
            if (!(contextNode instanceof NodeInfo))
            {
                getLogger().debug("Input tree is not SAXON TinyTree, converting");
                DOMSource source = new DOMSource(contextNode);
                TinyBuilder result = new TinyBuilder();
                factory.newTransformer().transform(source, result);
                contextNode = (Node)result.getCurrentDocument();
            }

            Expression expression = Expression.make(str, new Saxon7Context((NodeInfo)contextNode, resolver));
            XPathContext context = new XPathContext((NodeInfo)contextNode);
            return expression.iterate(context);
        }
        catch (final Exception e)
        {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Failed to evaluate '" + str + "'", e);
            }

            // ignore it
            return null;
        }
    }


    private class Saxon7Context extends StandaloneContext
    {
        private final PrefixResolver resolver;

        public Saxon7Context(NodeInfo node, PrefixResolver resolver)
        {
            super(node);
            this.resolver = resolver;
        }

        public String getURIForPrefix(String prefix) throws XPathException
        {
            return resolver.prefixToNamespace(prefix);
        }
    }
}
