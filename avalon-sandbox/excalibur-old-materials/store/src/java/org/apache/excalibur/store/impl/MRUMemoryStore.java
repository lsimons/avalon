/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.store.impl;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.excalibur.store.Store;
import org.apache.excalibur.store.StoreJanitor;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * This class provides a cache algorithm for the requested documents.
 * It combines a HashMap and a LinkedList to create a so called MRU
 * (Most Recently Used) cache.
 *
 * @author <a href="mailto:g-froehlich@gmx.de">Gerhard Froehlich</a>
 * @author <a href="mailto:dims@yahoo.com">Davanum Srinivas</a>
 * @author <a href="mailto:vgritsenko@apache.org">Vadim Gritsenko</a>
 * @version CVS $Id: MRUMemoryStore.java,v 1.7 2002/11/07 04:58:39 donaldp Exp $
 */
public final class MRUMemoryStore
    extends AbstractLogEnabled
    implements Store, Parameterizable, Serviceable, Disposable, ThreadSafe
{
    private int m_maxobjects;
    private boolean m_persistent;
    private Hashtable m_cache;
    private LinkedList m_mrulist;
    private Store m_persistentStore;
    private StoreJanitor m_storeJanitor;
    private ServiceManager m_manager;

    /**
     * Get components of the ComponentLocator
     *
     * @param manager The ComponentLocator
     * @avalon.service interface="Store"
     * @avalon.service interface="StoreJanitor"
     */
    public void service( ServiceManager manager )
        throws ServiceException
    {
        m_manager = manager;
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Looking up " + Store.PERSISTENT_STORE );
            getLogger().debug( "Looking up " + StoreJanitor.ROLE );
        }
        m_persistentStore = (Store)manager.lookup( Store.PERSISTENT_STORE );
        m_storeJanitor = (StoreJanitor)manager.lookup( StoreJanitor.ROLE );
    }

    /**
     * Initialize the MRUMemoryStore.
     * A few options can be used:
     * <UL>
     *  <LI>maxobjects: Maximum number of objects stored in memory (Default: 100 objects)</LI>
     *  <LI>use-persistent-cache: Use persistent cache to keep objects persisted after
     *      container shutdown or not (Default: false)</LI>
     * </UL>
     *
     * @param params Store parameters
     * @exception ParameterException
     */
    public void parameterize( Parameters params ) throws ParameterException
    {
        m_maxobjects = params.getParameterAsInteger( "maxobjects", 100 );
        m_persistent = params.getParameterAsBoolean( "use-persistent-cache", false );
        if( ( m_maxobjects < 1 ) )
        {
            throw new ParameterException( "MRUMemoryStore maxobjects must be at least 1!" );
        }

        m_cache = new Hashtable( (int)( m_maxobjects * 1.2 ) );
        m_mrulist = new LinkedList();
        m_storeJanitor.register( this );
    }

    /**
     * Dispose the component
     */
    public void dispose()
    {
        if( m_manager != null )
        {
            getLogger().debug( "Disposing component!" );

            if( m_storeJanitor != null )
            {
                m_storeJanitor.unregister( this );
            }
            m_manager.release( m_storeJanitor );
            m_storeJanitor = null;

            // save all cache entries to filesystem
            if( m_persistent )
            {
                getLogger().debug( "Final cache size: " + m_cache.size() );
                Enumeration enum = m_cache.keys();
                while( enum.hasMoreElements() )
                {
                    Object key = enum.nextElement();
                    if( key == null )
                    {
                        continue;
                    }
                    try
                    {
                        Object value = m_cache.remove( key );
                        if( checkSerializable( value ) )
                        {
                            m_persistentStore.store( key, value );
                        }
                    }
                    catch( IOException ioe )
                    {
                        getLogger().error( "Error in dispose()", ioe );
                    }
                }
            }
            m_manager.release( m_persistentStore );
            m_persistentStore = null;
        }

        m_manager = null;
    }

    /**
     * Store the given object in a persistent state. It is up to the
     * caller to ensure that the key has a persistent state across
     * different JVM executions.
     *
     * @param key The key for the object to store
     * @param value The object to store
     */
    public synchronized void store( Object key, Object value )
    {
        hold( key, value );
    }

    /**
     * This method holds the requested object in a HashMap combined
     * with a LinkedList to create the MRU.
     * It also stores objects onto the filesystem if configured.
     *
     * @param key The key of the object to be stored
     * @param value The object to be stored
     */
    public synchronized void hold( Object key, Object value )
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Holding object in memory:" );
            getLogger().debug( "  key: " + key );
            getLogger().debug( "  value: " + value );
        }
        /** ...first test if the max. objects in cache is reached... */
        while( m_mrulist.size() >= m_maxobjects )
        {
            /** ...ok, heapsize is reached, remove the last element... */
            free();
        }
        /** ..put the new object in the cache, on the top of course ... */
        m_cache.put( key, value );
        m_mrulist.remove( key );
        m_mrulist.addFirst( key );
    }

    /**
     * Get the object associated to the given unique key.
     *
     * @param key The key of the requested object
     * @return the requested object
     */
    public synchronized Object get( Object key )
    {
        Object value = m_cache.get( key );
        if( value != null )
        {
            /** put the accessed key on top of the linked list */
            m_mrulist.remove( key );
            m_mrulist.addFirst( key );
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Found key: " + key.toString() );
            }
            return value;
        }

        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "NOT Found key: " + key.toString() );
        }

        /** try to fetch from filesystem */
        if( m_persistent )
        {
            value = m_persistentStore.get( key );
            if( value != null )
            {
                try
                {
                    if( !m_cache.containsKey( key ) )
                    {
                        hold( key, value );
                    }
                    return value;
                }
                catch( Exception e )
                {
                    getLogger().error( "Error in get()!", e );
                    return null;
                }
            }
        }

        return null;
    }

    /**
     * Remove the object associated to the given key.
     *
     * @param key The key of to be removed object
     */
    public synchronized void remove( Object key )
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Removing object from store" );
            getLogger().debug( "  key: " + key );
        }
        m_cache.remove( key );
        m_mrulist.remove( key );
        if( m_persistent && key != null )
        {
            m_persistentStore.remove( key );
        }
    }

    /**
     * Clear the Store of all elements
     */
    public synchronized void clear()
    {
        Enumeration enum = m_cache.keys();
        while( enum.hasMoreElements() )
        {
            Object key = enum.nextElement();
            if( key == null )
            {
                continue;
            }
            remove( key );
        }
    }

    /**
     * Indicates if the given key is associated to a contained object.
     *
     * @param key The key of the object
     * @return true if the key exists
     */
    public synchronized boolean containsKey( Object key )
    {
        if( m_persistent )
        {
            return ( m_cache.containsKey( key ) || m_persistentStore.containsKey( key ) );
        }
        else
        {
            return m_cache.containsKey( key );
        }
    }

    /**
     * Returns the list of used keys as an Enumeration.
     *
     * @return the enumeration of the cache
     */
    public synchronized Enumeration keys()
    {
        return m_cache.keys();
    }

    /**
     * Returns count of the objects in the store, or -1 if could not be
     * obtained.
     */
    public synchronized int size()
    {
        return m_cache.size();
    }

    /**
     * Frees some of the fast memory used by this store.
     * It removes the last element in the store.
     */
    public synchronized void free()
    {
        try
        {
            if( m_cache.size() > 0 )
            {
                // This can throw NoSuchElementException
                Object key = m_mrulist.removeLast();
                Object value = m_cache.remove( key );
                if( value == null )
                {
                    getLogger().warn( "Concurrency condition in free()" );
                }

                if( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "Freeing cache." );
                    getLogger().debug( "  key: " + key );
                    getLogger().debug( "  value: " + value );
                }

                if( m_persistent )
                {
                    // Swap object on fs.
                    if( checkSerializable( value ) )
                    {
                        try
                        {
                            m_persistentStore.store( key, value );
                        }
                        catch( Exception e )
                        {
                            getLogger().error( "Error storing object on fs", e );
                        }
                    }
                }
            }
        }
        catch( NoSuchElementException e )
        {
            getLogger().warn( "Concurrency error in free()", e );
        }
        catch( Exception e )
        {
            getLogger().error( "Error in free()", e );
        }
    }

    /**
     * This method checks if an object is seriazable.
     *
     * @param object The object to be checked
     * @return true if the object is storeable
     */
    private boolean checkSerializable( Object object )
    {

        if( object == null ) return false;

        return ( object instanceof java.io.Serializable );
    }
}

