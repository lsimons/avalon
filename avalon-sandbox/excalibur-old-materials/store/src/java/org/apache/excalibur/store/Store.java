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
package org.apache.excalibur.store;

import java.io.IOException;
import java.util.Enumeration;

import org.apache.avalon.framework.component.Component;

/**
 * A Store is an object managing arbitrary data. It holds data stored
 * under a given key persistently. So if you put something in a store
 * you can be sure that the next time (even if the application restarted)
 * your data is in the store (of course unless noone else did remove it).
 * In some cases (like for example a cache) the data needs not to be
 * persistent. Therefore with the two role TRANSIENT_STORE and
 * PERSISTENT_STORE you get a store with exactly that behaviour. (The
 * PERSISTENT_STORE is only an alias for ROLE).
 *
 * @author <a href="mailto:scoobie@betaversion.org">Federico Barbieri</a>
 *         (Betaversion Productions)
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 *         (Apache Software Foundation)
 * @author <a href="mailto:fumagalli@exoffice.com">Pierpaolo Fumagalli</a>
 *         (Apache Software Foundation, Exoffice Technologies)
 * @version CVS $Id: Store.java,v 1.8 2003/03/22 12:46:55 leosimons Exp $
 */
public interface Store
    extends Component
{
    /** The role for a persistent store */
    String ROLE = Store.class.getName();

    /** The role for a transient store */
    String TRANSIENT_STORE = ROLE + "/TransientStore";
    /** The role for a persistent store (this is an alias for ROLE) */
    String PERSISTENT_STORE = ROLE;

    /**
     * Get the object associated to the given unique key.
     */
    Object get( Object key );

    /**
     * Store the given object. It is up to the
     * caller to ensure that the key has a persistent state across
     * different JVM executions.
     */
    void store( Object key, Object value ) throws IOException;

    /**
     * Try to free some used memory. The transient store can simply remove
     * some hold data, the persistent store can free all memory by
     * writing the data to a persistent store etc.
     */
    void free();

    /**
     * Remove the object associated to the given key.
     */
    void remove( Object key );

    /**
     * Clear the Store of all data it holds 
     */
    void clear();

    /**
     * Indicates if the given key is associated to a contained object.
     */
    boolean containsKey( Object key );

    /**
     * Returns the list of used keys as an Enumeration of Objects.
     */
    Enumeration keys();

    /**
     * Returns count of the objects in the store, or -1 if could not be
     * obtained.
     */
    int size();
}
