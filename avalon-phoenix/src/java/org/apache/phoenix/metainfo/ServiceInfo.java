/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.metainfo;

import org.apache.avalon.util.Version;

/**
 * This interface describes a particular service that a component offers.
 * 
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface ServiceInfo
    extends org.apache.avalon.camelot.MetaInfo
{
    /**
     * Return version of interface
     *
     * @return the version of interface
     */
    Version getVersion();

    /**
     * Return name of Service (which coresponds to the interface name 
     * eg org.apache.block.Logger).
     *
     * @return the name of the Service
     */
    String getName();

    /**
     * Determine if other service will match this service.
     * To match a service has to have same name and must comply with version.
     *
     * @param other the other ServiceInfo
     * @return true if matches, false otherwise
     */
    boolean matches( ServiceInfo other );
}
