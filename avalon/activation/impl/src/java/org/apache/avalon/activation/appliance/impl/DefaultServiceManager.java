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
package org.apache.avalon.activation.appliance.impl;

import java.util.Map;
import java.util.Hashtable;

import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.Home;
import org.apache.avalon.composition.util.ExceptionHelper;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.meta.info.DependencyDescriptor;

/**
 * Default implementation of the framework {@link ServiceManager} interface.
 * @author Stephen McConnell <mcconnell@osm.net>
 */
class DefaultServiceManager extends AbstractLogEnabled implements ServiceManager
{
    //========================================================================
    // immutable state
    //========================================================================

    /**
     * A table of home instances keyed by lookup key.
     */
    private final Map m_map;

    /**
     * A table of identity hashcode integers of established objects 
     * that map to the lookup key that was uased to establish the object.
     * Used to track which appliance is providing an object when we 
     * handle release of objects.
     */
    private final Hashtable m_table = new Hashtable();

    //========================================================================
    // constructor
    //========================================================================

    /**
     * Construct a new ServiceManager.
     * @param appliance the appliance handling the component
     *   to be serviced
     */
    public DefaultServiceManager( Logger logger, Map map )
    {
        if( logger == null ) throw new NullPointerException( "logger" );
        if( map == null ) throw new NullPointerException( "map" );
        super.enableLogging( logger );
        m_map = map;
    }

    //========================================================================
    // ServiceManager
    //========================================================================

    /**
     * Returns true if a provider exists for the supplied role.
     * @param key the service identifier key
     * @return boolean TRUE if the service is available else FALSE
     */
    public boolean hasService( String key )
    {
        return ( m_map.get( key ) != null );
    }

    /**
     * Retrieve Object by key.
     * @param key the role
     * @return the Object
     * @throws ServiceException if an error occurs
     * @throws NullPointerException if the supplied key is null
     */
    public Object lookup( String key ) throws ServiceException
    {
        if( key == null )
        {
            throw new NullPointerException( "key" );
        }

        if( !hasService( key ) )
        {
            final String error = 
              "Unknown key: " + key;
            throw new ServiceException( key, error );
        }

        Home provider = (Home) m_map.get( key );
        try
        {
            Object object = provider.resolve( this );
            String id = "" + System.identityHashCode( object );
            m_table.put( id, key );
            final String message = 
              "resolved service [" + id + "] for the key [" + key + "].";
            getLogger().debug( message );
            return object;
        }
        catch( Throwable e )
        {
            //
            // TODO: framework states that ServiceException is thrown
            // if the service is not found - and in this case that isn't 
            // the issue - in effect we have a good key, but we simply
            // have not been able to go from key to instance -
            // should look into some more concrete subtypes of 
            // ServiceException

            final String error = 
              "Unexpected runtime error while attempting to resolve service for key: " + key;
            throw new ServiceException( error, e );
        }
    }

    /**
     * Release a service back to the manager.
     * @param object the object to release
     */
    public void release( Object object )
    {
        if( object == null ) return;

        String id = "" + System.identityHashCode( object );
        final String key = (String) m_table.get( id );
        if( key == null )
        {
            final String warning = 
              "Unrecognized object identity [" 
              + id 
              + "]. "
              + "Either this object was not provided by this service manager "
              + "or it has already been released.";
            getLogger().warn( warning );
            return;
        }

        final Home provider = (Home) m_map.get( key );
        if( provider == null )
        {
            final String error = 
              "Unable to release component as no provider could be found for the key ["
              + key
              + "].";
            throw new IllegalStateException( error );
        }

        try
        {
            provider.release( this, object );
            final String message = 
              "released service [" + id + "] from the key [" + key + "].";
            getLogger().debug( message );
        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while attempting to release object from provider: " + provider;
            final String warning = ExceptionHelper.packException( error, e, true );
            getLogger().warn( warning );
        }
        finally
        {
            m_table.remove( id );
        }
    }
}

