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

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.store.Store;
import org.apache.excalibur.store.StoreJanitor;

/**
 * This class is a implentation of a StoreJanitor. Store classes
 * can register to the StoreJanitor. When memory is too low,
 * the StoreJanitor frees the registered caches until memory is normal.
 * 
 * @avalon.component
 * @avalon.service type=StoreJanitor
 * @x-avalon.info name=store-janitor
 * @x-avalon.lifestyle type=singleton
 *
 * @author <a href="mailto:cs@ffzj0ia9.bank.dresdner.net">Christian Schmitt</a>
 * @author <a href="mailto:g-froehlich@gmx.de">Gerhard Froehlich</a>
 * @author <a href="mailto:proyal@managingpartners.com">Peter Royal</a>
 * @version CVS $Id: StoreJanitorImpl.java,v 1.9 2003/07/29 04:43:14 vgritsenko Exp $
 */
public class StoreJanitorImpl
extends AbstractLogEnabled
implements StoreJanitor,
           Parameterizable,
           ThreadSafe,
           Runnable,
           Startable {

    private static boolean doRun = false;

    private int freememory = -1;
    private int heapsize = -1;
    private int cleanupthreadinterval = -1;
    private int priority = -1;
    private Runtime jvm;
    private ArrayList storelist;
    private int index = -1;
    private double fraction;

    /**
     * Initialize the StoreJanitorImpl.
     * A few options can be used :
     * <UL>
     *  <LI>freememory = how many bytes shall be always free in the jvm</LI>
     *  <LI>heapsize = max. size of jvm memory consumption</LI>
     *  <LI>cleanupthreadinterval = how often (sec) shall run the cleanup thread</LI>
     *  <LI>threadpriority = priority of the thread (1-10). (Default: 10)</LI>
     * </UL>
     *
     * @param params the Configuration of the application
     * @exception ConfigurationException
     */
    public void parameterize(Parameters params) throws ParameterException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Configure StoreJanitorImpl");
        }
        setJVM(Runtime.getRuntime());

        setFreememory(params.getParameterAsInteger("freememory",1000000));
        setHeapsize(params.getParameterAsInteger("heapsize",60000000));
        setCleanupthreadinterval(params.getParameterAsInteger("cleanupthreadinterval", 10));
        setPriority(params.getParameterAsInteger("threadpriority",
                                                 Thread.currentThread().getPriority()));
        int percent = params.getParameterAsInteger("percent_to_free", 10);

        if (getFreememory() < 1) {
            throw new ParameterException("StoreJanitorImpl freememory parameter has to be greater then 1");
        }
        if (getHeapsize() < 1) {
            throw new ParameterException("StoreJanitorImpl heapsize parameter has to be greater then 1");
        }
        if (getCleanupthreadinterval() < 1) {
            throw new ParameterException("StoreJanitorImpl cleanupthreadinterval parameter has to be greater then 1");
        }
        if (getPriority() < 1) {
            throw new ParameterException("StoreJanitorImpl threadpriority has to be greater then 1");
        }
        if (percent > 100 && percent < 1) {
            throw new ParameterException("StoreJanitorImpl percent_to_free, has to be between 1 and 100");
        }

        this.fraction = percent / 100.0;
        setStoreList(new ArrayList());
    }

    public void start() {
        doRun = true;
        Thread checker = new Thread(this);
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Intializing checker thread");
        }
        checker.setPriority(getPriority());
        checker.setDaemon(true);
        checker.setName("checker");
        checker.start();
    }

    public void stop() {
        doRun = false;
    }

    /**
     * The "checker" thread checks if memory is running low in the jvm.
     */
    public void run() {
        while (doRun) {
            // amount of memory used is greater then heapsize
            if (memoryLow()) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Invoking garbage collection, total memory = "
                                      + getJVM().totalMemory() + ", free memory = "
                                      + getJVM().freeMemory());
                }

                //this.freePhysicalMemory();

                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Garbage collection complete, total memory = "
                                      + getJVM().totalMemory() + ", free memory = "
                                      + getJVM().freeMemory());
                }

                synchronized (this) {
                    if (memoryLow() && getStoreList().size() > 0) {
                        freeMemory();
                        setIndex(getIndex() + 1);
                    }
                }
            }

            try {
                Thread.sleep(this.cleanupthreadinterval * 1000);
            } catch (InterruptedException ignore) {}
        }
    }

    /**
     * Method to check if memory is running low in the JVM.
     *
     * @return true if memory is low
     */
    private boolean memoryLow() {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("JVM total Memory: " + getJVM().totalMemory());
            getLogger().debug("JVM free Memory: " + getJVM().freeMemory());
        }

        if ((getJVM().totalMemory() > getHeapsize())
            && (getJVM().freeMemory() < getFreememory())) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Memory is low = true");
            }
            return true;
        } else {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Memory is low = false");
            }
            return false;
        }
    }

    /**
     * This method register the stores
     *
     * @param store the store to be registered
     */
    public synchronized void register(Store store) {
        getStoreList().add(store);
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Registering store instance");
            getLogger().debug("Size of StoreJanitor now:"
                              + getStoreList().size());
        }
    }

    /**
     * This method unregister the stores
     *
     * @param store the store to be unregistered
     */
    public synchronized void unregister(Store store) {
        getStoreList().remove(store);
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Unregister store instance");
            getLogger().debug("Size of StoreJanitor now:"
                              + getStoreList().size());
        }
    }

    /**
     * This method return a java.util.Iterator of every registered stores
     *
     * <i>The iterators returned is fail-fast: if list is structurally
     * modified at any time after the iterator is created, in any way, the
     * iterator will throw a ConcurrentModificationException.  Thus, in the
     * face of concurrent modification, the iterator fails quickly and
     * cleanly, rather than risking arbitrary, non-deterministic behavior at
     * an undetermined time in the future.</i>
     *
     * @return a java.util.Iterator
     */
    public Iterator iterator() {
        return getStoreList().iterator();
     }

    /**
     * Round Robin alghorithm for freeing the registered caches.
     */
    private void freeMemory() {
        try {
            //Determine elements in Store:
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("StoreList size=" + getStoreList().size());
                getLogger().debug("Actual Index position: " + getIndex());
            }
            if (getIndex() < getStoreList().size()) {
                if (getIndex() == -1) {
                    setIndex(0);
                }
            } else {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Restarting from the beginning");
                }
                setIndex(0);
            }

            Store store = (Store)getStoreList().get(getIndex());
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Freeing Store: " + getIndex());
            }

            //delete proportionate elements out of the cache as
            //configured.
            int limit = calcToFree(store);
            for (int i=0; i < limit; i++) {
                store.free();
            }
        } catch (Exception e) {
            getLogger().error("Error in freeMemory()", e);
        }
    }

    /**
     * This method claculates the number of Elements to be freememory
     * out of the Cache.
     *
     * @param store the Store which was selected as victim
     * @return number of elements to be removed!
     */
    private int calcToFree(Store store) {
        int cnt = store.size();
        if (cnt < 0) {
            getLogger().debug("Unknown size of the store: " + store);
            return 0;
        }
        return (int)(cnt * fraction);
    }

    /**
     * This method forces the garbage collector
    private void freePhysicalMemory() {
        getJVM().runFinalization();
        getJVM().gc();
    }
     */

    private int getFreememory() {
        return freememory;
    }

    private void setFreememory(int _freememory) {
        this.freememory = _freememory;
    }

    private int getHeapsize() {
        return this.heapsize;
    }

    private void setHeapsize(int _heapsize) {
        this.heapsize = _heapsize;
    }

    private int getCleanupthreadinterval() {
        return this.cleanupthreadinterval;
    }

    private void setCleanupthreadinterval(int _cleanupthreadinterval) {
        this.cleanupthreadinterval = _cleanupthreadinterval;
    }

    private int getPriority() {
        return this.priority;
    }

    private void setPriority(int _priority) {
        this.priority = _priority;
    }

    private Runtime getJVM() {
        return this.jvm;
    }

    private void setJVM(Runtime _runtime) {
        this.jvm = _runtime;
    }

    private ArrayList getStoreList() {
        return this.storelist;
    }

    private void setStoreList(ArrayList _storelist) {
        this.storelist = _storelist;
    }

    private void setIndex(int _index) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Setting index=" + _index);
        }
        this.index = _index;
    }

    private int getIndex() {
        return this.index;
    }
}
