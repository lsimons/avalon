/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.pool.test;

import java.util.HashMap;
import org.apache.avalon.excalibur.pool.ObjectFactory;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.Logger;

/**
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 05:09:04 $
 * @since 4.1
 */
public class ClassInstanceObjectFactory
    implements ObjectFactory
{
    private HashMap m_instances = new HashMap();
    private Logger m_logger;
    private Class m_clazz;
    private int m_id;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a reproducable log of activity in the provided StringBuffer
     */
    public ClassInstanceObjectFactory( Class clazz, Logger logger )
    {
        m_clazz = clazz;
        m_logger = logger;
        m_id = 1;
    }

    /*---------------------------------------------------------------
     * ObjectFactory Methods
     *-------------------------------------------------------------*/
    public Object newInstance() throws Exception
    {
        Object object = m_clazz.newInstance();
        Integer id = new Integer( m_id++ );

        m_instances.put( object, id );

        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "ClassInstanceObjectFactory.newInstance()  id:" + id );
        }

        return object;
    }

    public Class getCreatedClass()
    {
        return m_clazz;
    }

    public void decommission( Object object ) throws Exception
    {
        if( object instanceof Disposable )
        {
            ( (Disposable)object ).dispose();
        }
        Integer id = (Integer)m_instances.remove( object );

        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "ClassInstanceObjectFactory.decommission(a "
                            + object.getClass().getName() + ")  id:" + id );
        }
    }
}

