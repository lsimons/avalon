/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.facilities;

import org.apache.avalon.Component;
import org.apache.avalon.camelot.Entry;

/**
 * Component responsible for building components for entry.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface ComponentBuilder
    extends Component
{
    Object createComponent( String name, Entry entry )
        throws Exception;
}
