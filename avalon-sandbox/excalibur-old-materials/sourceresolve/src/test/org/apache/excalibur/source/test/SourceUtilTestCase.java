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
package org.apache.excalibur.source.test;

import org.apache.excalibur.source.SourceUtil;

import junit.framework.TestCase;

/**
 * Test case for SourceUtil.
 *
 * @author <a href="mailto:sylvain@apache.org">Sylvain Wallez</a>
 * @version $Id: SourceUtilTestCase.java,v 1.4 2003/06/10 14:40:28 bloritsch Exp $
 */
public class SourceUtilTestCase extends TestCase
{
    public SourceUtilTestCase()
    {
        this("SourceUtil");
    }

    public SourceUtilTestCase(String name)
    {
        super(name);
    }

    public void testNominalScheme() throws Exception
    {
        String uri = "http://foo";
        assertEquals(4, SourceUtil.indexOfSchemeColon(uri));
        assertEquals("http", SourceUtil.getScheme(uri));
        assertEquals("//foo", SourceUtil.getSpecificPart(uri));
    }

    public void testDoubleColon() throws Exception
    {
        assertEquals(4, SourceUtil.indexOfSchemeColon("file:foo:bar"));
    }

    public void testSpecialScheme() throws Exception
    {
        String uri = "a-+.:foo"; // Strange, but valid !
        assertEquals(4, SourceUtil.indexOfSchemeColon(uri));
        assertEquals("a-+.", SourceUtil.getScheme(uri));
        assertEquals("foo", SourceUtil.getSpecificPart(uri));
    }

    public void testSpecialPart() throws Exception
    {
        String uri = "bar:";
        assertEquals(3, SourceUtil.indexOfSchemeColon(uri));
        assertEquals("bar", SourceUtil.getScheme(uri));
        assertEquals("", SourceUtil.getSpecificPart(uri));
    }

    public void testInvalidScheme() throws Exception
    {
        String uri = "2foo:bar";
        assertEquals(-1, SourceUtil.indexOfSchemeColon(uri));
        assertEquals(null, SourceUtil.getScheme(uri));
        assertEquals(null, SourceUtil.getSpecificPart(uri));

        // Invalid character before any of the allowed ones
        assertEquals(-1, SourceUtil.indexOfSchemeColon("h ttp:foo"));
        assertEquals(-1, SourceUtil.indexOfSchemeColon(" http:foo"));
        assertEquals(-1, SourceUtil.indexOfSchemeColon("http :foo"));

       // Invalid character between allowed ranges
        assertEquals(-1, SourceUtil.indexOfSchemeColon("h_ttp:foo"));
        assertEquals(-1, SourceUtil.indexOfSchemeColon("_http:foo"));
        assertEquals(-1, SourceUtil.indexOfSchemeColon("http_:foo"));

        // Invalid character after any of the allowed ones
        assertEquals(-1, SourceUtil.indexOfSchemeColon("h~ttp:foo"));
        assertEquals(-1, SourceUtil.indexOfSchemeColon("~http:foo"));
        assertEquals(-1, SourceUtil.indexOfSchemeColon("http~:foo"));
	}

    public void testAbsolutize()
    {
        String base = "http://a/b/c/d;p?q";

        //
        // Test examples from RFC 2396
        //

        // normal cases
        assertEquals("g:h", SourceUtil.absolutize(base, "g:h"));
        assertEquals("http://a/b/c/g", SourceUtil.absolutize(base, "g"));
        assertEquals("http://a/b/c/g", SourceUtil.absolutize(base, "./g"));
        assertEquals("http://a/b/c/g/", SourceUtil.absolutize(base, "g/"));
        assertEquals("http://a/g", SourceUtil.absolutize(base, "/g"));
        assertEquals("http://g", SourceUtil.absolutize(base, "//g"));
        assertEquals("http://a/b/c/?y", SourceUtil.absolutize(base, "?y"));
        assertEquals("http://a/b/c/g?y", SourceUtil.absolutize(base, "g?y"));
        assertEquals("http://a/b/c/d;p?q#s", SourceUtil.absolutize(base, "#s"));
        assertEquals("http://a/b/c/g#s", SourceUtil.absolutize(base, "g#s"));
        assertEquals("http://a/b/c/g?y#s", SourceUtil.absolutize(base, "g?y#s"));
        assertEquals("http://a/b/c/;x", SourceUtil.absolutize(base, ";x"));
        assertEquals("http://a/b/c/g;x", SourceUtil.absolutize(base, "g;x"));
        assertEquals("http://a/b/c/g;x?y#s", SourceUtil.absolutize(base, "g;x?y#s"));
        assertEquals("http://a/b/c/", SourceUtil.absolutize(base, "."));
        assertEquals("http://a/b/c/", SourceUtil.absolutize(base, "./"));
        assertEquals("http://a/b/", SourceUtil.absolutize(base, ".."));
        assertEquals("http://a/b/", SourceUtil.absolutize(base, "../"));
        assertEquals("http://a/b/g", SourceUtil.absolutize(base, "../g"));
        assertEquals("http://a/", SourceUtil.absolutize(base, "../.."));
        assertEquals("http://a/", SourceUtil.absolutize(base, "../../"));
        assertEquals("http://a/g", SourceUtil.absolutize(base, "../../g"));

        // abnormal cases
        assertEquals("http://a/../g", SourceUtil.absolutize(base, "../../../g"));
        assertEquals("http://a/../../g", SourceUtil.absolutize(base, "../../../../g"));

        assertEquals("http://a/./g", SourceUtil.absolutize(base, "/./g"));
        assertEquals("http://a/../g", SourceUtil.absolutize(base, "/../g"));
        assertEquals("http://a/b/c/g.", SourceUtil.absolutize(base, "g."));
        assertEquals("http://a/b/c/.g", SourceUtil.absolutize(base, ".g"));
        assertEquals("http://a/b/c/g..", SourceUtil.absolutize(base, "g.."));
        assertEquals("http://a/b/c/..g", SourceUtil.absolutize(base, "..g"));

        assertEquals("http://a/b/g", SourceUtil.absolutize(base, "./../g"));
        assertEquals("http://a/b/c/g/", SourceUtil.absolutize(base, "./g/."));
        assertEquals("http://a/b/c/g/h", SourceUtil.absolutize(base, "g/./h"));
        assertEquals("http://a/b/c/h", SourceUtil.absolutize(base, "g/../h"));
        assertEquals("http://a/b/c/g;x=1/y", SourceUtil.absolutize(base, "g;x=1/./y"));
        assertEquals("http://a/b/c/y", SourceUtil.absolutize(base, "g;x=1/../y"));

        assertEquals("http://a/b/c/g?y/./x", SourceUtil.absolutize(base, "g?y/./x"));
        assertEquals("http://a/b/c/g?y/../x", SourceUtil.absolutize(base, "g?y/../x"));
        assertEquals("http://a/b/c/g#s/./x", SourceUtil.absolutize(base, "g#s/./x"));
        assertEquals("http://a/b/c/g#s/../x", SourceUtil.absolutize(base, "g#s/../x"));

        //
        // other tests
        //

        // if there's a scheme, url is absolute
        assertEquals("http://a", SourceUtil.absolutize("", "http://a"));
        assertEquals("cocoon:/a", SourceUtil.absolutize("", "cocoon:/a", true));

        // handle null base
        assertEquals("a", SourceUtil.absolutize(null, "a"));

        // handle network reference
        assertEquals("http://a/b", SourceUtil.absolutize("http://myhost", "//a/b"));

        // handle empty authority
        assertEquals("http:///a/b", SourceUtil.absolutize("http:///a/", "b"));

        // cocoon and context protocols
        assertEquals("cocoon://a/b/c", SourceUtil.absolutize("cocoon://a/b/", "c", true));
        assertEquals("cocoon:/a/b/c", SourceUtil.absolutize("cocoon:/a/b/", "c", true));
        assertEquals("cocoon://c", SourceUtil.absolutize("cocoon://a", "c", true));
        assertEquals("cocoon://c", SourceUtil.absolutize("cocoon://a/b/", "../../c", true));

        // Test relative File URI
        assertEquals("file://C:/projects/avalon-excalibur/build/docs/framework/api/index.html%3Ffoo=bar",
                SourceUtil.absolutize("file://C:/projects/avalon-excalibur/build/docs/framework/", "api/index.html%3Ffoo=bar"));
        assertEquals( "file://C:/foo/api/", SourceUtil.absolutize( "file://C:/foo/", "api/" ) );
    }
}
