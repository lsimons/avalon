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
package org.apache.excalibur.xml.xpath.test;

import org.apache.avalon.excalibur.testcase.ExcaliburTestCase;
import org.apache.avalon.framework.component.Component;
import org.apache.excalibur.xml.dom.DOMParser;
import org.apache.excalibur.xml.xpath.XPathProcessor;
import org.apache.excalibur.xml.xpath.PrefixResolver;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;

import java.io.StringReader;

public class XPathTestCase extends ExcaliburTestCase
{
    /** A small test document. */
    private static final String CONTENT1 =
        "<?xml version=\"1.0\"?>" +
        "<test:root xmlns:test=\"http://localhost/test\">" +
            "<test:element1/>" +
            "<test:element2/>" +
        "</test:root>";

    /** Second testdocument, has a different namespace than {@link #CONTENT1}. */
    private static final String CONTENT2 =
        "<?xml version=\"1.0\"?>" +
        "<test:root xmlns:test=\"http://localhost/test2\">" +
            "<test:element1/>" +
            "<test:element2/>" +
        "</test:root>";

    public XPathTestCase(String name) {
        super(name);
    }

    public void testXPath() throws Exception {
        DOMParser parser = null;
        XPathProcessor processor = null;
        try {
            parser = (DOMParser)manager.lookup(DOMParser.ROLE);
            processor = (XPathProcessor)manager.lookup(XPathProcessor.ROLE);

            Document document1 = parser.parseDocument(new InputSource(new StringReader(CONTENT1)));
            Document document2 = parser.parseDocument(new InputSource(new StringReader(CONTENT2)));

            // test with a namespace prefix configured in the component configuration
            String testExpr = "count(/test:root/*)";
            Number number = processor.evaluateAsNumber(document1, testExpr);
            assertEquals(2, number.intValue());

            // test with a custom prefix resolver using a different document in a different namespace,
            // to be sure the custom prefix resolver is used
            number = processor.evaluateAsNumber(document2, testExpr, new PrefixResolver() {
                public String prefixToNamespace(String prefix)
                {
                    if (prefix.equals("test"))
                        return "http://localhost/test2";
                    return null;
                }
            });
            assertEquals(2, number.intValue());

        } finally {
            if (parser != null)
                manager.release((Component)parser);
            if (processor != null)
                manager.release((Component)processor);
        }
    }
}
