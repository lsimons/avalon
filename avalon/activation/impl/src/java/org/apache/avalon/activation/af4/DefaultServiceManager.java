/* 
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avalon.activation.af4;

import java.util.Map;
import java.util.Hashtable;

import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.Home;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;

import org.apache.avalon.util.exception.ExceptionHelper;

/**
 * Default implementation of the framework {@link ServiceManager} interface.
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @author Stephen McConnell <mcconnell@osm.net>
 */
public class DefaultServiceManager extends AbstractLogEnabled 
    implements ServiceManager
{
    //========================================================================
    // immutable state
    //========================================================================

    /**
     * A table of home instances keyed by lookup key.
     */
    private final Map m_applianceMap;

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
    public DefaultServiceManager( Logger logger, Map applianceMap )
    {
        if( logger == null ) 
        {
            throw new NullPointerException( "logger" );
        }
        if( applianceMap == null )
        {
            throw new NullPointerException( "map" );
        }
        super.enableLogging( logger );
        m_applianceMap = applianceMap;
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
        return ( m_applianceMap.get( key ) != null );
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

        Appliance provider = (Appliance) m_applianceMap.get( key );
        try
        {
            Object object = provider.resolve();
            String id = "" + System.identityHashCode( object );
            m_table.put( id, key );
            if( getLogger().isDebugEnabled() )
            {
                final String message = 
                  "resolved service [" + id + "] for the key [" + key + "].";
                getLogger().debug( message );
            }
            return object;
        }
        catch( Exception e )
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
            throw new ServiceException( key, error, e );
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

        final Home provider = (Home) m_applianceMap.get( key );
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
            provider.release( object );
            final String message = 
              "released service [" + id + "] from the key [" + key + "].";
            getLogger().debug( message );
        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while attempting to release object from provider: " + provider;
            final String warning = 
              ExceptionHelper.packException( error, e, true );
            getLogger().warn( warning );
        }
        finally
        {
            m_table.remove( id );
        }
    }
}

