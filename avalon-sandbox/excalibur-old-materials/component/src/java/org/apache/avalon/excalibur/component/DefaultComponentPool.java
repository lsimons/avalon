/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.component;

import org.apache.avalon.excalibur.pool.AbstractPool;
import org.apache.avalon.excalibur.pool.ObjectFactory;
import org.apache.avalon.excalibur.pool.PoolController;
import org.apache.avalon.excalibur.pool.SoftResourceLimitingPool;
import org.apache.avalon.framework.activity.Initializable;

/**
 * This is the implementation of <code>Pool</code> for Avalon
 * Components that is thread safe.  For Component Management, we need
 * soft resource limiting due to the possibility of spikes in demand.
 * This pool will destroy all unnecessary Components when they are
 * no longer needed.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:Giacomo.Pati@pwr.ch">Giacomo Pati</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 05:09:02 $
 * @since 4.0
 *
 * @deprecated DefaultComponentPool is no longer used by the PoolableComponentHandler.
 */
public class DefaultComponentPool
    extends SoftResourceLimitingPool
    implements Initializable
{
    /**
     * Initialize the <code>Pool</code> with an
     * <code>ObjectFactory</code>.
     */
    public DefaultComponentPool( ObjectFactory factory ) throws Exception
    {
        this( factory,
              new DefaultComponentPoolController(
                  AbstractPool.DEFAULT_POOL_SIZE / 4 ),
              AbstractPool.DEFAULT_POOL_SIZE / 4,
              AbstractPool.DEFAULT_POOL_SIZE );
    }

    /**
     * Initialized the <code>Pool</code> with an alternative management
     * infrastructure.
     */
    public DefaultComponentPool( ObjectFactory factory,
                                 PoolController controller,
                                 int minimumPoolSize,
                                 int maximumPoolSIze )
        throws Exception
    {
        super( factory, controller, minimumPoolSize, maximumPoolSIze );
    }
}
