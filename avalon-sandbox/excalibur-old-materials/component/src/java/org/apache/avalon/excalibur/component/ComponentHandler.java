/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.component;

import org.apache.avalon.excalibur.logger.LogKitManager;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.thread.SingleThreaded;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * This class acts like a Factory to instantiate the correct version
 * of the ComponentHandler that you need.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:ryan@silveregg.co.jp">Ryan Shaw</a>
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 05:09:02 $
 * @since 4.0
 */
public abstract class ComponentHandler extends AbstractLoggable
    implements Initializable, Disposable
{

    private Object m_referenceSemaphore = new Object();
    private int m_references = 0;

    public static ComponentHandler getComponentHandler(
        final Class componentClass,
        final Configuration config,
        final ComponentManager manager,
        final Context context,
        final RoleManager roles,
        final LogKitManager logkit )
        throws Exception
    {
        int numInterfaces = 0;

        if( SingleThreaded.class.isAssignableFrom( componentClass ) )
        {
            numInterfaces++;
        }

        if( ThreadSafe.class.isAssignableFrom( componentClass ) )
        {
            numInterfaces++;
        }

        if( Poolable.class.isAssignableFrom( componentClass ) )
        {
            numInterfaces++;
        }

        if( numInterfaces > 1 )
        {
            throw new Exception( "[CONFLICT] lifestyle interfaces: " + componentClass.getName() );
        }

        if( Poolable.class.isAssignableFrom( componentClass ) )
        {
            return new PoolableComponentHandler( componentClass,
                                                 config,
                                                 manager,
                                                 context,
                                                 roles,
                                                 logkit );
        }
        else if( ThreadSafe.class.isAssignableFrom( componentClass ) )
        {
            return new ThreadSafeComponentHandler( componentClass,
                                                   config,
                                                   manager,
                                                   context,
                                                   roles,
                                                   logkit );
        }
        else // This is a SingleThreaded component
        {
            return new DefaultComponentHandler( componentClass,
                                                config,
                                                manager,
                                                context,
                                                roles,
                                                logkit );
        }
    }

    public static ComponentHandler getComponentHandler(
        final Component componentInstance )
        throws Exception
    {
        int numInterfaces = 0;

        if( SingleThreaded.class.isAssignableFrom( componentInstance.getClass() ) )
        {
            numInterfaces++;
        }

        if( ThreadSafe.class.isAssignableFrom( componentInstance.getClass() ) )
        {
            numInterfaces++;
        }

        if( Poolable.class.isAssignableFrom( componentInstance.getClass() ) )
        {
            numInterfaces++;
        }

        if( numInterfaces > 1 )
        {
            throw new Exception( "[CONFLICT] lifestyle interfaces: " + componentInstance.getClass().getName() );
        }

        return new ThreadSafeComponentHandler( componentInstance );
    }

    /**
     * Get an instance of the type of component handled by this handler.
     * <p>
     * Subclasses should not extend this method but rather the doGet method below otherwise
     *  reference counts will not be supported.
     * <p>
     * This method is not final to make the class backwards compatible.
     *
     * @return a <code>Component</code>
     * @exception Exception if an error occurs
     */
    public Component get() throws Exception
    {
        Component component = doGet();

        synchronized( m_referenceSemaphore )
        {
            m_references++;
        }

        return component;
    }

    /**
     * Put back an instance of the type of component handled by this handler.
     * <p>
     * Subclasses should not extend this method but rather the doPut method below otherwise
     *  reference counts will not be supported.
     * <p>
     * This method is not final to make the class backwards compatible.
     *
     * @param component a <code>Component</code>
     * @exception Exception if an error occurs
     */
    public void put( Component component ) throws Exception
    {
        // The reference count must be decremented before any calls to doPut.
        //  If there is another thread blocking, then this thread could stay deep inside
        //  doPut for an undetermined amount of time until the thread scheduler gives it
        //  some cycles again.  (It happened).  All ComponentHandler state must therefor
        //  reflect the thread having left this method before the call to doPut to avoid
        //  warning messages from the dispose() cycle if that takes place before this
        //  thread has a chance to continue.
        synchronized( m_referenceSemaphore )
        {
            m_references--;
        }

        try
        {
            doPut( component );
        }
        catch( Throwable t )
        {
            t.printStackTrace();
        }
    }

    /**
     * Concrete implementation of getting a component.
     *
     * @return a <code>Component</code> value
     * @exception Exception if an error occurs
     */
    protected Component doGet() throws Exception
    {
        // This method is not abstract to make the class backwards compatible.
        throw new IllegalStateException( "This method must be overridden." );
    }

    /**
     * Concrete implementation of putting back a component.
     *
     * @param component a <code>Component</code> value
     * @exception Exception if an error occurs
     */
    protected void doPut( Component component ) throws Exception
    {
        // This method is not abstract to make the class backwards compatible.
        throw new IllegalStateException( "This method must be overridden." );
    }
    
    /**
     * Returns the current number of references.
     *
     * @return The current number of references.
     */
    protected int getReferences()
    {
        return m_references;
    }

    /**
     * Returns <code>true</code> if this component handler can safely be
     * disposed (i.e. none of the components it is handling are still
     * being used).
     *
     * @return <code>true</code> if this component handler can safely be
     *         disposed; <code>false</code> otherwise
     */
    public final boolean canBeDisposed()
    {
        return ( m_references == 0 );
    }
}
