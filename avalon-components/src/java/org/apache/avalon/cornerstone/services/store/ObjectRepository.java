/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.store;

import java.util.Iterator;

/**
 * Repository for Serializable Objects.
 *
 * @author Federico Barbieri <fede@apache.org>
 */
public interface ObjectRepository
    extends Repository
{
    Object get( String key );

    Object get( String key, ClassLoader classLoader );

    void put( String key, Object value );

    void remove( String key );

    boolean containsKey( String key );

    Iterator list();
}
