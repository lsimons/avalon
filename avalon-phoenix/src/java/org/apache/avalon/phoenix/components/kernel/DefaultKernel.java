/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

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

package org.apache.avalon.phoenix.components.kernel;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.CascadingException;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.DefaultServiceManager;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.phoenix.components.application.DefaultApplication;
import org.apache.avalon.phoenix.containerkit.profile.PartitionProfile;
import org.apache.avalon.phoenix.interfaces.Application;
import org.apache.avalon.phoenix.interfaces.ApplicationContext;
import org.apache.avalon.phoenix.interfaces.ApplicationMBean;
import org.apache.avalon.phoenix.interfaces.ConfigurationRepository;
import org.apache.avalon.phoenix.interfaces.ConfigurationValidator;
import org.apache.avalon.phoenix.interfaces.Kernel;
import org.apache.avalon.phoenix.interfaces.KernelMBean;
import org.apache.avalon.phoenix.interfaces.SystemManager;
import org.apache.excalibur.instrument.InstrumentManager;
import org.realityforge.loggerstore.LoggerStore;

/**
 * The ServerKernel is the core of the Phoenix system.
 * The kernel is responsible for orchestrating low level services
 * such as loading, configuring and destroying blocks. It also
 * gives access to basic facilities such as scheduling sub-systems,
 * protected execution contexts, naming and directory services etc.
 *
 * Note that no facilities are available until after the Kernel has been
 * configured and initialized.
 *
 * @author Peter Donald
 * @author <a href="mailto:leosimons@apache.org">Leo Simons</a>
 */
public class DefaultKernel
    extends AbstractLogEnabled
    implements Kernel, KernelMBean, Initializable, Serviceable, Disposable, Configurable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultKernel.class );

    /**
     * The interfaces of application that are exported to Management system.
     * Declared up here to avoid classloader deadlock issues where ApplicationMBean
     * is loaded after the application starts. If the application is also loading
     * classes and happens to recursively enter the bootstrap classloader a deadlock
     * can be caused. P{lacing class interfaces up here avoids this deadlock.
     */
    private static final Class[] APPLICATION_INTERFACES =
        new Class[]{ApplicationMBean.class};

    ///SystemManager provided by Embeddor
    private SystemManager m_systemManager;

    private SystemManager m_applicationManager;

    ///Configuration Repository
    private ConfigurationRepository m_repository;

    //Configuration Validator
    private ConfigurationValidator m_validator;

    //Instrument Manager
    private InstrumentManager m_instrumentManager;

    private final HashMap m_entries = new HashMap();

    //Allow applications that fail to startup to remain in the kernel in a stopped state?
    private boolean m_addInvalidApplications;

    //Counter to provide simple locking. when the count is 0, the kernel is unlocked
    private int m_lockCount;

    //List of applications to return when the kernel is locked
    private String[] m_lockedApplications;

    public void service( final ServiceManager serviceManager )
        throws ServiceException
    {
        m_systemManager = (SystemManager)serviceManager.lookup( SystemManager.ROLE );
        m_repository = (ConfigurationRepository)serviceManager.
            lookup( ConfigurationRepository.ROLE );
        m_validator = (ConfigurationValidator)serviceManager.lookup( ConfigurationValidator.ROLE );
        m_instrumentManager = (InstrumentManager)serviceManager.lookup( InstrumentManager.ROLE );
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_addInvalidApplications =
            configuration.getChild( "add-invalid-applications" ).getValueAsBoolean( false );
    }

    public void initialize()
        throws Exception
    {
        m_applicationManager =
            m_systemManager.getSubContext( null, "application" );
    }

    public void dispose()
    {
        final String[] names = getApplicationNames();
        for( int i = 0; i < names.length; i++ )
        {
            try
            {
                final SarEntry entry = (SarEntry)m_entries.get( names[ i ] );
                shutdown( entry );
            }
            catch( final Exception e )
            {
                final String message = REZ.getString( "kernel.error.entry.dispose", names[ i ] );
                getLogger().warn( message, e );
            }
        }
    }

    /**
     * Lock the kernel, temporarily preserving the list of applications running in the container
     */
    public void lock()
    {
        synchronized( this )
        {
            m_lockedApplications = getApplicationNames();
            m_lockCount++;

            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Kernel locked [count:" + m_lockCount + "]" );
            }
        }
    }

    /**
     * Unlock the kernel, restoring the list of applications to be the current active list
     */
    public void unlock()
    {
        synchronized( this )
        {
            m_lockCount--;

            if( m_lockCount < 0 )
            {
                throw new IllegalStateException( REZ.getString( "kernel.error.negativelock") );
            }

            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Kernel unlocked [count:" + m_lockCount + "]" );
            }
        }
    }

    public String[] getApplicationNames()
    {
        if( isLocked() )
        {
            return m_lockedApplications;
        }
        else
        {
            return (String[])m_entries.keySet().toArray( new String[ 0 ] );
        }
    }

    private boolean isLocked()
    {
        return m_lockCount > 0;
    }

    public Application getApplication( final String name )
    {
        final SarEntry entry = (SarEntry)m_entries.get( name );
        if( null == entry )
        {
            return null;
        }
        else
        {
            return entry.getApplication();
        }
    }

    /**
     * Create and initialize the application instance if it is not already initialized.
     *
     * @param entry the entry for application
     * @throws Exception if an error occurs
     */
    private void startup( final SarEntry entry )
        throws Exception
    {
        //lock for application startup and shutdown
        synchronized( entry )
        {
            final String name = entry.getProfile().getMetaData().getName();

            Application application = entry.getApplication();
            if( null == application )
            {
                try
                {
                    final Application newApp = new DefaultApplication();
                    final Logger childLogger =
                        getLogger().getChildLogger( name );
                    ContainerUtil.enableLogging( newApp, childLogger );

                    final ApplicationContext context =
                        createApplicationContext( entry );
                    newApp.setApplicationContext( context );

                    ContainerUtil.initialize( newApp );

                    application = newApp;
                }
                catch( final Throwable t )
                {
                    //Initialization failed so clean entry
                    //so invalid instance is not used
                    entry.setApplication( null );

                    final String message =
                        REZ.getString( "kernel.error.entry.initialize",
                                       entry.getProfile().getMetaData().getName() );
                    throw new CascadingException( message, t );
                }

                try
                {
                    ContainerUtil.start( application );
                }
                catch( final Throwable t )
                {
                    final String message =
                        REZ.getString( "kernel.error.entry.start",
                                       entry.getProfile().getMetaData().getName() );

                    if( m_addInvalidApplications )
                    {
                        getLogger().warn( message, t );
                    }
                    else
                    {
                        //Initialization failed so clean entry
                        //so invalid instance is not used
                        entry.setApplication( null );

                        throw new CascadingException( message, t );
                    }
                }

                entry.setApplication( application );

                // manage application
                try
                {
                    m_applicationManager.register( name,
                                                   application,
                                                   APPLICATION_INTERFACES );
                }
                catch( final Throwable t )
                {
                    final String message =
                        REZ.getString( "kernel.error.entry.manage", name );
                    throw new CascadingException( message, t );
                }
            }
        }
    }

    private void shutdown( final SarEntry entry )
        throws Exception
    {
        //lock for application startup and shutdown
        synchronized( entry )
        {
            final Application application = entry.getApplication();
            if( null != application )
            {
                entry.setApplication( null );
                ContainerUtil.shutdown( application );
            }
            else
            {
                final String message =
                    REZ.getString( "kernel.error.entry.nostop",
                                   entry.getProfile().getMetaData().getName() );
                getLogger().warn( message );
            }
        }
    }

    public void addApplication( final PartitionProfile profile,
                                final File homeDirectory,
                                final File workDirectory,
                                final ClassLoader classLoader,
                                final LoggerStore store,
                                final Map classloaders )
        throws Exception
    {

        final String name = profile.getMetaData().getName();
        final SarEntry entry =
            new SarEntry( profile, homeDirectory,
                          workDirectory, classLoader,
                          store, classloaders );
        m_entries.put( name, entry );

        try
        {
            startup( entry );
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "kernel.error.entry.start", name );
            getLogger().warn( message, e );
            throw e;
        }
    }

    private ApplicationContext createApplicationContext( final SarEntry entry )
        throws Exception
    {
        final String name = entry.getProfile().getMetaData().getName();

        final DefaultApplicationContext context =
            new DefaultApplicationContext( entry.getProfile(),
                                           entry.getHomeDirectory(),
                                           entry.getWorkDirectory(),
                                           entry.getClassLoader(),
                                           entry.getLoggerStore(),
                                           entry.getClassLoaders() );

        ContainerUtil.enableLogging( context, createContextLogger( name ) );
        ContainerUtil.service( context, createServiceManager() );
        ContainerUtil.initialize( context );
        return context;
    }

    /**
     * Create a logger for specified ApplicationContext.
     *
     * @param name the name of application name
     * @return the Logger for context
     */
    private Logger createContextLogger( final String name )
    {
        final String loggerName = name + ".frame";
        final Logger childLogger =
            getLogger().getChildLogger( loggerName );
        return childLogger;
    }

    private ServiceManager createServiceManager()
    {
        final DefaultServiceManager serviceManager = new DefaultServiceManager();
        serviceManager.put( SystemManager.ROLE, m_systemManager );
        serviceManager.put( ConfigurationRepository.ROLE, m_repository );
        serviceManager.put( ConfigurationValidator.ROLE, m_validator );
        serviceManager.put( InstrumentManager.ROLE, m_instrumentManager );
        serviceManager.put( Kernel.ROLE, this );
        serviceManager.makeReadOnly();
        return serviceManager;
    }

    public void removeApplication( final String name )
        throws Exception
    {
        final SarEntry entry = (SarEntry)m_entries.remove( name );
        if( null == entry )
        {
            final String message =
                REZ.getString( "kernel.error.entry.initialize", name );
            throw new Exception( message );
        }
        else
        {
            // un-manage application
            try
            {
                m_applicationManager.unregister( name );
            }
            catch( final Throwable t )
            {
                final String message =
                    REZ.getString( "kernel.error.entry.unmanage", name );
                throw new CascadingException( message, t );
            }

            shutdown( entry );
        }
    }
}
