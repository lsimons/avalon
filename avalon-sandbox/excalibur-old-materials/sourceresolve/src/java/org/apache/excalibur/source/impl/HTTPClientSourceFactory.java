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

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceFactory;

/**
 * {@link HTTPClientSource} Factory class.
 *
 * @avalon.component
 * @avalon.service type=SourceFactory
 * @x-avalon.info name=httpclient-source
 * @x-avalon.lifestyle type=singleton
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Id: HTTPClientSourceFactory.java,v 1.1 2003/07/02 13:23:58 crafterm Exp $
 */
public final class HTTPClientSourceFactory extends AbstractLogEnabled
    implements SourceFactory, ThreadSafe
{
    /**
     * Creates a {@link HTTPClientSource} instance.
     */
    public Source getSource( final String uri, final Map parameters )
        throws MalformedURLException, IOException
    {
        try
        {
            final HTTPClientSource source = 
                new HTTPClientSource( uri, parameters );
            ContainerUtil.enableLogging( source, getLogger() );
            ContainerUtil.initialize( source );
            return source;
        }
        catch ( final MalformedURLException e )
        {
            throw e;
        }
        catch ( final IOException e ) 
        {
            throw e;
        }
        catch ( final Exception e )
        {
            final StringBuffer message = new StringBuffer();
            message.append( "Exception thrown while creating " );
            message.append( HTTPClientSource.class.getName() );

            throw new SourceException( message.toString(), e );
        }
    }

    /**
     * Releases the given {@link Source} object.
     *
     * @param source {@link Source} object to be released
     */
    public void release( final Source source )
    {
        if ( source instanceof HTTPClientSource )
        {
            HTTPClientSource src = (HTTPClientSource) source;

            ContainerUtil.dispose( src );
        }
        else
        {
            if ( getLogger().isDebugEnabled() )
            {
                getLogger().debug( 
                    "Ignoring request to release non-HTTPClientSource object" 
                );
            }
        }
    }
}
