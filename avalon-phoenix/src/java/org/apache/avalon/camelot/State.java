/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.camelot;

import org.apache.avalon.ValuedEnum;

/**
 * Defines possible states for contained components.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class State
    extends ValuedEnum
{
    public State( final String name, final int value )
    {
        super( name, value );
    }
}
