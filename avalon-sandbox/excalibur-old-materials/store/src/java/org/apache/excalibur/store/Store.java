/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.store;

import org.apache.avalon.framework.component.Component;

import java.io.IOException;
import java.util.Enumeration;

/**
 * A Store is an object managing arbitrary data. It holds usually data stored
 * under a given key persistently.
 * It is up to the administrator of the application to decide whether the
 * store is persistent or not (by choosing an appropriate implementation).
 * However, the two roles TRANSIENT_STORE and PERSISTENT_STORE must exactly
 * define a STORE which is either transient or persistent, so the application
 * can rely on the behaviour.
 *
 * @author <a href="mailto:scoobie@betaversion.org">Federico Barbieri</a>
 *         (Betaversion Productions)
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 *         (Apache Software Foundation)
 * @author <a href="mailto:fumagalli@exoffice.com">Pierpaolo Fumagalli</a>
 *         (Apache Software Foundation, Exoffice Technologies)
 * @version CVS $Id: Store.java,v 1.1 2002/05/02 08:55:39 cziegeler Exp $
 */
public interface Store
    extends Component
{

    String ROLE = Store.class.getName();

    String TRANSIENT_STORE = "org.apache.cocoon.components.store.Store/TransientCache";
    String PERSISTENT_STORE = "org.apache.cocoon.components.store.Store/PersistentCache";

    /**
     * Get the object associated to the given unique key.
     */
    Object get(Object key);

    /**
     * Store the given object in a persistent state. It is up to the
     * caller to ensure that the key has a persistent state across
     * different JVM executions.
     */
    void store(Object key, Object value) throws IOException;

    /**
     * Holds the given object in a volatile state. This means
     * the object store will discard held objects if the
     * virtual machine is restarted or some error happens.
     */
    void hold(Object key, Object value) throws IOException;

    void free();

    /**
     * Remove the object associated to the given key.
     */
    void remove(Object key);

    /**
     * Indicates if the given key is associated to a contained object.
     */
    boolean containsKey(Object key);

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
