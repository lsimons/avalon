/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.store.impl;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.store.Store;

import com.coyotegulch.jisp.BTreeIndex;
import com.coyotegulch.jisp.BTreeObjectIterator;
import com.coyotegulch.jisp.IndexedObjectDatabase;
import com.coyotegulch.jisp.KeyNotFound;
import com.coyotegulch.jisp.KeyObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

/**
 * This store is based on the Jisp library
 * (http://www.coyotegulch.com/jisp/index.html). This store uses B-Tree indexes
 * to access variable-length serialized data stored in files.
 *
 * @author <a href="mailto:g-froehlich@gmx.de">Gerhard Froehlich</a>
 * @author <a href="mailto:vgritsenko@apache.org">Vadim Gritsenko</a>
 * @version CVS $Id: AbstractJispFilesystemStore.java,v 1.1 2002/10/12 10:36:55 froehlich Exp $
 */
public abstract class AbstractJispFilesystemStore
extends AbstractLogEnabled
implements Store, ThreadSafe, Initializable {

    /**    
     *  The directory repository
     */
    protected File m_directoryFile;
    protected volatile String m_directoryPath;

    /**
     * The database
     */   
    protected IndexedObjectDatabase m_Database;
    protected BTreeIndex m_Index;

    /**
     * Sets the repository's location
     */
    public void setDirectory(final String directory)
    throws IOException {
        this.setDirectory(new File(directory));
    }

    /**
     * Sets the repository's location
     */
    public void setDirectory(final File directory)
    throws IOException {
        this.m_directoryFile = directory;

        /* Save directory path prefix */
        this.m_directoryPath = this.getFullFilename(this.m_directoryFile);
        this.m_directoryPath += File.separator;

        /* Does directory exist? */
        if (!this.m_directoryFile.exists()) {
            /* Create it anew */
            if (!this.m_directoryFile.mkdir()) {
                throw new IOException(
                "Error creating store directory '" + this.m_directoryPath + "': ");
            }
        }

        /* Is given file actually a directory? */
        if (!this.m_directoryFile.isDirectory()) {
            throw new IOException("'" + this.m_directoryPath + "' is not a directory");
        }

        /* Is directory readable and writable? */
        if (!(this.m_directoryFile.canRead() && this.m_directoryFile.canWrite())) {
            throw new IOException(
                "Directory '" + this.m_directoryPath + "' is not readable/writable"
            );
        }
    }
   
    /**
     * Returns the repository's full pathname
     *
     * @return the directory as String
     */
    public String getDirectoryPath() {
        return this.m_directoryPath;
    }

    /**
     * Returns a Object from the store associated with the Key Object
     *
     * @param key the Key object
     * @return the Object associated with Key Object
     */
    public synchronized Object get(Object key) {
        Object value = null;
        try {
            value = m_Database.read(this.wrapKeyObject(key), m_Index);
            if (getLogger().isDebugEnabled()) {
                if (value != null) {
                    getLogger().debug("Found key: " + key);
                } else {
                    getLogger().debug("NOT Found key: " + key);
                }
            }
        } catch (Exception e) {
            getLogger().error("get(..): Exception", e);
        }
        return value;
    }

    /**
     *  Store the given object in the indexed data file.
     *
     * @param key the key object
     * @param value the value object
     * @exception  IOException
     */
    public synchronized void store(Object key, Object value)
        throws IOException {

        if (getLogger().isDebugEnabled()) {
            this.getLogger().debug("store(): Store file with key: "
                                  + key.toString());
            this.getLogger().debug("store(): Store file with value: "
                                  + value.toString());
        }

        if (value instanceof Serializable) {
            try {
                KeyObject[] keyArray = new KeyObject[1];
                keyArray[0] = this.wrapKeyObject(key);
                m_Database.write(keyArray, (Serializable) value);
            } catch (Exception e) {
                this.getLogger().error("store(..): Exception", e);
            }
        } else {
            throw new IOException("Object not Serializable");
        }
    }

    /**
     *  Holds the given object in the indexed data file.
     *
     * @param key the key object
     * @param value the value object
     * @exception IOException
     */
    public synchronized void hold(Object key, Object value)
        throws IOException {
        this.store(key, value);
    }

    /**
     * Frees some values of the data file.<br>
     * TODO: implementation
     */
    public synchronized void free() {
       //TODO: implementation
    }

    /**
     * Clear the Store of all elements
     */
    public synchronized void clear() {
        BTreeObjectEnumeration enum = new BTreeObjectEnumeration(m_Database.createIterator(m_Index),this);

        if (getLogger().isDebugEnabled()) {
            this.getLogger().debug("clear(): Clearing the database ");
        }
        
        while(enum.hasMoreElements()) {
            Object tmp = enum.nextElement();
            if (getLogger().isDebugEnabled()) {
                this.getLogger().debug("clear(): Removing key: " + tmp.toString());
            }
            this.remove(tmp);
        }
    }

    /**
     * Removes a value from the data file with the given key.
     *
     * @param key the key object
     */
    public synchronized void remove(Object key) {
        if (getLogger().isDebugEnabled()) {
            this.getLogger().debug("remove(..) Remove item");
        }

        try {
            KeyObject[] keyArray = new KeyObject[1];
            keyArray[0] = this.wrapKeyObject(key);
            m_Database.remove(keyArray);
        } catch (KeyNotFound ignore) {
        } catch (Exception e) {
            this.getLogger().error("remove(..): Exception", e);
        }
    }

    /**
     *  Test if the the index file contains the given key
     *
     * @param key the key object
     * @return true if Key exists and false if not
     */
    public synchronized boolean containsKey(Object key) {
        long res = -1;

        try {
            res = m_Index.findKey(this.wrapKeyObject(key));
            if (getLogger().isDebugEnabled()) {
                this.getLogger().debug("containsKey(..): res=" + res);
            }
        } catch (KeyNotFound ignore) {
        } catch (Exception e) {
            this.getLogger().error("containsKey(..): Exception", e);
        }

        if (res > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns a Enumeration of all Keys in the indexed file.<br>
     *
     * @return  Enumeration Object with all existing keys
     */
    public Enumeration keys() {
        BTreeObjectEnumeration enum = new BTreeObjectEnumeration(m_Database.createIterator(m_Index),this);
        return enum;
    }

    public int size() {
        int cnt = 0;

        BTreeObjectEnumeration enum = new BTreeObjectEnumeration(m_Database.createIterator(m_Index),this);

        while(enum.hasMoreElements()) {
            cnt++;
        }
        return cnt;
    }

    /**
     * This method wraps around the key Object a Jisp KeyObject.
     *
     * @param key the key object
     * @return the wrapped key object
     */
    private KeyObject wrapKeyObject(Object key) {
        // TODO: Implementation of Integer and Long keys
        String skey = String.valueOf(key);
        return new JispStringKey(key.toString());
    }

    /**
     * Get the complete filename corresponding to a (typically relative)
     * <code>File</code>.
     * This method accounts for the possibility of an error in getting
     * the filename's <i>canonical</i> path, returning the io/error-safe
     * <i>absolute</i> form instead
     *
     * @param file The file
     * @return The file's absolute filename
     */
    public String getFullFilename(File file)
    {
        try
        {
            return file.getCanonicalPath();
        }
        catch (Exception e)
        {
            return file.getAbsolutePath();
        }
    }

    class BTreeObjectEnumeration implements Enumeration {
        private BTreeObjectIterator m_Iterator;
        private AbstractJispFilesystemStore m_Store;

        public BTreeObjectEnumeration(BTreeObjectIterator iterator, AbstractJispFilesystemStore store) {
            m_Iterator = iterator;
            m_Store = store;
        }

        public boolean hasMoreElements() {
            boolean hasMore = false;
            Object tmp = null;

            try {
                tmp = m_Iterator.getKey();

                if(m_Iterator.moveNext()) {
                    hasMore = true;
                }
    
                /* resets iterator to the old state **/
                m_Iterator.moveTo((KeyObject)tmp);
            } catch (IOException ioe) {
                m_Store.getLogger().error("store(..): Exception", ioe);
            } catch (ClassNotFoundException cnfe) {
                m_Store.getLogger().error("store(..): Exception", cnfe);
            }
            return hasMore;
        }

        public Object nextElement() {
            Object tmp = null;

            try {
                tmp = m_Iterator.getKey();
                m_Iterator.moveNext();
            } catch (IOException ioe) {
                m_Store.getLogger().error("store(..): Exception", ioe);
            } catch (ClassNotFoundException cnfe) {
                m_Store.getLogger().error("store(..): Exception", cnfe);
            }
            // make a string out of it (JispStringKey is not usefull here)
            return tmp.toString();
        }
    }
}