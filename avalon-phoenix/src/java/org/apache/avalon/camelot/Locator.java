/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.camelot;

import java.net.URL;
import org.apache.avalon.component.Component;

/**
 * This contains information required to locate a component.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Locator
    extends Info
{
    /**
     * Retrieve "name" of component type.
     * The "name" usually indicates the classname.
     *
     * @return the name
     */
    String getName();

    /**
     * Retrieve location of component.
     * Usually references the archive (zip/jar/war/ear)
     * which contains the name (ie classname)
     *
     * @return the URL of location
     */
    URL getLocation();
}
