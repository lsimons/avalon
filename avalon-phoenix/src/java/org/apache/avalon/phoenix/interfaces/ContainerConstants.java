/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.interfaces;

/**
 * A set of constants that are used internally in the container to communicate
 * about different artefacts. They usually act as keys into maps.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public interface ContainerConstants
{
    /**
     * The name of the software. (Usually phoenix but different
     * users may overide this).
     */
    String SOFTWARE = "@@NAME@@";

    /**
     * The version of the software.
     */
    String VERSION = "@@VERSION@@";

    /**
     * The date on which software was built.
     */
    String DATE = "@@DATE@@";

    /**
     * The name of the attribute used to determine whether
     * a block is not proxied.
     */
    String DISABLE_PROXY_ATTR = "phoenix:disable-proxy";

    /**
     * The name which the assembly is registered into phoenix
     * using.
     */
    String ASSEMBLY_NAME = "phoenix:assembly-name";

    /**
     * The name of the config file which is used
     * to load assembly data.
     */
    String ASSEMBLY_CONFIG = "phoenix:config";

    /**
     * The default classloader to use to load components.
     */
    String ASSEMBLY_CLASSLOADER = "phoenix:classloader";

    /**
     * The name of the partition in which blocks are contained.
     */
    String BLOCK_PARTITION = "block";

    /**
     * The name of the partition in which listeners are contained.
     */
    String LISTENER_PARTITION = "listener";

    String ROOT_INSTRUMENT_CATEGORY = "applications";
}
