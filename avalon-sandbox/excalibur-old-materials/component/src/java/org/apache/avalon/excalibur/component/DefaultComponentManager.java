/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.component;

/**
 * Default component manager for Avalon's components.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:paul@luminas.co.uk">Paul Russell</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/06/13 17:24:50 $
 * @since 4.0
 * @deprecated  Please use <code>ExcaliburComponentManager</code> instead
 */
public class DefaultComponentManager
    extends ExcaliburComponentManager
{
    public DefaultComponentManager()
    {
        super();
    }

    /** Create the ComponentLocator with a Classloader */
    public DefaultComponentManager( final ClassLoader loader )
    {
        super( loader );
    }
}
