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

import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.store.Store;

/**
 * 
 * @avalon.component
 * @avalon.service type=Store
 * @x-avalon.info name=mem-store
 * @x-avalon.lifestyle type=singleton
 *
 * @author <a href="mailto:scoobie@betaversion.org">Federico Barbieri</a>
 *         (Betaversion Productions)
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 *         (Apache Software Foundation)
 * @author <a href="mailto:fumagalli@exoffice.com">Pierpaolo Fumagalli</a>
 *         (Apache Software Foundation, Exoffice Technologies)
 * @version CVS $Id: MemoryStore.java,v 1.8 2003/08/28 12:40:28 cziegeler Exp $
 */
public class MemoryStore
    extends AbstractLogEnabled
    implements Store, ThreadSafe {

    /* WARNING: Hashtable is threadsafe, whereas HashMap is not.
     * Should we move this class over to the Collections API,
     * use Collections.synchronizedMap(Map map) to ensure
     * accesses are synchronized.
     */

    /** The shared store */
    protected Hashtable m_table = new Hashtable();

    /**
     * Get the object associated to the given unique key.
     */
    public Object get(Object key) 
    {
        return m_table.get(key);
    }

    /**
     * Store the given object in a persistent state. It is up to the
     * caller to ensure that the key has a persistent state across
     * different JVM executions.
     */
    public void store(Object key, Object value) 
    {
        m_table.put(key,value);
    }

    /**
     * Remove the object associated to the given key.
     */
    public void remove(Object key) 
    {
        m_table.remove(key);
    }

    /**
     * Clear the Store of all elements 
     */
    public void clear() 
    {
        m_table.clear();
    }

    public void free() {}

    /**
     * Indicates if the given key is associated to a contained object.
     */
    public boolean containsKey(Object key) 
    {
        return m_table.containsKey(key);
    }

    /**
     * Returns the list of used keys as an Enumeration of Objects.
     */
    public Enumeration keys() 
    {
        return m_table.keys();
    }

    /**
     * Returns count of the objects in the store, or -1 if could not be
     * obtained.
     */
    public int size()
    {
        return m_table.size();
    }
}
