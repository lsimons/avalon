/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:
 
 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
 
 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.
 
 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the 
 Apache Software Foundation, please see <http://www.apache.org/>.
 
*/
package org.apache.excalibur.store.impl;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import com.coyotegulch.jisp.BTreeIndex;
import com.coyotegulch.jisp.BTreeIterator;
import com.coyotegulch.jisp.IndexedObjectDatabase;
import com.coyotegulch.jisp.KeyNotFound;
import com.coyotegulch.jisp.KeyObject;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.store.Store;

import EDU.oswego.cs.dl.util.concurrent.FIFOReadWriteLock;
import EDU.oswego.cs.dl.util.concurrent.ReadWriteLock;
import EDU.oswego.cs.dl.util.concurrent.Sync;

/**
 * This store is based on the Jisp library
 * (http://www.coyotegulch.com/jisp/index.html). This store uses B-Tree indexes
 * to access variable-length serialized data stored in files.
 *
 * @author <a href="mailto:g-froehlich@gmx.de">Gerhard Froehlich</a>
 * @author <a href="mailto:vgritsenko@apache.org">Vadim Gritsenko</a>
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Id: AbstractJispFilesystemStore.java,v 1.17 2003/08/25 09:20:30 cziegeler Exp $
 */
public abstract class AbstractJispFilesystemStore
extends AbstractLogEnabled
implements Store, ThreadSafe {

    /** The directory repository */
    protected File m_directoryFile;

    /** The database  */   
    protected IndexedObjectDatabase m_Database;
    
    /** And the index */
    protected BTreeIndex m_Index;

    /** The lock */
    protected ReadWriteLock lock = new FIFOReadWriteLock();
    
    /**
     * Sets the repository's location
     */
    public void setDirectory(final File directory)
    throws IOException 
    {
        this.m_directoryFile = directory;

        /* Does directory exist? */
        if (!this.m_directoryFile.exists()) 
        {
            /* Create it anew */
            if (!this.m_directoryFile.mkdirs()) 
            {
                throw new IOException(
                "Error creating store directory '" + this.m_directoryFile.getAbsolutePath() + "'. ");
            }
        }

        /* Is given file actually a directory? */
        if (!this.m_directoryFile.isDirectory()) 
        {
            throw new IOException("'" + this.m_directoryFile.getAbsolutePath() + "' is not a directory");
        }

        /* Is directory readable and writable? */
        if (!(this.m_directoryFile.canRead() && this.m_directoryFile.canWrite())) 
        {
            throw new IOException(
                "Directory '" + this.m_directoryFile.getAbsolutePath() + "' is not readable/writable"
            );
        }
    }
   
    /**
     * Returns a Object from the store associated with the Key Object
     *
     * @param key the Key object
     * @return the Object associated with Key Object
     */
    public Object get(Object key) 
    {
        Object value = null;
        Sync sync = this.lock.writeLock();
        try
        {
            sync.acquire();
            try 
            {
                value = m_Database.read(this.wrapKeyObject(key), m_Index);
                if (getLogger().isDebugEnabled()) 
                {
                    if (value != null) 
                    {
                        getLogger().debug("Found key: " + key);
                    } 
                    else 
                    {
                        getLogger().debug("NOT Found key: " + key);
                    }
                }
            } 
            catch (Exception e) 
            {
                getLogger().error("get(..): Exception", e);
            }
            finally 
            {
                sync.release();
            }
        }
        catch (InterruptedException ignore)
        {
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
    public void store(Object key, Object value)
    throws IOException 
    {

        if (getLogger().isDebugEnabled()) 
        {
            getLogger().debug("store(): Store file with key: "
                              + key.toString());
            getLogger().debug("store(): Store file with value: "
                              + value.toString());
        }

        if (value instanceof Serializable) 
        {
            Sync sync = this.lock.writeLock();
            try
            {
                sync.acquire();

                try 
                {
                    KeyObject[] keyArray = new KeyObject[1];
                    keyArray[0] = this.wrapKeyObject(key);
                    m_Database.write(keyArray, (Serializable) value);
                } 
                catch (Exception e) 
                {
                    getLogger().error("store(..): Exception", e);
                }
                finally 
                {
                    sync.release();
                }
            }
            catch (InterruptedException ignore)
            {
            } 
        } 
        else 
        {
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
    public void hold(Object key, Object value)
    throws IOException 
    {
        // store is synced!
        this.store(key, value);
    }

    /**
     * Frees some values of the data file.<br>
     * TODO: implementation
     */
    public void free() 
    {
    }

    /**
     * Clear the Store of all elements
     */
    public void clear() 
    {
        
        if (getLogger().isDebugEnabled()) 
        {
            getLogger().debug("clear(): Clearing the database ");
        }

        Sync sync = this.lock.writeLock();
        try
        {
            sync.acquire();
            try 
            {
                final BTreeIterator iter = new BTreeIterator(m_Index);
                Object tmp;
                do 
                {
                    tmp = iter.getKey();
                    if ( tmp != null ) 
                    {
                        if (getLogger().isDebugEnabled()) 
                        {
                            getLogger().debug("clear(): Removing key: " + tmp.toString());
                        }
                        iter.moveNext();
                        this.remove( tmp );
                    }
                } 
                while (tmp != null);
            } 
            catch (Exception ignore) 
            {
                getLogger().error("store(..): Exception", ignore);
            }
            finally 
            {
                sync.release();
            }
        }
        catch (InterruptedException ignore)
        {
        } 
    }

    /**
     * Removes a value from the data file with the given key.
     *
     * @param key the key object
     */
    public void remove(Object key)
    {
        if (getLogger().isDebugEnabled()) 
        {
            getLogger().debug("remove(..) Remove item");
        }

        Sync sync = this.lock.writeLock();
        try
        {
            sync.acquire();
            try 
            {
                KeyObject[] keyArray = new KeyObject[1];
                keyArray[0] = this.wrapKeyObject(key);
                m_Database.remove(keyArray);
            } 
            catch (KeyNotFound ignore) 
            {
            } 
            catch (Exception e) 
            {
                getLogger().error("remove(..): Exception", e);
            }
            finally 
            {
                sync.release();
            }
        }
        catch (InterruptedException ignore)
        {
        } 
    }

    /**
     *  Test if the the index file contains the given key
     *
     * @param key the key object
     * @return true if Key exists and false if not
     */
    public boolean containsKey(Object key) 
    {
        long res = -1;

        Sync sync = this.lock.readLock();
        try
        {
            sync.acquire();
            try 
            {
                res = m_Index.findKey(this.wrapKeyObject(key));
                if (getLogger().isDebugEnabled()) 
                {
                    getLogger().debug("containsKey(..): res=" + res);
                }
            } 
            catch (KeyNotFound ignore) 
            {
            } 
            catch (Exception e)
            {
                getLogger().error("containsKey(..): Exception", e);
            }
            finally 
            {
                sync.release();
            }
        }
        catch (InterruptedException ignore)
        {
        } 

        if (res > 0) 
        {
            return true;
        } 
        else 
        {
            return false;
        }
    }

    /**
     * Returns a Enumeration of all Keys in the indexed file.<br>
     *
     * @return  Enumeration Object with all existing keys
     */
    public Enumeration keys() 
    {
        try 
        {
            return new BTreeObjectEnumeration(new BTreeIterator(m_Index), this);
        }
        catch (Exception ignore) 
        {
            return Collections.enumeration(Collections.EMPTY_LIST);
        }
    }

    public int size() 
    {
        return m_Index.count();
    }

    /**
     * This method wraps around the key Object a Jisp KeyObject.
     *
     * @param key the key object
     * @return the wrapped key object
     */
    protected KeyObject wrapKeyObject(Object key) 
    {
        return new JispKey( key );
    }

    /**
     * Return the Null JispKey
     */
    protected KeyObject getNullKey() 
    {
        return new JispKey().makeNullKey();
    }
    
    class BTreeObjectEnumeration implements Enumeration
    {
        private Object m_Next;
        private BTreeIterator m_Iterator;
        private AbstractJispFilesystemStore m_Store;

        public BTreeObjectEnumeration(BTreeIterator iterator, AbstractJispFilesystemStore store) 
        {
            m_Iterator = iterator;
            m_Store = store;

            // Obtain first element. If any.
            try
            {
                m_Next = m_Iterator.getKey();
            }
            catch (IOException ioe)
            {
                m_Store.getLogger().error("store(..): Exception", ioe);
                m_Next = null;
            }
        }

        public boolean hasMoreElements() 
        {
            return (m_Next != null);
        }

        public Object nextElement() throws NoSuchElementException
        {
            if (m_Next == null)
            {
                throw new NoSuchElementException();
            }

            // Save current element
            Object tmp = m_Next;

            // Advance to the next element
            try
            {
                if (m_Iterator.moveNext())
                {
                    m_Next = m_Iterator.getKey();
                }
                else
                {
                    // Can't move to the next element - no more elements.
                    m_Next = null;
                }
            }
            catch (IOException ioe) 
            {
                m_Store.getLogger().error("store(..): Exception", ioe);
                m_Next = null;
            }
            catch (ClassNotFoundException cnfe) 
            {
                m_Store.getLogger().error("store(..): Exception", cnfe);
                m_Next = null;
            }

            // Return the real key
            return ((JispKey) tmp).getKey();
        }
    }
}
