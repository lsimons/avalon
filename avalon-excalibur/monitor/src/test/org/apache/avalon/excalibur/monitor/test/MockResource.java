/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation. All rights
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
 * 4. The names "D-Haven" and "Apache Software Foundation"
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
package org.apache.avalon.excalibur.monitor.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.apache.avalon.excalibur.monitor.ResourceOutputStream;
import org.apache.avalon.excalibur.monitor.ResourceWriter;
import org.apache.avalon.excalibur.monitor.StreamResource;

/**
 * The MockResource object so that we can enable the tests.
 */
public class MockResource extends StreamResource
{
    private final Mock m_mock;

    /**
     * Instantiate the FileResource
     */
    public MockResource( final String resource )
        throws Exception
    {
        this( new Mock( resource ) );
    }

    public MockResource( final Mock resource )
        throws Exception
    {
        super( resource.getName() );
        m_mock = resource;
        setPreviousModified( m_mock.lastModified() );
    }

    /**
     * Determines the last time this resource was modified
     */
    public long lastModified()
    {
        return m_mock.lastModified();
    }

    /**
     * Sets the resource value with an OutputStream
     */
    public InputStream getResourceAsStream()
        throws IOException
    {
        return new ByteArrayInputStream( m_mock.getContent().getBytes() );
    }

    /**
     * Sets the resource value with a Writer
     */
    public Reader getResourceAsReader()
        throws IOException
    {
        return new InputStreamReader( getResourceAsStream() );
    }

    /**
     * Sets the resource value with an OutputStream
     */
    public OutputStream setResourceAsStream()
        throws IOException
    {
        return new ResourceOutputStream( new MockOutputStream( m_mock ), this );
    }

    /**
     * Sets the resource value with a Writer
     */
    public Writer setResourceAsWriter()
        throws IOException
    {
        return new ResourceWriter( new OutputStreamWriter( new MockOutputStream( m_mock ) ), this );
    }
}
