/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.pool;

/**
 * This is the interface for Pools that are not a fixed size.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface Resizable
{
    void grow(int amount);
    void shrink(int amount);
}
