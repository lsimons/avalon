/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.store;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * Repository for Streams
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 */
public interface StreamRepository
    extends Repository
{
    OutputStream put( String key );

    InputStream get( String key );

    void remove( String key );

    Iterator list();
}
