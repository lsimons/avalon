/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.store.impl;


import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.thread.ThreadSafe;
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
 * @version CVS $Id: MRUMemoryStore.java,v 1.6 2002/08/14 15:33:53 crafterm Exp $
 */
public final class MRUMemoryStore
extends AbstractLogEnabled
implements Store, Parameterizable, Composable, Disposable, ThreadSafe {

    private int maxobjects;
    private boolean persistent;
    private Hashtable cache;
    private LinkedList mrulist;
    private Store persistentStore;
    private StoreJanitor storeJanitor;
    private ComponentManager manager;

    /**
     * Get components of the ComponentLocator
     *
     * @param manager The ComponentLocator
     */
    public void compose(ComponentManager manager)
    throws ComponentException {
        this.manager = manager;
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Looking up " + Store.PERSISTENT_STORE);
            getLogger().debug("Looking up " + StoreJanitor.ROLE);
        }
        this.persistentStore = (Store)manager.lookup(Store.PERSISTENT_STORE);
        this.storeJanitor = (StoreJanitor)manager.lookup(StoreJanitor.ROLE);
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
    public void parameterize(Parameters params) throws ParameterException {
        this.maxobjects = params.getParameterAsInteger("maxobjects", 100);
        this.persistent = params.getParameterAsBoolean("use-persistent-cache", false);
        if ((this.maxobjects < 1)) {
            throw new ParameterException("MRUMemoryStore maxobjects must be at least 1!");
        }

        this.cache = new Hashtable((int)(this.maxobjects * 1.2));
        this.mrulist = new LinkedList();
        this.storeJanitor.register(this);
    }

    /**
     * Dispose the component
     */
    public void dispose() {
        if (this.manager != null) {
            getLogger().debug("Disposing component!");

            if (this.storeJanitor != null)
                this.storeJanitor.unregister(this);
            this.manager.release(this.storeJanitor);
            this.storeJanitor = null;

            // save all cache entries to filesystem
            if (this.persistent) {
                getLogger().debug("Final cache size: " + this.cache.size());
                Enumeration enum = this.cache.keys();
                while (enum.hasMoreElements()) {
                    Object key = enum.nextElement();
                    if (key == null) {
                        continue;
                    }
                    try {
                        Object value = this.cache.remove(key);
                        if(checkSerializable(value)) {
                             persistentStore.store(key, value);
                        }
                    } catch (IOException ioe) {
                        getLogger().error("Error in dispose()", ioe);
                    }
                }
            }
            this.manager.release(this.persistentStore);
            this.persistentStore = null;
        }

        this.manager = null;
    }

    /**
     * Store the given object in a persistent state. It is up to the
     * caller to ensure that the key has a persistent state across
     * different JVM executions.
     *
     * @param key The key for the object to store
     * @param value The object to store
     */
    public synchronized void store(Object key, Object value) {
        this.hold(key,value);
    }

    /**
     * This method holds the requested object in a HashMap combined
     * with a LinkedList to create the MRU.
     * It also stores objects onto the filesystem if configured.
     *
     * @param key The key of the object to be stored
     * @param value The object to be stored
     */
    public synchronized void hold(Object key, Object value) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Holding object in memory:");
            getLogger().debug("  key: " + key);
            getLogger().debug("  value: " + value);
        }
        /** ...first test if the max. objects in cache is reached... */
        while (this.mrulist.size() >= this.maxobjects) {
            /** ...ok, heapsize is reached, remove the last element... */
            this.free();
        }
        /** ..put the new object in the cache, on the top of course ... */
        this.cache.put(key, value);
        this.mrulist.remove(key);
        this.mrulist.addFirst(key);
    }

    /**
     * Get the object associated to the given unique key.
     *
     * @param key The key of the requested object
     * @return the requested object
     */
    public synchronized Object get(Object key) {
        Object value = this.cache.get(key);
        if (value != null) {
            /** put the accessed key on top of the linked list */
            this.mrulist.remove(key);
            this.mrulist.addFirst(key);
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Found key: " + key.toString());
            }
            return value;
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("NOT Found key: " + key.toString());
        }

        /** try to fetch from filesystem */
        if (this.persistent) {
            value = this.persistentStore.get(key);
            if (value != null) {
                try {
                    if(!this.cache.containsKey(key)) {
                        this.hold(key, value);
                    }
                    return value;
                } catch (Exception e) {
                    getLogger().error("Error in get()!", e);
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
    public synchronized void remove(Object key) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Removing object from store");
            getLogger().debug("  key: " + key);
        }
        this.cache.remove(key);
        this.mrulist.remove(key);
        if(this.persistent && key != null) {
            this.persistentStore.remove(key);
        }
    }

    /**
     * Clear the Store of all elements 
     */
    public synchronized void clear() {
                Enumeration enum = this.cache.keys();
                while (enum.hasMoreElements()) {
                    Object key = enum.nextElement();
                    if (key == null) {
                        continue;
                    }
                        this.remove(key);
                 }
    }

    /**
     * Indicates if the given key is associated to a contained object.
     *
     * @param key The key of the object
     * @return true if the key exists
     */
    public synchronized boolean containsKey(Object key) {
        if(persistent) {
            return (cache.containsKey(key) || persistentStore.containsKey(key));
        } else {
            return cache.containsKey(key);
        }
    }

    /**
     * Returns the list of used keys as an Enumeration.
     *
     * @return the enumeration of the cache
     */
    public synchronized Enumeration keys() {
        return this.cache.keys();
    }

    /**
     * Returns count of the objects in the store, or -1 if could not be
     * obtained.
     */
    public synchronized int size() {
        return this.cache.size();
    }

    /**
     * Frees some of the fast memory used by this store.
     * It removes the last element in the store.
     */
    public synchronized void free() {
        try {
            if (this.cache.size() > 0) {
                // This can throw NoSuchElementException
                Object key = this.mrulist.removeLast();
                Object value = this.cache.remove(key);
                if (value == null) {
                    getLogger().warn("Concurrency condition in free()");
                }

                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Freeing cache.");
                    getLogger().debug("  key: " + key);
                    getLogger().debug("  value: " + value);
                }

                if (this.persistent) {
                    // Swap object on fs.
                    if(checkSerializable(value)) {
                        try {
                            this.persistentStore.store(key, value);
                        } catch(Exception e) {
                            getLogger().error("Error storing object on fs", e);
                        }
                    }
                }
            }
        } catch (NoSuchElementException e) {
            getLogger().warn("Concurrency error in free()", e);
        } catch (Exception e) {
            getLogger().error("Error in free()", e);
        }
    }

    /**
     * This method checks if an object is seriazable.
     *
     * @param object The object to be checked
     * @return true if the object is storeable
     */
    private boolean checkSerializable(Object object) {

        if (object == null) return false;

        return (object instanceof java.io.Serializable);
    }
}

