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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceFactory;

/**
 * A factory for a {@link URL} wrapper
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version $Id: URLSourceFactory.java,v 1.2 2003/01/30 07:57:10 cziegeler Exp $
 */
public class URLSourceFactory
    extends AbstractLogEnabled
    implements SourceFactory, Parameterizable, ThreadSafe
{

    /** The URLSource class used */
    protected Class m_urlSourceClass;

    public void parameterize( Parameters pars )
        throws ParameterException
    {
        final String urlSourceClassName = pars.getParameter( "url-source",
                                                             "org.apache.excalibur.source.impl.URLSource" );
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if( loader == null )
        {
            loader = getClass().getClassLoader();
        }
        try
        {
            m_urlSourceClass = loader.loadClass( urlSourceClassName );
        }
        catch( ClassNotFoundException cnfe )
        {
            this.getLogger().error( "Class not found: " + urlSourceClassName, cnfe );
            throw new ParameterException( "Class not found: " + urlSourceClassName, cnfe );
        }
    }

    /**
     * @see org.apache.excalibur.source.SourceFactory#getSource(java.lang.String, java.util.Map)
     */
    public Source getSource(String systemID, Map parameters)
        throws MalformedURLException, IOException 
    {
        if( getLogger().isDebugEnabled() )
        {
            final String message = "Creating source object for " + systemID;
            getLogger().debug( message );
        }

        Source source;
        try
        {
            if( getLogger().isDebugEnabled() == true )
            {
                this.getLogger().debug( "Making URL from " + systemID );
            }
            try
            {
                final URLSource urlSource =
                    (URLSource)this.m_urlSourceClass.newInstance();
                urlSource.init( new URL( systemID ), parameters );
                source = urlSource;
            }
            catch( MalformedURLException mue )
            {
                throw mue;
            }
            catch( Exception ie )
            {
                throw new SourceException( "Unable to create new instance of " +
                                           this.m_urlSourceClass, ie );
            }
        }
        catch( MalformedURLException mue )
        {
            if( getLogger().isDebugEnabled() )
            {
                this.getLogger().debug( "Making URL - MalformedURLException in getURL:", mue );
                this.getLogger().debug( "Making URL a File (assuming that it is full path):" + systemID );
            }
            try
            {
                final URLSource urlSource =
                    (URLSource)this.m_urlSourceClass.newInstance();
                urlSource.init( ( new File( systemID ) ).toURL(), parameters );
                source = urlSource;
            }
            catch( Exception ie )
            {
                throw new SourceException( "Unable to create new instance of " +
                                           this.m_urlSourceClass, ie );
            }
        }

        return source;
    }

    /**
     * @see org.apache.excalibur.source.SourceFactory#release(org.apache.excalibur.source.Source)
     */
    public void release(Source source) {
        if( null != source && getLogger().isDebugEnabled() )
        {
            final String message = "Releasing source object for " + source.getURI();
            getLogger().debug( message );
        }
        // do nothing here
    }

}

