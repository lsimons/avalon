/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.excalibur.monitor;

/**
 * This interface is used by the Monitor section so that we can test if a
 * resource is modified by an external source.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version $Id: Modifiable.java,v 1.2 2001/11/19 12:08:47 donaldp Exp $
 */
public interface Modifiable
{
    /**
     * Tests if a resource has been modified, and causes the resource to act on
     * that test.  The contract is that the method does its work <b>only</b>
     * when the time passed in is after the last time the resource was modified.
     */
    void testModifiedAfter( long time );

    /**
     * Simply provides the last time the resource has been modified.
     */
    long lastModified();
}
