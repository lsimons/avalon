/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.manager;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.phoenix.components.kernel.DefaultKernel;
import org.apache.avalon.phoenix.components.kernel.DefaultKernelMBean;
import org.apache.avalon.phoenix.interfaces.ManagerException;
import org.apache.excalibur.baxter.JavaBeanMBean;

/**
 * An abstract class via which JMX Managers can extend.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @author <a href="mailto:Huw@mmlive.com">Huw Roberts</a>
 * @version $Revision: 1.1 $ $Date: 2002/07/13 10:15:12 $
 */
public abstract class AbstractJMXManager
    extends AbstractSystemManager
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( AbstractJMXManager.class );

    private MBeanServer m_mBeanServer;
    private String m_domain = "Phoenix";

    public void initialize()
        throws Exception
    {
        super.initialize();

        final MBeanServer mBeanServer = createMBeanServer();
        setMBeanServer( mBeanServer );
    }

    public void start()
        throws Exception
    {
    }

    public void stop()
        throws Exception
    {
    }

    public void dispose()
    {
        setMBeanServer( null );
    }

    /**
     * Export the object to the particular management medium using
     * the supplied object and interfaces.
     * This needs to be implemented by subclasses.
     *
     * @param name the name of object
     * @param object the object
     * @param interfaces the interfaces
     * @return the exported object
     * @throws ManagerException if an error occurs
     */
    protected Object export( final String name,
                             final Object object,
                             final Class[] interfaces )
        throws ManagerException
    {
        try
        {
            final Object mBean = createMBean( object, interfaces );
            final ObjectName objectName = createObjectName( name );
            getMBeanServer().registerMBean( mBean, objectName );
            return mBean;
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "jmxmanager.error.export.fail", name );
            getLogger().error( message, e );
            throw new ManagerException( message, e );
        }
    }

    /**
     * Create MBean for specified object (and interfaces if any).
     *
     * @param object the object
     * @param interfaces the interfaces to export
     * @return the MBean
     * @throws ManagerException if unable to create MBean
     */
    private Object createMBean( final Object object,
                                final Class[] interfaces )
        throws ManagerException
    {
        if( null != interfaces )
        {
            return new JavaBeanMBean( object, interfaces );
        }
        else
        {
            return createMBean( object );
        }
    }

    /**
     * Create JMX name for object.
     *
     * @param name the name of object
     * @return the {@link ObjectName} representing object
     * @throws MalformedObjectNameException if malformed name
     */
    private ObjectName createObjectName( final String name )
        throws MalformedObjectNameException
    {
        return new ObjectName( getDomain() + ":" + name );
    }

    /**
     * Create a MBean for specified object.
     * The following policy is used top create the MBean...
     *
     * @param object the object to create MBean for
     * @return the MBean to be exported
     * @throws ManagerException if an error occurs
     */
    private Object createMBean( final Object object )
        throws ManagerException
    {
        //HACK: ugly Testing hack!!
        if( object instanceof DefaultKernel )
        {
            return new DefaultKernelMBean( (DefaultKernel)object );
        }
        else
        {
            return new JavaBeanMBean( object );
        }
    }

    /**
     * Stop the exported object from being managed.
     *
     * @param name the name of object
     * @param exportedObject the object return by export
     * @throws ManagerException if an error occurs
     */
    protected void unexport( final String name,
                             final Object exportedObject )
        throws ManagerException
    {
        try
        {
            getMBeanServer().unregisterMBean( createObjectName( name ) );
        }
        catch( final Exception e )
        {
            final String message =
                REZ.getString( "jmxmanager.error.unexport.fail", name );
            getLogger().error( message, e );
            throw new ManagerException( message, e );
        }
    }

    /**
     * Verify that an interface conforms to the requirements of management medium.
     *
     * @param clazz the interface class
     * @throws ManagerException if verification fails
     */
    protected void verifyInterface( final Class clazz )
        throws ManagerException
    {
        //TODO: check it extends all right things and that it
        //has all the right return types etc. Blocks must have
        //interfaces extending Service (or Manageable)
    }

    protected MBeanServer getMBeanServer()
    {
        return m_mBeanServer;
    }

    protected void setMBeanServer( MBeanServer mBeanServer )
    {
        m_mBeanServer = mBeanServer;
    }

    protected String getDomain()
    {
        return m_domain;
    }

    protected void setDomain( final String domain )
    {
        m_domain = domain;
    }

    /**
     * Creates a new MBeanServer.
     * The subclass should implement this to create specific MBeanServer.
     */
    protected abstract MBeanServer createMBeanServer()
        throws Exception;
}
