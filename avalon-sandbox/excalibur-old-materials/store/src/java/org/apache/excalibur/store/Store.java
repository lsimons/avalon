/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
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
 * @version CVS $Id: Store.java,v 1.3 2002/07/14 01:28:01 donaldp Exp $
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
