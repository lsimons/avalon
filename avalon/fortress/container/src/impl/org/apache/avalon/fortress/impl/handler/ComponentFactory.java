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
import org.apache.avalon.framework.component.WrapperComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.LogKit2AvalonLoggerAdapter;
import org.apache.avalon.framework.logger.Loggable;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.avalon.fortress.impl.LifecycleExtensionManager;
import org.apache.excalibur.instrument.CounterInstrument;
import org.apache.excalibur.instrument.Instrument;
import org.apache.excalibur.instrument.InstrumentManageable;
import org.apache.excalibur.instrument.InstrumentManager;
import org.apache.excalibur.instrument.Instrumentable;
import org.apache.excalibur.mpool.ObjectFactory;

/**
 * Factory for Avalon components.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:paul@luminas.co.uk">Paul Russell</a>
 * @version CVS $Revision: 1.3 $ $Date: 2003/02/10 15:47:00 $
 * @since 4.0
 */
public class ComponentFactory
    extends AbstractLogEnabled
    implements ObjectFactory, ThreadSafe, Instrumentable
{
    private CounterInstrument m_newInstance;
    private CounterInstrument m_dispose;

    /**
     * Name of the Instrumentable.  Maps to the id of the component in the
     * configuration.
     */
    private String m_instrumentableName;

    /** The class which this <code>ComponentFactory</code>
     * should create.
     */
    private Class m_componentClass;

    /** The Context for the component
     */
    private Context m_context;

    /** The component manager for this component.
     */
    private ServiceManager m_serviceManager;

    /** The configuration for this component.
     */
    private Configuration m_configuration;

    /** The LogKitManager for child ComponentSelectors
     */
    private LoggerManager m_loggerManager;

    /** Lifecycle extensions manager
     */
    private final LifecycleExtensionManager m_extManager;

    /** InstrumentManager
     */
    private final InstrumentManager m_instrumentManager;
    private Logger m_componentLogger;

    /**
     * Construct a new component factory for the specified component.
     *
     * @param componentClass the class to instantiate (must have a default constructor).
     * @param configuration the <code>Configuration</code> object to pass to new instances.
     * @param serviceManager the service manager to pass to <code>Serviceable</code>s.
     * @param context the <code>Context</code> to pass to <code>Contexutalizable</code>s.
     * @param loggerManager the loggerManager manager instance.
     */
    public ComponentFactory( final Class componentClass,
                             final Configuration configuration,
                             final ServiceManager serviceManager,
                             final Context context,
                             final LoggerManager loggerManager,
                             final LifecycleExtensionManager extManager,
                             final InstrumentManager instrumentManager )
    {
        m_componentClass = componentClass;
        m_configuration = configuration;
        m_serviceManager = serviceManager;
        m_context = context;
        m_loggerManager = loggerManager;
        m_extManager = extManager;
        enableLogging( m_loggerManager.getLoggerForCategory( "system.factory" ) );
        m_instrumentManager = instrumentManager;
        m_instrumentableName = configuration.getAttribute( "id", componentClass.getName() );
        m_componentLogger = aquireLogger();

        m_newInstance = new CounterInstrument( "creates" );
        m_dispose = new CounterInstrument( "destroys" );
    }

   /**
    * Returns a new instance of a component and optionally applies a logging channel,
    * instrumentation, context, a component or service manager, configuration, parameters,
    * lifecycle extensions, initialization, and execution phases based on the interfaces
    * implemented by the component class.
    *
    * @return the new instance
    * @exception
    */
    public Object newInstance()
        throws Exception
    {
        final Object component = m_componentClass.newInstance();

        if( getLogger().isDebugEnabled() )
        {
            final String message =
                "ComponentFactory creating new instance of " +
                m_componentClass.getName() + ".";
            getLogger().debug( message );
        }

        if( component instanceof LogEnabled ||
            component instanceof Loggable )
        {

            if( component instanceof LogEnabled )
            {
                ContainerUtil.enableLogging( component, m_componentLogger );
            }
            else
            {
                final String message = "WARNING: " + m_componentClass.getName() +
                    " implements the Loggable lifecycle stage. This is " +
                    " a deprecated feature that will be removed in the future. " +
                    " Please upgrade to using LogEnabled.";
                getLogger().warn( message );
                System.out.println( message );

                final org.apache.log.Logger logkitLogger =
                    LogKit2AvalonLoggerAdapter.createLogger( m_componentLogger );
                ( (Loggable)component ).setLogger( logkitLogger );
            }
        }

        if( component instanceof InstrumentManageable )
        {
            ( (InstrumentManageable)component ).setInstrumentManager( m_instrumentManager );
        }

        ContainerUtil.contextualize( component, m_context );
        ContainerUtil.compose( component, new WrapperComponentManager( m_serviceManager ) );
        ContainerUtil.service( component, m_serviceManager );
        ContainerUtil.configure( component, m_configuration );

        if( component instanceof Parameterizable )
        {
            Parameters parameters = Parameters.fromConfiguration( m_configuration );
            ContainerUtil.parameterize( component, parameters );
        }

        m_extManager.executeCreationExtensions( component, m_context );

        ContainerUtil.initialize( component );

        if( component instanceof Instrumentable )
        {
            final Instrumentable instrumentable = (Instrumentable)component;
            instrumentable.setInstrumentableName( m_instrumentableName );
            m_instrumentManager.registerInstrumentable(
                instrumentable, m_instrumentableName );
        }

        ContainerUtil.start( component );

        if( m_newInstance.isActive() )
        {
            m_newInstance.increment();
        }

        return component;
    }

    private Logger aquireLogger()
    {
        Logger logger;
        final String name = ( m_configuration == null ? null : m_configuration.getAttribute( "name", null ) );
        if( null == name )
        {
            if( getLogger().isDebugEnabled() )
            {
                final String message = "no name attribute available, using standard name";
                getLogger().debug( message );
            }
            logger = m_loggerManager.getDefaultLogger();
        }
        else
        {
            if( getLogger().isDebugEnabled() )
            {
                final String message = "name attribute is " + name;
                getLogger().debug( message );
            }
            logger = m_loggerManager.getLoggerForCategory( name );
        }
        return logger;
    }

   /**
    * Returns the component class.
    * @return the class
    */
    public final Class getCreatedClass()
    {
        return m_componentClass;
    }

   /**
    * Disposal of the supplied component instance.
    * @param component the component to dispose of
    * @exception Exception if a disposal error occurs
    */
    public final void dispose( final Object component )
        throws Exception
    {
        if( getLogger().isDebugEnabled() )
        {
            final String message = "ComponentFactory decommissioning instance of " +
                getCreatedClass().getName() + ".";
            getLogger().debug( message );
        }

        if( getCreatedClass().equals( component.getClass() ) )
        {
            ContainerUtil.shutdown( component );

            m_extManager.executeDestructionExtensions( component, m_context );

            if( m_dispose.isActive() )
            {
                m_dispose.increment();
            }
        }
        else
        {
            final String message = "The object given to be disposed does " +
                "not come from this ObjectFactory";
            throw new IllegalArgumentException( message );
        }
    }

   /**
    * Set the instrumentable name
    * @param name the name
    */
    public final void setInstrumentableName( String name )
    {
        // ignore
    }

   /**
    * Returns the instrumentable name
    * @return the name
    */
    public final String getInstrumentableName()
    {
        return m_instrumentableName;
    }

   /**
    * Returns the set of instruments assigned to the component factory.
    * @return the instruments
    */
   public final Instrument[] getInstruments()
    {
        return new Instrument[]
        {
            m_newInstance,
            m_dispose
        };
    }

   /**
    * Returns the set of child instrumentables. The default implementation
    * simply returns an empty instrumentable array.
    * @return the instrumentables
    */
    public final Instrumentable[] getChildInstrumentables()
    {
        return Instrumentable.EMPTY_INSTRUMENTABLE_ARRAY;
    }
}
