/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.component.test;

import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.logger.Logger;

/**
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 05:09:02 $
 */
public class PoolableTestObject
    implements Component, Initializable, Recyclable, Disposable, Poolable
{
    public static final String ROLE = PoolableTestObject.class.getName();

    /** Semaphore used to synchronize access to m_instanceCounter */
    private static Object m_semaphore = new Object();

    /** Number of instances created since the last call to resetInstanceCounter() */
    private static int m_instanceCounter = 0;

    private static Logger m_logger;

    /** Instance Id */
    private int m_instanceId;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public PoolableTestObject()
    {
        synchronized( m_semaphore )
        {
            m_instanceCounter++;
            m_instanceId = m_instanceCounter;
        }
    }

    /*---------------------------------------------------------------
     * Static Methods
     *-------------------------------------------------------------*/
    /**
     * Resets the instance counter so that the next Poolable will get an instance Id of 1.
     */
    public static void resetInstanceCounter()
    {
        synchronized( m_semaphore )
        {
            m_instanceCounter = 0;
        }
    }

    /**
     * Used by tests to change the current logger object.
     */
    public static void setStaticLoggger( Logger logger )
    {
        m_logger = logger;
    }

    /*---------------------------------------------------------------
     * Initializable Methods
     *-------------------------------------------------------------*/
    /**
     * Called by the Container to initialize the component.
     */
    public void initialize()
    {
        m_logger.debug( "PoolableTestObject #" + m_instanceId + " initialized." );
    }

    /*---------------------------------------------------------------
     * Recyclable Methods
     *-------------------------------------------------------------*/
    /**
     * Called by the Container when the component is recycled.
     */
    public void recycle()
    {
        m_logger.debug( "PoolableTestObject #" + m_instanceId + " recycled." );
    }

    /*---------------------------------------------------------------
     * Disposable Methods
     *-------------------------------------------------------------*/
    /**
     * Called by the Container to dispose the component.
     */
    public void dispose()
    {
        m_logger.debug( "PoolableTestObject #" + m_instanceId + " disposed." );
    }
}

