/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.configuration;

import java.io.IOException;
import org.xml.sax.SAXException;

/**
 * The interface implemented to build configurations.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface ConfigurationBuilder
{
    Configuration build( String resource )
        throws SAXException, IOException, ConfigurationException;
}
