/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.phases;

import org.apache.avalon.framework.Enum;

/**
 * A type-safe enumeration of possible traversal methods.
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mailto:jefft@apache.org">Jeff Turner</a>
 */
public final class Traversal
    extends Enum
{
    public final static Traversal  FORWARD     = new Traversal( "FORWARD" );
    public final static Traversal  REVERSE     = new Traversal( "REVERSE" );
    public final static Traversal  LINEAR      = new Traversal( "LINEAR" );

    private Traversal( final String name )
    {
        super( name );
    }
}
