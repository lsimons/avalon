/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

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
package org.apache.avalon.excalibur.component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.thread.SingleThreaded;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.instrument.CounterInstrument;
import org.apache.excalibur.instrument.InstrumentManager;
import org.apache.excalibur.instrument.ValueInstrument;

/**
 * This class acts like a Factory to instantiate the correct version
 * of the ComponentHandler that you need.
 *
 * @deprecated ECM is no longer supported
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:ryan@silveregg.co.jp">Ryan Shaw</a>
 * @author <a href="mailto:leif@apache.org">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 12:45:26 $
 * @since 4.0
 */
public abstract class ComponentHandler
    extends AbstractDualLogEnabledInstrumentable
    implements Initializable, Disposable
{
    private Object m_referenceSemaphore = new Object();
    private int m_references = 0;

    /** Instrument used to profile the number of outstanding references. */
    private ValueInstrument m_referencesInstrument;

    /** Instrument used to profile the number of gets. */
    private CounterInstrument m_getsInstrument;

    /** Instrument used to profile the number of puts. */
    private CounterInstrument m_putsInstrument;

    /*---------------------------------------------------------------
     * Static Methods
     *-------------------------------------------------------------*/
    /**
     * Looks up and returns a component handler for a given component class.
     * The componentClass must either implement Component or have a public
     * static field named ROLE containing the Fully Qualified Classname as a
     * String of the interface or class defining the component's role.
     *
     * @param componentClass Class of the component for which the handle is
     *                       being requested.
     * @param configuration The configuration for this component.
     * @param componentManager The ComponentLocator which will be managing
     *                         the Component.
     * @param context The current context object.
     * @param roleManager The current RoleManager.
     * @param loggerManager The current LogKitLoggerManager.
     *
     * @throws Exception If there were any problems obtaining a ComponentHandler
     *
     * @deprecated This method has been deprecated in favor of the version below which
     *             handles instrumentation.
     */
    public static ComponentHandler getComponentHandler( final Class componentClass,
                                                        final Configuration configuration,
                                                        final ComponentManager componentManager,
                                                        final Context context,
                                                        final RoleManager roleManager,
                                                        final LogkitLoggerManager loggerManager )
        throws Exception
    {
        return ComponentHandler.getComponentHandler( componentClass,
                                                     configuration,
                                                     componentManager,
                                                     context,
                                                     roleManager,
                                                     loggerManager,
                                                     null,
                                                     "N/A" );
    }

    /**
     * Looks up and returns a component handler for a given component class.
     * The componentClass must either implement Component or have a public
     * static field named ROLE containing the Fully Qualified Classname as a
     * String of the interface or class defining the component's role.
     *
     * @param componentClass Class of the component for which the handle is
     *                       being requested.
     * @param configuration The configuration for this component.
     * @param componentManager The ComponentLocator which will be managing
     *                         the Component.
     * @param context The current context object.
     * @param roleManager The current RoleManager.
     * @param loggerManager The current LogKitLoggerManager.
     * @param instrumentManager The current InstrumentManager (May be null).
     * @param instrumentableName The instrumentable name to assign to
     *                           components created by the handler.
     *
     * @throws Exception If there were any problems obtaining a ComponentHandler
     */
    public static ComponentHandler getComponentHandler( final Class componentClass,
                                                        final Configuration configuration,
                                                        final ComponentManager componentManager,
                                                        final Context context,
                                                        final RoleManager roleManager,
                                                        final LogkitLoggerManager loggerManager,
                                                        final InstrumentManager instrumentManager,
                                                        final String instrumentableName )
        throws Exception
    {
    // If componentClass extends Component, everything
    // is fly, as no proxy needs to be generated, and we can pass
    // in null for the role. If not, we check for a public ROLE
    // member and use that. If that fails, we complain loudly...
    String role = null;
    final boolean isComponent = Component.class.isAssignableFrom( componentClass );

    if( role == null && !isComponent )
    {
        try
        {
            final Field field = componentClass.getField( "ROLE" );
            final boolean isStatic = Modifier.isStatic(field.getModifiers());
            if( !isStatic )
                throw new IllegalArgumentException( "the componentClass you provided" +
                    "does not implement Component, and you also did not" +
                    "specify a role." );

            final Object fieldContents = field.get(null);
            if( fieldContents instanceof String )
                role = (String)field.get(null); // found the role
        }
        catch( NoSuchFieldException nsfe )
        {
            throw new IllegalArgumentException( "the componentClass you provided" +
                    "does not implement Component, and you also did not" +
                    "specify a role." );
        }
        catch( SecurityException se )
        {
            throw new IllegalArgumentException( "the componentClass you provided" +
                    "does not implement Component, and you also did not" +
                    "specify a role." );
        }
        catch( IllegalArgumentException iae )
        {
             // won't happen
            throw iae;
        }
        catch( IllegalAccessException iae )
        {
            // won't happen
            throw new IllegalArgumentException( "the componentClass you provided" +
                    "does not implement Component, and you also did not" +
                    "specify a role." );
        }
    }
        return ComponentHandler.getComponentHandler( role,
                                                     componentClass,
                                                     configuration,
                                                     componentManager,
                                                     context,
                                                     roleManager,
                                                     loggerManager,
                                                     instrumentManager,
                                                     instrumentableName );
    }

    /**
     * Looks up and returns a component handler for a given component class.
     *
     * @param role           The role name of the component. This must be
     *                       a fully-qualified classname.
     * @param componentClass Class of the component for which the handle is
     *                       being requested.
     * @param configuration The configuration for this component.
     * @param componentManager The ComponentLocator which will be managing
     *                         the Component.
     * @param context The current context object.
     * @param roleManager The current RoleManager.
     * @param loggerManager The current LogKitLoggerManager.
     *
     * @throws Exception If there were any problems obtaining a ComponentHandler
     *
     * @deprecated This method has been deprecated in favor of the version below which
     *             handles instrumentation.
     */
    public static ComponentHandler getComponentHandler( final String role,
                                                        final Class componentClass,
                                                        final Configuration configuration,
                                                        final ComponentManager componentManager,
                                                        final Context context,
                                                        final RoleManager roleManager,
                                                        final LogkitLoggerManager loggerManager )
        throws Exception
    {
        return ComponentHandler.getComponentHandler( role,
                                                     componentClass,
                                                     configuration,
                                                     componentManager,
                                                     context,
                                                     roleManager,
                                                     loggerManager,
                                                     null,
                                                     "N/A" );
    }

    /**
     * Looks up and returns a component handler for a given component class.
     *
     * @param role           The role name of the component. This must be
     *                       a fully-qualified classname
     * @param componentClass Class of the component for which the handle is
     *                       being requested.
     * @param configuration The configuration for this component.
     * @param componentManager The ComponentLocator which will be managing
     *                         the Component.
     * @param context The current context object.
     * @param roleManager The current RoleManager.
     * @param loggerManager The current LogKitLoggerManager.
     * @param instrumentManager The current InstrumentManager (May be null).
     * @param instrumentableName The instrumentable name to assign to
     *                           components created by the handler.
     *
     * @throws Exception If there were any problems obtaining a ComponentHandler
     */
    public static ComponentHandler getComponentHandler( final String role,
                                                        final Class componentClass,
                                                        final Configuration configuration,
                                                        final ComponentManager componentManager,
                                                        final Context context,
                                                        final RoleManager roleManager,
                                                        final LogkitLoggerManager loggerManager,
                                                        final InstrumentManager instrumentManager,
                                                        final String instrumentableName )
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
            throw new Exception( "[CONFLICT] More than one lifecycle interface in "
                                 + componentClass.getName() + "  May implement no more than one of "
                                 + "SingleThreaded, ThreadSafe, or Poolable" );
        }

        // Create the factory to use to create the instances of the Component.
        DefaultComponentFactory factory =
            new DefaultComponentFactory( role,
                                         componentClass,
                                         configuration,
                                         componentManager,
                                         context,
                                         roleManager,
                                         loggerManager,
                                         instrumentManager,
                                         instrumentableName );

        ComponentHandler handler;
        if( Poolable.class.isAssignableFrom( componentClass ) )
        {
            handler = new PoolableComponentHandler( factory, configuration );
        }
        else if( ThreadSafe.class.isAssignableFrom( componentClass ) )
        {
            handler = new ThreadSafeComponentHandler( factory, configuration );
        }
        else // This is a SingleThreaded component
        {
            handler = new DefaultComponentHandler( factory, configuration );
        }

        // Set the instrumentable name of the handler.
        handler.setInstrumentableName(
            ExcaliburComponentManager.INSTRUMENTABLE_NAME + "." + instrumentableName );

        return handler;
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

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new ComponentHandler.
     */
    public ComponentHandler()
    {
        // Initialize the Instrumentable elements.
        addInstrument( m_referencesInstrument = new ValueInstrument( "references" ) );
        addInstrument( m_getsInstrument = new CounterInstrument( "gets" ) );
        addInstrument( m_putsInstrument = new CounterInstrument( "puts" ) );
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
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

        // Notify the instrument manager
        m_getsInstrument.increment();
        m_referencesInstrument.setValue( m_references );

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

        m_putsInstrument.increment();
        m_referencesInstrument.setValue( m_references );

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
