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

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

import EDU.oswego.cs.dl.util.concurrent.FIFOReadWriteLock;
import EDU.oswego.cs.dl.util.concurrent.ReadWriteLock;
import EDU.oswego.cs.dl.util.concurrent.Sync;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.instrument.CounterInstrument;
import org.apache.excalibur.instrument.Instrument;
import org.apache.excalibur.instrument.Instrumentable;
import org.apache.excalibur.instrument.ValueInstrument;
import org.apache.excalibur.store.Store;

/**
 * This is a base implementation for stores that are synchronized by
 * using a read/write lock.
 * 
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Id: AbstractReadWriteStore.java,v 1.2 2003/12/11 14:19:19 sylvain Exp $
 */
public abstract class AbstractReadWriteStore
extends AbstractLogEnabled
implements Store, ThreadSafe {

    private ValueInstrument m_sizeInstrument = new ValueInstrument("size");
    private CounterInstrument m_hitsInstrument = new CounterInstrument("hits");
    private CounterInstrument m_missesInstrument = new CounterInstrument("misses");

    private String m_instrumentableName;

    /** The lock */
    protected ReadWriteLock lock = new FIFOReadWriteLock();
    
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
                value = this.doGet(key);
            }
            finally 
            {
                sync.release();
            }
        }
        catch (InterruptedException ignore)
        {
        } 
        
        if ( null == value )
        {
            m_missesInstrument.increment();
        }
        else
        {
            m_hitsInstrument.increment();
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
        Sync sync = this.lock.writeLock();
        try
        {
            sync.acquire();

            try 
            {
                this.doStore(key, value);
                m_sizeInstrument.setValue( doGetSize() );
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
     * Frees some values of the data file.<br>
     */
    public void free() 
    {
        Sync sync = this.lock.writeLock();
        try
        {
            sync.acquire();

            try 
            {
                this.doFree();
                m_sizeInstrument.setValue( doGetSize() );
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
                this.doClear();
                m_sizeInstrument.setValue( 0 );
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
        Sync sync = this.lock.writeLock();
        try
        {
            sync.acquire();
            try 
            {
                this.doRemove(key);
                m_sizeInstrument.setValue( doGetSize() );
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
        Sync sync = this.lock.readLock();
        try
        {
            sync.acquire();
            try 
            {
                return this.doContainsKey(key);
            }
            finally 
            {
                sync.release();
            }
        }
        catch (InterruptedException ignore)
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
        Sync sync = this.lock.readLock();
        try
        {
            sync.acquire();
            try 
            {
                return this.doGetKeys();
            }
            finally 
            {
                sync.release();
            }
        }
        catch (InterruptedException ignore)
        {
            return Collections.enumeration(Collections.EMPTY_LIST);
        } 
    }

    public int size() 
    {
        Sync sync = this.lock.readLock();
        try
        {
            sync.acquire();
            try 
            {
                return this.doGetSize();
            }
            finally 
            {
                sync.release();
            }
        }
        catch (InterruptedException ignore)
        {
            return 0;
        } 
    }

    public void setInstrumentableName(String name)
    {
        m_instrumentableName = name;    
    }

    public String getInstrumentableName()
    {
        return m_instrumentableName;
    }

    public Instrument[] getInstruments()
    {
        return new Instrument[] { m_sizeInstrument, m_hitsInstrument, m_missesInstrument };
    }

    public Instrumentable[] getChildInstrumentables() {
        return Instrumentable.EMPTY_INSTRUMENTABLE_ARRAY;
    }

    /**
     * Get the object associated to the given unique key.
     */
    protected abstract Object doGet( Object key );

    /**
     * Store the given object. It is up to the
     * caller to ensure that the key has a persistent state across
     * different JVM executions.
     */
    protected abstract void doStore( Object key, Object value ) throws IOException;

    /**
     * Try to free some used memory. The transient store can simply remove
     * some hold data, the persistent store can free all memory by
     * writing the data to a persistent store etc.
     */
    protected abstract void doFree();

    /**
     * Remove the object associated to the given key.
     */
    protected abstract void doRemove( Object key );

    /**
     * Clear the Store of all data it holds 
     */
    protected abstract void doClear();

    /**
     * Indicates if the given key is associated to a contained object.
     */
    protected abstract boolean doContainsKey( Object key );

    /**
     * Returns the list of used keys as an Enumeration of Objects.
     */
    protected abstract Enumeration doGetKeys();

    /**
     * Returns count of the objects in the store, or -1 if could not be
     * obtained.
     */
    protected abstract int doGetSize();
}
