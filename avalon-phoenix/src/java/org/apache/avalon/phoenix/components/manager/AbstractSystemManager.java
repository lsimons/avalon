/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.manager;

import java.util.HashMap;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.logger.AbstractLoggable;

/**
 * This is abstract implementation of SystemManager.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class AbstractSystemManager
    extends AbstractLoggable
    implements SystemManager
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( AbstractSystemManager.class );

    protected final static class ManagedEntry
    {
        ///Object passed in for management
        protected Object   m_object;

        ///Interfaces object wants to be managed through (can be null)
        protected Class[]  m_interfaces;

        ///Object representation when exported (usually a proxy)
        protected Object   m_exportedObject;
    }

    private HashMap       m_entrys = new HashMap();

    /**
     * Register an object for management.
     * The object is exported through some management scheme
     * (typically JMX) and the management is restricted
     * to the interfaces passed in as a parameter to method.
     *
     * @param name the name to register object under
     * @param object the object
     * @param interfaces the interfaces to register the component under
     * @exception ManagerException if an error occurs. An error could occur if the object doesn't
     *            implement the interfaces, the interfaces parameter contain non-instance
     *            classes, the name is already registered etc.
     * @exception IllegalArgumentException if object or interfaces is null
     */
    public synchronized void register( final String name, final Object object, final Class[] interfaces )
        throws ManagerException, IllegalArgumentException
    {
        checkRegister( name, object );
        if( null == interfaces )
        {
            final String message = REZ.format( "manager.error.interfaces.null", name );
            throw new IllegalArgumentException( message );
        }

        verifyInterfaces( object, interfaces );

        final ManagedEntry entry = new ManagedEntry();
        entry.m_object = object;
        entry.m_interfaces = interfaces;
        entry.m_exportedObject = export( name, entry.m_object, interfaces );

        m_entrys.put( name, entry );
    }

    /**
     * Register an object for management.
     * The object is exported through some management scheme
     * (typically JMX).
     *
     * @param name the name to register object under
     * @param object the object
     * @exception ManagerException if an error occurs such as name already registered.
     * @exception IllegalArgumentException if object is null
     */
    public synchronized void register( final String name, final Object object )
        throws ManagerException, IllegalArgumentException
    {
        checkRegister( name, object );

        final ManagedEntry entry = new ManagedEntry();
        entry.m_object = object;
        entry.m_interfaces = null;
        entry.m_exportedObject = export( name, entry.m_object, null );

        m_entrys.put( name, entry );
    }

    /**
     * Unregister named object.
     *
     * @param name the name of object to unregister
     * @exception ManagerException if an error occurs such as when no such object registered.
     */
    public synchronized void unregister( final String name )
        throws ManagerException
    {
        final ManagedEntry entry = (ManagedEntry)m_entrys.remove( name );

        if( null == entry )
        {
            final String message = REZ.format( "manager.error.unregister.noentry", name );
            throw new ManagerException( message );
        }

        unexport( name, entry.m_exportedObject );
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
     * @exception ManagerException if an error occurs
     */
    protected abstract Object export( String name, Object object, Class[] interfaces )
        throws ManagerException;

    /**
     * Stop the exported object from being managed.
     *
     * @param name the name of object
     * @param exportedObject the object return by export
     * @exception ManagerException if an error occurs
     */
    protected abstract void unexport( String name, Object exportedObject )
        throws ManagerException;

    /**
     * Verify that an interface conforms to the requirements of management medium.
     *
     * @param clazz the interface class
     * @exception ManagerException if verification fails
     */
    protected abstract void verifyInterface( Class clazz )
        throws ManagerException;

    /**
     * Verify that object implements interfaces and interfaces are of "acceptable form".
     * "Acceptable form" is determined by specific management policy.
     *
     * @param object the object
     * @param interfaces the array of interfaces to check
     * @exception ManagerException if an error occurs
     */
    private void verifyInterfaces( final Object object, final Class[] interfaces )
        throws ManagerException
    {
        for( int i = 0; i < interfaces.length; i++ )
        {
            final Class clazz = interfaces[ i ];

            if( !clazz.isInterface() )
            {
                final String message = 
                    REZ.format( "manager.error.verify.notinterface", clazz.getName() );
                throw new ManagerException( message );
            }

            if( !clazz.isInstance( object ) )
            {
                final String message = 
                    REZ.format( "manager.error.verify.notinstance", clazz.getName() );
                throw new ManagerException( message );
            }

            verifyInterface( clazz );
        }
    }

    /**
     * Helper method to help check before an objects registration.
     * Verifies name and object are not null and verifies no entry exists using name.
     *
     * @param name the name of object
     * @param object the object to be registered
     * @exception ManagerException if name already exists
     * @exception IllegalArgumentException if name or object is null
     */
    private void checkRegister( final String name, final Object object )
        throws ManagerException, IllegalArgumentException
    {
        if( null == object )
        {
            throw new NullPointerException( "object" );
        }

        if( null == name )
        {
            throw new NullPointerException( "name" );
        }

        if( null != m_entrys.get( name ) )
        {
            final String message = REZ.format( "manager.error.register.exists", name );
            throw new ManagerException( message );
        }
    }
}
