/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.embeddor;

import java.io.File;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.io.ExtensionFileFilter;
import org.apache.avalon.framework.CascadingException;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.phoenix.Constants;
import org.apache.avalon.phoenix.interfaces.Deployer;
import org.apache.avalon.phoenix.interfaces.Embeddor;
import org.apache.avalon.phoenix.interfaces.EmbeddorMBean;
import org.apache.avalon.phoenix.interfaces.Kernel;

/**
 * This is the object that is interacted with to create, manage and
 * dispose of the kernel and related resources.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="peter@apache.org">Peter Donald</a>
 * @author <a href="bauer@denic.de">Joerg Bauer</a>
 */
public class DefaultEmbeddor
    extends AbstractLogEnabled
    implements Embeddor, Contextualizable, Parameterizable, Configurable, EmbeddorMBean
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultEmbeddor.class );

    private static final String DEFAULT_APPS_PATH = "/apps";

    private EmbeddorObservable m_observable = new EmbeddorObservable();
    private Parameters m_parameters;

    /**
     * Context passed to embeddor. See the contextualize() method
     * for details on what is stored in context.
     *
     * @see DefaultEmbeddor#contextualize(Context)
     */
    private Context m_context;

    private String m_phoenixHome;

    private EmbeddorEntry[] m_components;

    /**
     * If true, flag indicates that the Embeddor should continue running
     * even when there are no applications in kernel. Otherwise the
     * Embeddor will shutdown when it detects there is no longer any
     * applications running.
     */
    private boolean m_persistent;

    /**
     * Flag is set to true when the embeddor should  shut itself down.
     * It is set to true as a result of a call to shutdown() method.
     *
     * @see Embeddor#shutdown()
     */
    private boolean m_shutdown;

    /**
     * Time at which the embeddor was started.
     */
    private long m_startTime;

    /**
     * Pass the Context to the embeddor.
     * It is expected that the following will be entrys in context;
     * <ul>
     *   <li><b>common.classloader</b>: ClassLoader shared betweeen
     *      container and applications</li>
     *   <li><b>container.classloader</b>: ClassLoader used to load
     *      container</li>
     * </ul>
     *
     * @param context
     * @throws ContextException
     */
    public void contextualize( final Context context )
        throws ContextException
    {
        m_context = context;
        try
        {
            final Observer observer = (Observer)context.get( Observer.class.getName() );
            m_observable.addObserver( observer );
        }
        catch( final ContextException ce )
        {
            final String message = REZ.getString( "embeddor.notice.no-restart" );
            getLogger().warn( message );
        }
    }

    /**
     * Set parameters for this component.
     * This must be called after contextualize() and before initialize()
     *
     * Make sure to provide all the neccessary information through
     * these parameters. All information it needs consists of strings.
     * There are two types of strings included in parameters. The first
     * type include parameters used to setup proeprties of the embeddor.
     * The second type include the implementation names of the components
     * that the Embeddor manages. For instance if you want to replace the
     * <code>ConfigurationRepository</code> with your own repository you
     * would pass in a parameter such as;</p>
     * <p>org.apache.avalon.phoenix.interfaces.ConfigurationRepository =
     * com.biz.MyCustomConfigurationRepository</p>
     *
     * <p>Of the other type of parameters, the following are supported by
     * the DefaultEmbeddor implementation of Embeddor. Note that some of
     * the embedded components may support other parameters.</p>
     * <ul>
     * <li><b>phoenix.home</b>, the home directory of phoenix. Defaults
     * to "..".</li>
     * <li><b>log-destination</b>, the file to save log
     * messages in. If omitted, ${phoenix.home}/logs/phoenix.log is used.</li>
     * <li><b>log-priority</b>, the priority at which log messages are filteres.
     * If omitted, then INFO will be default level used.</li>
     * <li><b>applications-directory</b>, the directory in which
     * the defaul applications to be loaded by the kernel are stored
     * (in .sar format). Defaults to ${phoenix.home}/apps</li>
     * </ul>
     *
     * @param parameters the Parameters for embeddor
     * @exception ParameterException if an error occurs
     */
    public synchronized void parameterize( final Parameters parameters )
        throws ParameterException
    {
        m_parameters = parameters;
        m_parameters.setParameter( "phoenix.home", ".." );
        m_parameters.setParameter( "persistent", "false" );
        m_phoenixHome = m_parameters.getParameter( "phoenix.home" );
        m_persistent = m_parameters.getParameterAsBoolean( "persistent" );
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration[] children = configuration.getChildren( "component" );
        m_components = new EmbeddorEntry[ children.length ];
        for( int i = 0; i < children.length; i++ )
        {
            final String role = children[ i ].getAttribute( "role" );
            final String classname = children[ i ].getAttribute( "class" );
            final String logger = children[ i ].getAttribute( "logger" );
            final Configuration childConfiguration = children[ i ];

            m_components[ i ] = new EmbeddorEntry();
            m_components[ i ].setRole( role );
            m_components[ i ].setClassName( classname );
            m_components[ i ].setLoggerName( logger );
            m_components[ i ].setConfiguration( childConfiguration );
        }
    }

    /**
     * Creates the core handlers - logger, deployer, Manager and
     * Kernel. Note that these are not set up properly until you have
     * called the <code>run()</code> method.
     */
    public void initialize()
        throws Exception
    {
        m_startTime = System.currentTimeMillis();
        try
        {
            createComponents();
            setupComponents();
        }
        catch( final Exception e )
        {
            // whoops!
            final String message = REZ.getString( "embeddor.error.start.failed" );
            getLogger().fatalError( message, e );
            throw e;
        }
    }

    /**
     * This is the main method of the embeddor. It sets up the core
     * components, and then deploys the <code>Facilities</code>. These
     * are registered with the Kernel and the Manager. The same
     * happens for the <code>Applications</code>.
     * Now, the Kernel is taken through its lifecycle. When it is
     * finished, as well as all the applications running in it, it
     * is shut down, after which the PhoenixEmbeddor is as well.
     */
    public void execute()
        throws Exception
    {
        deployDefaultApplications();

        //  If the kernel is empty at this point, it is because the server was
        //  started without supplying any applications, display a message to
        //  give the user a clue as to why the server is shutting down
        //  immediately.
        if( emptyKernel() )
        {
            final String message = REZ.getString( "embeddor.error.start.no-apps" );
            getLogger().fatalError( message );
        }
        else
        {
            // loop until <code>Shutdown</code> is created.
            while( true )
            {
                // wait() for shutdown() to take action...
                if( m_shutdown ||
                    ( emptyKernel() && !m_persistent ) )
                {
                    // The server will shut itself down when all applications are disposed.
                    if( emptyKernel() )
                    {
                        final String message = REZ.getString( "embeddor.shutdown.all-apps-disposed" );
                        getLogger().info( message );
                    }
                    break;
                }
                gotoSleep();
            }
        }
    }

    private boolean emptyKernel()
    {
        Kernel kernel = (Kernel)getEmbeddorComponent( Kernel.ROLE );
        if( null != kernel )
        {
            final String[] names = kernel.getApplicationNames();
            return ( 0 == names.length );
        }
        else
        {
            //Consider the kernel empty
            //if it has been shutdown
            return true;
        }
    }

    private void gotoSleep()
    {
        try
        {
            synchronized( this )
            {
                wait( 1000 );
            }
        }
        catch( final InterruptedException e )
        {
        }
    }

    /**
     * Release all the resources associated with kernel.
     */
    public synchronized void dispose()
    {
        shutdown();
        try
        {
            shutdownComponents();
        }
        catch( final Exception e )
        {
            // whoops!
            final String message = REZ.getString( "embeddor.error.shutdown.failed" );
            getLogger().fatalError( message, e );
        }
        for( int i = 0; i < m_components.length; i++ )
        {
            m_components[ i ].setObject( null );
        }
        System.gc(); // make sure resources are released
    }

    /**
     * Request the Embeddor shutsdown.
     */
    public void shutdown()
    {
        m_shutdown = true;
        synchronized( this )
        {
            notifyAll();
        }
    }

    /**
     * Ask the embeddor to restart itself if this operation is supported.
     *
     * @exception UnsupportedOperationException if restart not supported
     */
    public void restart()
        throws UnsupportedOperationException
    {
        try
        {
            m_observable.change();
            m_observable.notifyObservers( "restart" );
        }
        catch( final Exception e )
        {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Get name by which the server is know.
     * Usually this defaults to "Phoenix" but the admin
     * may assign another name. This is useful when you
     * are managing a cluster of Phoenix servers.
     *
     * @return the name of server
     */
    public String getName()
    {
        return Constants.SOFTWARE;
    }

    /**
     * Get location of Phoenix installation
     *
     * @return the home directory of phoenix
     */
    public String getHomeDirectory()
    {
        return m_phoenixHome;
    }

    /**
     * Get the date at which this server started.
     *
     * @return the date at which this server started
     */
    public Date getStartTime()
    {
        return new Date( m_startTime );
    }

    /**
     * Retrieve the number of millisecond
     * the server has been up.
     *
     * @return the the number of millisecond the server has been up
     */
    public long getUpTimeInMillis()
    {
        return System.currentTimeMillis() - m_startTime;
    }

    /**
     * Retrieve a string identifying version of server.
     * Usually looks like "v4.0.1a".
     *
     * @return version string of server.
     */
    public String getVersion()
    {
        return Constants.VERSION;
    }

    /**
     * Get a string defining the build.
     * Possibly the date on which it was built, where it was built,
     * with what features it was built and so forth.
     *
     * @return the string describing build
     */
    public String getBuild()
    {
        return "(" + Constants.DATE + ")";
    }

    //////////////////////
    /// HELPER METHODS ///
    //////////////////////
    /**
     * Create the logger, deployer and kernel components.
     * Note that these components are not ready to be used
     * until setupComponents() is called.
     */
    private synchronized void createComponents()
    {
        Object object;
        try
        {
            for( int i = 0; i < m_components.length; i++ )
            {
                object = createComponent( m_components[ i ].getClassName(), Class.forName( m_components[ i ].getClassName() ) );
                m_components[ i ].setObject( object );
            }
        }
        catch( Exception e )
        {
            final String message = REZ.getString( "embeddor.error.createComponents.failed" );
            getLogger().fatalError( message, e );
        }
    }

    /**
     * The deployer is used to load the applications from the
     * default-apps-location specified in Parameters.
     * TODO: load facilities from .fars as well.
     *
     * @exception Exception if an error occurs
     */
    protected void deployDefaultApplications()
        throws Exception
    {
        //Name of optional application specified on CLI
        final String application = m_parameters.getParameter( "application-location", null );
        if( null != application )
        {
            final File file = new File( application );
            deployFile( file );
        }
        final String defaultAppsLocation = m_parameters.getParameter( "applications-directory", m_phoenixHome + DEFAULT_APPS_PATH );
        if( null != defaultAppsLocation )
        {
            final File directory = new File( defaultAppsLocation );
            final ExtensionFileFilter filter = new ExtensionFileFilter( ".sar" );
            final File[] files = directory.listFiles( filter );
            if( null != files )
            {
                deployFiles( files );
            }
        }
    }

    private void deployFiles( final File[] files )
        throws Exception
    {
        for( int i = 0; i < files.length; i++ )
        {
            deployFile( files[ i ] );
        }
    }

    private void deployFile( final File file )
        throws Exception
    {
        final String filename = file.getName();
        int index = filename.lastIndexOf( '.' );
        if( -1 == index ) index = filename.length();
        final String name = filename.substring( 0, index );
        final File canonicalFile = file.getCanonicalFile();
        deployFile( name, canonicalFile );
    }

    protected final synchronized void deployFile( final String name, final File file )
        throws Exception
    {
        final Deployer deployer = (Deployer)getEmbeddorComponent( Deployer.ROLE );
        deployer.deploy( name, file.toURL() );
    }

    private void setupComponents()
        throws Exception
    {
        for( int i = 0; i < m_components.length; i++ )
        {
            final Component component = (Component)( m_components[ i ].getObject() );
            final String loggerName = m_components[ i ].getLoggerName();
            final Configuration configuration = m_components[ i ].getConfiguration();
            setupComponent( component, loggerName, configuration );
        }
    }

    /**
     * Setup a component and run it through al of it's
     * setup lifecycle stages.
     *
     * @param component the component
     * @exception Exception if an error occurs
     */
    private void setupComponent( final Component component, final String loggerName, Configuration config )
        throws Exception
    {
        setupLogger( component, loggerName );
        if( component instanceof Contextualizable )
        {
            ( (Contextualizable)component ).contextualize( m_context );
        }
        if( component instanceof Composable )
        {
            final ComponentManager componentManager = getComponentManager();
            ( (Composable)component ).compose( componentManager );
        }
        if( component instanceof Parameterizable )
        {
            ( (Parameterizable)component ).parameterize( m_parameters );
        }
        else if( component instanceof Configurable )
        {
            ( (Configurable)component ).configure( config );
        }
        if( component instanceof Initializable )
        {
            ( (Initializable)component ).initialize();
        }
        if( component instanceof Startable )
        {
            ( (Startable)component ).start();
        }
    }

    private void shutdownComponents()
        throws Exception
    {
        for( int i = 0; i < m_components.length; i++ )
        {
            final EmbeddorEntry entry = m_components[ i ];
            shutdownComponent( (Component)entry.getObject(); );
        }
    }

    /**
     * Shutdown a component and run it through al of it's
     * shutdown lifecycle stages.
     *
     * @param component the component
     * @exception Exception if an error occurs
     */
    private void shutdownComponent( final Component component )
        throws Exception
    {
        if( null == component ) return;
        if( component instanceof Startable )
        {
            ( (Startable)component ).stop();
        }
        if( component instanceof Disposable )
        {
            ( (Disposable)component ).dispose();
        }
    }

    /**
     * Create a component that implements an interface.
     *
     * @param component the name of the component
     * @param clazz the name of interface/type
     * @return the created object
     * @exception Exception if an error occurs
     */
    private Object createComponent( final String component, final Class clazz )
        throws Exception
    {
        try
        {
            final Object object = Class.forName( component ).newInstance();
            if( !clazz.isInstance( object ) )
            {
                final String message = REZ.getString( "bad-type.error", component, clazz.getName() );
                throw new Exception( message );
            }
            return object;
        }
        catch( final IllegalAccessException iae )
        {
            final String message = REZ.getString( "bad-ctor.error", clazz.getName(), component );
            throw new CascadingException( message, iae );
        }
        catch( final InstantiationException ie )
        {
            final String message = REZ.getString( "no-instantiate.error", clazz.getName(), component );
            throw new CascadingException( message, ie );
        }
        catch( final ClassNotFoundException cnfe )
        {
            final String message = REZ.getString( "no-class.error", clazz.getName(), component );
            throw new CascadingException( message, cnfe );
        }
    }

    private ComponentManager getComponentManager()
    {
        final DefaultComponentManager componentManager = new DefaultComponentManager();
        componentManager.put( Embeddor.ROLE, this );
        for( int i = 0; i < m_components.length; i++ )
        {
            componentManager.put( m_components[ i ].getRole(), (Component)getEmbeddorComponent( m_components[ i ].getRole() ) );
        }
        return componentManager;
    }

    /**
     * Allow subclasses to get access to kernel.
     *
     * @return the Kernel
     */
    protected final Kernel getKernel()
    {
        return (Kernel)getEmbeddorComponent( Kernel.ROLE );
    }

    /**
     * Allow subclasses to get access to parameters.
     *
     * @return the Parameters
     */
    protected final Parameters getParameters()
    {
        return m_parameters;
    }

    private Object getEmbeddorComponent( final String role )
    {
        for( int i = 0; i < m_components.length; i++ )
        {
            final EmbeddorEntry entry = m_components[ i ];
            if( entry.getRole().equals( role ) )
            {
                return m_components[ i ].getObject();
            }
        }
        // Should never happen
        // TODO: create error / warning
        return null;
    }
}

class EmbeddorObservable
    extends Observable
{
    public void change()
    {
        super.setChanged();
    }
}