/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.avalon.fortress.impl.handler;

import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.instrument.AbstractInstrumentable;
import org.apache.excalibur.instrument.Instrumentable;
import org.apache.excalibur.mpool.ObjectFactory;

/**
 * AbstractComponentHandler class, ensures components are initialized
 * and destroyed correctly.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.2 $ $Date: 2003/02/07 16:08:11 $
 * @since 4.0
 */
public abstract class AbstractComponentHandler
    extends AbstractInstrumentable
    implements Serviceable, Initializable, Disposable, ComponentHandler
{
    /**
     * The instance of the ComponentFactory that creates and disposes of the
     * Component
     */
    protected ObjectFactory m_factory;

    /**
     * State management boolean stating whether the Handler is initialized or
     * not
     */
    protected boolean m_prepared;

    /**
     * State management boolean stating whether the Handler is disposed or
     * not
     */
    protected boolean m_disposed;

    /** Logger for factory */
    protected Logger m_logger;

    /** Logger Manager */
    protected LoggerManager m_loggerManager;

    /**
     * @avalon.dependency type="LoggerManager"
     */
    public void service( ServiceManager manager )
        throws ServiceException
    {
        m_loggerManager =
            (LoggerManager)manager.lookup( LoggerManager.ROLE );
        m_factory =
            (ObjectFactory)manager.lookup( ObjectFactory.ROLE );
    }

    public void initialize()
        throws Exception
    {
        final String classname = getClass().getName();
        final int index = classname.lastIndexOf( '.' );
        final String name = classname.substring( index + 1 );

        String loggerName = name.toLowerCase();
        if( name.endsWith( "ComponentHandler" ) )
        {
            final int endIndex = loggerName.length() - 16;
            loggerName = loggerName.substring( 0, endIndex );
        }

        final String categoryName = "system.handler." + loggerName;
        m_logger =
            m_loggerManager.getLoggerForCategory( categoryName );

        if( m_factory instanceof Instrumentable )
        {
            addChildInstrumentable( (Instrumentable)m_factory );
        }

        setInstrumentableName( name );
    }

    /**
     * Actually prepare the handler and make it ready to
     * handle component access.
     *
     * @throws Exception if unable to prepare handler
     */
    public void prepareHandler()
        throws Exception
    {
        if( m_prepared )
        {
            return;
        }

        if( m_disposed )
        {
            final String message = "Attempted to prepare disposed ComponentHandler for : " +
                m_factory.getCreatedClass().getName();
            m_logger.warn( message );

            return;
        }

        doPrepare();

        if( m_logger.isDebugEnabled() )
        {
            final String message = "ComponentHandler initialized for: " +
                m_factory.getCreatedClass().getName();
            m_logger.debug( message );
        }

        m_prepared = true;
    }

    /**
     * Initialize the ComponentHandler.
     * Subclasses should overide this to do their own initialization.
     */
    protected void doPrepare()
        throws Exception
    {
    }

    /**
     * Get a reference of the desired Component
     * @return the component
     */
    public Object get()
        throws Exception
    {
        synchronized( this )
        {
            if( !m_prepared )
            {
                prepareHandler();
            }
        }

        if( m_disposed )
        {
            final String message =
                "You cannot get a component from a disposed holder";
            throw new IllegalStateException( message );
        }

        return doGet();
    }

    /**
     * Subclasses should actually overide this to do the work
     * of retrieving a service.
     *
     * @return the service
     * @throws Exception if unable to aquire service
     */
    protected abstract Object doGet()
        throws Exception;

    /**
     * Return a reference of the desired Component
     * @param component the component
     */
    public void put( final Object component )
    {
        if( !m_prepared )
        {
            final String message =
                "You cannot put a component in an uninitialized holder";
            throw new IllegalStateException( message );
        }

        doPut( component );
    }

    /**
     * Subclasses should overide this to return component to handler.
     *
     * @param component the component
     */
    protected void doPut( final Object component )
    {
    }

    /**
     * Create a new component for handler.
     *
     * @return the new component
     * @throws Exception if unable to create new component
     */
    protected Object newComponent()
        throws Exception
    {
        try
        {
            return m_factory.newInstance();
        }
        catch( final Exception e )
        {
            if( m_logger.isErrorEnabled() )
            {
                final String message = "Unable to create new instance";
                m_logger.error( message, e );
            }

            throw e;
        }
    }

    /**
     * Dispose of the specified component.
     *
     * @param component the component
     */
    protected void disposeComponent( final Object component )
    {
        if( null == component )
        {
            return;
        }
        try
        {
            m_factory.dispose( component );
        }
        catch( final Exception e )
        {
            if( m_logger.isWarnEnabled() )
            {
                m_logger.warn( "Error disposing component", e );
            }
        }
    }

    /**
     * Dispose of the ComponentHandler and any associated Pools and Factories.
     */
    public void dispose()
    {
        doDispose();
        try
        {
            ContainerUtil.dispose( m_factory );
        }
        catch( RuntimeException e )
        {
            if( m_logger.isWarnEnabled() )
            {
                final String message = "Error decommissioning component: " +
                    m_factory.getCreatedClass().getName();
                m_logger.warn( message, e );
            }
        }

        m_disposed = true;
    }

    /**
     * Dispose handler specific resources.
     * Subclasses should overide this to provide their own funcitonality.
     */
    protected void doDispose()
    {
    }

    /**
     * Represents the handler as a string.
     * @return the string representation of the handler
     */
    public String toString()
    {
        return getClass().getName() + "[for: " + m_factory.getCreatedClass().getName() + "]";
    }
}
