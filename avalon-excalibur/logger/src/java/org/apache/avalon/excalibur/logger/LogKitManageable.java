/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.logger;

/**
 * LogKitManageable Interface, use this to set the LogKitManagers for child
 * Components.
 *
 * @author <a href="mailto:giacomo@apache.org">Giacomo Pati</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 02:34:14 $
 * @since 4.0
 */
public interface LogKitManageable
{
    /**
     * Sets the LogKitManager for child components.  Can be for special
     * purpose components, however it is used mostly internally.
     */
    void setLogKitManager( final LogKitManager logmanager );
}
