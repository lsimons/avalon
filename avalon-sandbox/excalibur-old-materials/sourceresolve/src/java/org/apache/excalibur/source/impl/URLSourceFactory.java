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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceFactory;

/**
 * A factory for a {@link URL} wrapper
 * 
 * @avalon.component
 * @avalon.service type=SourceFactory
 * @x-avalon.info name=url-source
 * @x-avalon.lifestyle type=singleton
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @author <a href="mailto:sylvain@apache.org">Sylvain Wallez</a>
 * @version $Id: URLSourceFactory.java,v 1.5 2003/05/20 20:56:43 bloritsch Exp $
 */
public class URLSourceFactory extends AbstractLogEnabled implements SourceFactory, ThreadSafe
{

    /**
     * Create an URL-based source. This class actually creates an {@link URLSource}, but if another
     * implementation is needed, subclasses can override this method.
     */
    protected Source createURLSource(URL url, Map parameters) throws MalformedURLException, IOException
    {
        URLSource result = new URLSource();
        result.init(url, parameters);
        return result;
    }

    /**
     * Create an file-based source. This class actually creates an {@link FileSource}, but if another
     * implementation is needed, subclasses can override this method.
     */
    protected Source createFileSource(String uri) throws MalformedURLException, IOException
    {
        return new FileSource(uri);
    }

    /**
     * @see org.apache.excalibur.source.SourceFactory#getSource(java.lang.String, java.util.Map)
     */
    public Source getSource(String uri, Map parameters) throws MalformedURLException, IOException
    {
        if (getLogger().isDebugEnabled())
        {
            final String message = "Creating source object for " + uri;
            getLogger().debug(message);
        }

        // First check if it's a file
        if (uri.startsWith("file:"))
        {
            // Yes : return a file source
            return createFileSource(uri);
        }
        else
        {
            // Not a "file:" : create an URLSource
            // First try to create the URL
            URL url;
            try
            {
                url = new URL(uri);
            }
            catch (MalformedURLException mue)
            {
                // Maybe a file name containing a ':' ?
                if (getLogger().isDebugEnabled())
                {
                    this.getLogger().debug("URL " + uri + " is malformed. Assuming it's a file path.", mue);
                }
                return createFileSource(uri);
            }

            return createURLSource(url, parameters);
        }
    }

    /**
     * @see org.apache.excalibur.source.SourceFactory#release(org.apache.excalibur.source.Source)
     */
    public void release(Source source)
    {
        // do nothing here
    }
}
