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

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.thread.ThreadSafe;

import com.icl.saxon.Context;
import com.icl.saxon.TransformerFactoryImpl;
import com.icl.saxon.tinytree.TinyBuilder;
import com.icl.saxon.expr.Expression;
import com.icl.saxon.expr.StandaloneContext;
import com.icl.saxon.expr.Value;
import com.icl.saxon.expr.XPathException;
import com.icl.saxon.expr.NodeSetValue;
import com.icl.saxon.om.DocumentInfo;
import com.icl.saxon.om.NamePool;
import com.icl.saxon.om.NodeInfo;
import com.icl.saxon.om.NodeEnumeration;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class defines the implementation of the {@link XPathProcessor}
 * component. This implementation depends on Saxon 6.X XSLT processor.
 * This implementation was tested with Saxon 6.5.2 release.
 *
 * To configure it, add the following lines in the
 * <file>cocoon.xconf</file> file:
 *
 * <pre>
 * &lt;xpath-processor class="org.apache.cocoon.components.xpath.Saxon6ProcessorImpl"&gt;
 * &lt;/xpath-processor&gt;
 * </pre>
 *
 * @author <a href="mailto:vgritsenko@apache.org">Vadim Gritsenko</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 12:47:42 $ $Author: leosimons $
 */
public class Saxon6ProcessorImpl
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
            Value value = evaluate(contextNode, str, resolver);
            if (value == null)
            {
                return false;
            }

            return value.asBoolean();
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
            Value value = evaluate(contextNode, str, resolver);
            if (value == null)
            {
                return null;
            }

            return new Double(value.asNumber());
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
            Value value = evaluate(contextNode, str, resolver);
            if (value == null)
            {
                return null;
            }

            return value.asString();
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
            Value value = evaluate(contextNode, str, resolver);
            if (value == null || value.getDataType() != Value.NODESET)
            {
                return null;
            }

            return (Node)((NodeSetValue)value).getFirst();
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
            Value value = evaluate(contextNode, str, resolver);
            if (value.getDataType() != Value.NODESET)
            {
                return null;
            }

            NodeSetValue nodeset = (NodeSetValue)value;
            NodeEnumeration enumeration = nodeset.enumerate();
            Node[] nodes = new Node[nodeset.getCount()];
            for (int i = 0; i < nodes.length; i++)
            {
                nodes[i] = (Node)enumeration.nextElement();
            }

            return new NodeListImpl(nodes);
        } catch (final Exception e) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Failed to evaluate '" + str + "'", e);
            }

            // ignore it
            return null;
        }
    }

    private Value evaluate(Node contextNode, String str, PrefixResolver resolver)
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

            DocumentInfo doc = ((NodeInfo)contextNode).getDocumentRoot();
            NamePool pool = doc.getNamePool();
            if (pool == null)
            {
                pool = NamePool.getDefaultNamePool();
                doc.setNamePool(pool);
            }
            Expression expression = Expression.make(str, new Saxon6Context(pool, resolver));

            Context context = new Context();
            context.setContextNode((NodeInfo)contextNode);
            context.setPosition(1);
            context.setLast(1);

            return expression.evaluate(context);
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

    private class Saxon6Context extends StandaloneContext
    {
        private final PrefixResolver resolver;

        public Saxon6Context(NamePool namePool, PrefixResolver resolver)
        {
            super(namePool);
            this.resolver = resolver;
        }

        public String getURIForPrefix(String prefix) throws XPathException
        {
            return resolver.prefixToNamespace(prefix);
        }
    }
}
