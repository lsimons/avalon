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

import org.apache.avalon.framework.CascadingRuntimeException;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.source.*;

/**
 * This is the default implemenation of a {@link SourceResolver}.
 *
 * The source resolving is done relative to a base directory/URI (if
 * the given location is relative). This implementation looks for the
 * base URI in the {@link Context} object of the "container" for the
 * "context-root" information. This information can either be a
 * {@link File} object or a {@link URL} object.
 * If the entry does not exist, the system property "user.dir" is used
 * as the base URI instead.
 *
 * @see org.apache.excalibur.source.SourceResolver
 *
 * @avalon.component
 * @avalon.service type=SourceResolver
 * @x-avalon.info name=resolver
 * @x-avalon.lifestyle type=singleton
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version $Id: SourceResolverImpl.java,v 1.1 2003/11/09 12:46:57 leosimons Exp $
 */
public class SourceResolverImpl
    extends AbstractLogEnabled
    implements Serviceable,
    Contextualizable,
    Disposable,
    SourceResolver,
    ThreadSafe
{
    /** The component m_manager */
    protected ServiceManager m_manager;

    /** The special Source factories */
    protected ServiceSelector m_factorySelector;

    /**
     * The base URL
     */
    protected URL m_baseURL;

    /**
     * Get the context
     */
    public void contextualize( Context context )
        throws ContextException
    {
        try
        {
            if( context.get( "context-root" ) instanceof URL )
            {
                m_baseURL = (URL)context.get( "context-root" );
            }
            else
            {
                m_baseURL = ( (File)context.get( "context-root" ) ).toURL();
            }
        }
        catch( ContextException ce )
        {
            // set the base URL to the current directory
            try
            {
                m_baseURL = new File( System.getProperty( "user.dir" ) ).toURL();
                if( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "SourceResolver: Using base URL: " + m_baseURL );
                }
            }
            catch( MalformedURLException mue )
            {
                getLogger().warn( "Malformed URL for user.dir, and no container.rootDir exists", mue );
                throw new ContextException( "Malformed URL for user.dir, and no container.rootDir exists", mue );
            }
        }
        catch( MalformedURLException mue )
        {
            getLogger().warn( "Malformed URL for container.rootDir", mue );
            throw new ContextException( "Malformed URL for container.rootDir", mue );
        }
    }

    /**
     * Set the current <code>ComponentLocator</code> instance used by this
     * <code>Composable</code>.
     *
     * @avalon.dependency type="org.apache.excalibur.source.SourceFactory"
     */
    public void service( final ServiceManager manager )
        throws ServiceException
    {
        m_manager = manager;

        if ( m_manager.hasService( SourceFactory.ROLE + "Selector" ) )
        {
            m_factorySelector = (ServiceSelector) m_manager.lookup( SourceFactory.ROLE + "Selector" );
        }
    }

    public void dispose()
    {
        if( null != m_manager )
        {
            m_manager.release( m_factorySelector );
            m_factorySelector = null;
        }
    }

    /**
     * Get a <code>Source</code> object.
     * @throws org.apache.excalibur.source.SourceNotFoundException if the source cannot be found
     */
    public Source resolveURI( String location )
        throws MalformedURLException, IOException, SourceException
    {
        return this.resolveURI( location, null, null );
    }

    /**
     * Get a <code>Source</code> object.
     * @throws org.apache.excalibur.source.SourceNotFoundException if the source cannot be found
     */
    public Source resolveURI( String location,
                              String baseURI,
                              Map parameters )
        throws MalformedURLException, IOException, SourceException
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Resolving '" + location + "' with base '" + baseURI + "' in context '" + m_baseURL + "'" );
        }
        if( location == null ) throw new MalformedURLException( "Invalid System ID" );
        if( null != baseURI && SourceUtil.indexOfSchemeColon(baseURI) == -1 )
        {
            throw new MalformedURLException( "BaseURI is not valid, it must contain a protocol: " + baseURI );
        }

        if( baseURI == null ) baseURI = m_baseURL.toExternalForm();

        String systemID = location;
        // special handling for windows file paths
        if( location.length() > 1 && location.charAt( 1 ) == ':' )
            systemID = "file:/" + location;
        else if( location.length() > 2 && location.charAt(0) == '/' && location.charAt(2) == ':' )
            systemID = "file:" + location;

        // determine protocol (scheme): first try to get the one of the systemID, if that fails, take the one of the baseURI
        String protocol;
        int protocolPos = SourceUtil.indexOfSchemeColon(systemID);
        if( protocolPos != -1 )
        {
            protocol = systemID.substring( 0, protocolPos );
        }
        else
        {
            protocolPos = SourceUtil.indexOfSchemeColon(baseURI);
            if( protocolPos != -1 )
                protocol = baseURI.substring( 0, protocolPos );
            else
                protocol = "*";
        }

        Source source = null;
        // search for a SourceFactory implementing the protocol
        SourceFactory factory = null;
        try
        {
            factory = (SourceFactory)m_factorySelector.select( protocol );
            systemID = absolutize( factory, baseURI, systemID );
            if( getLogger().isDebugEnabled() )
                getLogger().debug( "Resolved to systemID : " + systemID );
            source = factory.getSource( systemID, parameters );
        }
        catch( final ServiceException ce )
        {
            // no selector available, use fallback
        }
        finally
        {
            m_factorySelector.release( factory );
        }

        if( null == source )
        {
            try
            {
                factory = (SourceFactory) m_factorySelector.select("*");
                systemID = absolutize( factory, baseURI, systemID );
                if( getLogger().isDebugEnabled() )
                    getLogger().debug( "Resolved to systemID : " + systemID );
                source = factory.getSource( systemID, parameters );
            }
            catch (ServiceException se )
            {
                throw new SourceException( "Unable to select source factory for " + systemID, se );
            }
            finally
            {
                m_factorySelector.release(factory);
            }
        }

        return source;
    }

    /**
     * Makes an absolute URI based on a baseURI and a relative URI.
     */
    private String absolutize( SourceFactory factory, String baseURI, String systemID )
    {
        if( factory instanceof URIAbsolutizer )
            systemID = ((URIAbsolutizer)factory).absolutize(baseURI, systemID);
        else
            systemID = SourceUtil.absolutize(baseURI, systemID);
        return systemID;
    }

    /**
     * Releases a resolved resource
     * @param source the source to release
     */
    public void release( final Source source )
    {
        if( source == null ) return;

        // search for a SourceFactory implementing the protocol
        final String scheme = source.getScheme();
        SourceFactory factory = null;

        try
        {
            factory = (SourceFactory) m_factorySelector.select(scheme);
            factory.release(source);
        }
        catch (ServiceException se )
        {
            try
            {
                factory = (SourceFactory) m_factorySelector.select("*");
                factory.release(source);
            }
            catch (ServiceException sse )
            {
                throw new CascadingRuntimeException( "Unable to select source factory for " + source.getURI(), se );
            }
        }
        finally
        {
            m_factorySelector.release( factory );
        }
    }
}
