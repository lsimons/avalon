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
package org.apache.excalibur.source.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceValidity;

/**
 * Abstract base class for a source implementation.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.11 $ $Date: 2003/05/20 20:56:43 $
 */

public abstract class AbstractSource
    implements Source
{
    private boolean m_gotInfos;
    private long m_lastModificationDate;
    private long m_contentLength;
    private String m_systemId;

    private String m_scheme;

    /**
     * Get the last modification date and content length of the source.
     * Any exceptions are ignored.
     * Override this to get the real information
     */
    protected void getInfos()
    {
        this.m_contentLength = -1;
        this.m_lastModificationDate = 0;
    }

    protected void checkInfos()
    {
        if( !m_gotInfos )
        {
            getInfos();
            m_gotInfos = true;
        }
    }

    /**
     * Return an <code>InputStream</code> object to read from the source.
     *
     * @throws SourceException if file not found or
     *         HTTP location does not exist.
     * @throws IOException if I/O error occured.
     */
    public InputStream getInputStream()
        throws IOException, SourceException
    {
        return null;
    }

    /**
     * Return the unique identifer for this source
     */
    public String getURI()
    {
        return m_systemId;
    }

    /**
     * Return the protocol identifier.
     */
    public String getScheme() 
    {
        return this.m_scheme;
    }

    /**
     *  Get the Validity object. This can either wrap the last modification
     *  date or the expires information or...
     *  If it is currently not possible to calculate such an information
     *  <code>null</code> is returned.
     */
    public SourceValidity getValidity()
    {
        return null;
    }

    /**
     * Refresh this object and update the last modified date
     * and content length.
     */
    public void refresh()
    {
        m_gotInfos = false;
    }

    /**
     * The mime-type of the content described by this object.
     * If the source is not able to determine the mime-type by itself
     * this can be null.
     */
    public String getMimeType()
    {
        return null;
    }

    /**
     * Return the content length of the content or -1 if the length is
     * unknown
     */
    public long getContentLength()
    {
        checkInfos();
        return this.m_contentLength;
    }

    /**
     * Get the last modification date of the source or 0 if it
     * is not possible to determine the date.
     */
    public long getLastModified()
    {
        checkInfos();
        return this.m_lastModificationDate;
    }
    /**
     * Sets the contentLength.
     * @param contentLength The contentLength to set
     */
    protected void setContentLength(long contentLength)
    {
        m_contentLength = contentLength;
    }

    /**
     * Sets the lastModificationDate.
     * @param setLastModified The lastModificationDate to set
     */
    protected void setLastModified(long lastModificationDate)
    {
        m_lastModificationDate = lastModificationDate;
    }

    /**
     * Sets the scheme.
     * @param scheme The scheme to set
     */
    protected void setScheme(String scheme)
    {
        m_scheme = scheme;
    }

    /**
     * Sets the systemId.
     * @param systemId The systemId to set
     */
    protected void setSystemId(String systemId)
    {
        m_systemId = systemId;
    }

}
