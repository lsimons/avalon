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
 * @version $Id: SourceUtilTestCase.java,v 1.1 2003/04/04 16:36:52 sylvain Exp $
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

	    assertEquals(-1, SourceUtil.indexOfSchemeColon("/file/with:colon"));
	    assertEquals(-1, SourceUtil.indexOfSchemeColon(".foo:bar"));
	    assertEquals(-1, SourceUtil.indexOfSchemeColon("no-colon"));
	}
}
