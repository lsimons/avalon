/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.services;

/**
 * @author Federico Barbieri <fede@apache.org>
 * @deprecated Use org.apache.cornerstone.services.store.* instead
 */
public interface Store
    extends org.apache.cornerstone.services.store.Store
{
    public interface ObjectRepository 
        extends org.apache.cornerstone.services.store.ObjectRepository
    {
    }

    public interface StreamRepository 
        extends org.apache.cornerstone.services.store.StreamRepository
    {
    }
}
