/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.avalon.magic.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationHandler;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.parameters.Parameters;

/**
 * An InvocationHandler that intercepts calls to the avalon-framework
 * lifecycle methods and saves their arguments for later use. During
 * initialize, an instance is created with the constructor arguments
 * populated from stuff retrieved from the avalon-framework lifecycle
 * arguments.
 *
 * Usage:
 *
 * <pre>
 * public class MyComponentImpl implements MyComponent
 * {
 *      Logger m_logger;
 *      Configuration m_config;
 *      MyOtherComponent m_moc;
 *
 *      public MyComponentImpl( Logger logger, Configuration config,
 *              MyOtherComponent moc )
 *      {
 *          m_logger = logger;
 *          m_configuration = config;
 *          m_moc = moc;
 *      }
 *
 *      public void doStuff() {
 *          moc.callMe();
 *      }
 * }
 *
 * // and then somewhere....
 * MyComponent comp = (MyComponent)
 *          AvalonInvocationHandler.getProxy( MyComponentImpl.class );
 *
 * // the container will set up your logger, configuration and
 * // all other dependencies for you
 * myAvalonContainer.add( comp );
 *
 * // you can only use methods specified by interfaces here, but
 * // that's already true for all avalon components
 * comp.doStuff();
 *
 * </pre>
 *
 * Yes, indeed, this allows you to deploy any PicoContainer-compatible
 * component into an existing avalon container! This class can also be
 * used by avalon containers internally to automagicallly support
 * PicoContainer-compatible components.
 *
 * @version $Id: Avalon2PicoAdapter.java,v 1.2 2003/08/21 20:58:31 leosimons Exp $
 */
public class Avalon2PicoAdapter implements InvocationHandler
{
    // ----------------------------------------------------------------------
    //  Properties
    // ----------------------------------------------------------------------
    private boolean initialized = false;
    private boolean badstate = false;
    private Class m_targetClass;
    private Object m_target;

    private Logger m_log;
    private Context m_context;
    private ServiceManager m_serviceManager;
    private Configuration m_configuration;

    public final static Method ENABLE_LOGGING;
    static
    {
        Method m;
        try
        {
            m = LogEnabled.class.getMethod( "enableLogging",
                    new Class[] { Logger.class } );
        }
        catch( NoSuchMethodException nsme )
        {
            // won't happen
            m = null;
        }
        ENABLE_LOGGING = m;
    }

    public final static Method SERVICE;
    static
    {
        Method m;
        try
        {
            m = Serviceable.class.getMethod( "service",
                    new Class[] { ServiceManager.class } );
        }
        catch( NoSuchMethodException nsme )
        {
            // won't happen
            m = null;
        }
        SERVICE = m;
    }

    public final static Method CONTEXTUALIZE;
    static
    {
        Method m;
        try
        {
            m = Contextualizable.class.getMethod( "contextualize",
                    new Class[] { Context.class } );
        }
        catch( NoSuchMethodException nsme )
        {
            // won't happen
            m = null;
        }
        CONTEXTUALIZE = m;
    }

    public final static Method CONFIGURE;
    static
    {
        Method m;
        try
        {
            m = Configurable.class.getMethod( "configure",
                    new Class[] { Configuration.class } );
        }
        catch( NoSuchMethodException nsme )
        {
            // won't happen
            m = null;
        }
        CONFIGURE = m;
    }

    public final static Method INITIALIZE;
    static
    {
        Method m;
        try
        {
            m = Initializable.class.getMethod( "initialize",
                    new Class[0] );
        }
        catch( NoSuchMethodException nsme )
        {
            // won't happen
            m = null;
        }
        INITIALIZE = m;
    }

    // ----------------------------------------------------------------------
    //  Constructors
    // ----------------------------------------------------------------------

    Avalon2PicoAdapter( Class target )
    {
        setTargetClass( target );
    }

    /**
     * Create a proxy that will redirect calls to avalon-framework
     * lifecycle methods to a multi-argument constructor.
     *
     * @param targetClass
     * @return
     */
    public static Object getProxy( Class targetClass )
    {
        // all interfaces implemented by the class,
        // and all lifecycle interfaces implemented
        // by the handler
        Class[] intf = targetClass.getInterfaces();
        List i = new ArrayList( Arrays.asList(intf) );
        i.add( LogEnabled.class );
        i.add( Serviceable.class );
        i.add( Contextualizable.class );
        i.add( Configurable.class );
        i.add( Initializable.class );
        intf = (Class[])i.toArray( new Class[i.size()] );

        return Proxy.newProxyInstance(
                targetClass.getClassLoader(),
                intf,
                new Avalon2PicoAdapter( targetClass ) );
    }

    // ----------------------------------------------------------------------
    //  Methods
    // ----------------------------------------------------------------------

    protected Class getTargetClass()
    {
        return m_targetClass;
    }

    protected void setTargetClass( Class targetClass )
    {
        m_targetClass = targetClass;
    }

    protected Logger getLog()
    {
        return m_log;
    }

    protected void setLog( Logger log )
    {
        m_log = log;
    }

    protected Context getContext()
    {
        return m_context;
    }

    protected void setContext( Context context )
    {
        m_context = context;
    }

    protected ServiceManager getServiceManager()
    {
        return m_serviceManager;
    }

    protected void setServiceManager( ServiceManager serviceManager )
    {
        m_serviceManager = serviceManager;
    }

    protected Configuration getConfiguration()
    {
        return m_configuration;
    }

    protected void setConfiguration( Configuration configuration )
    {
        m_configuration = configuration;
    }

    public Object invoke( Object proxy, Method method, Object[] args )
            throws Throwable
    {
        if(badstate)
            throw new IllegalStateException( "Initialization did not complete without errors!" );
        if(initialized)
            return method.invoke( m_target, args );

        if( ENABLE_LOGGING.equals( method ) )
        {
            setLog( (Logger)args[0] );
            return null;
        }
        if( CONTEXTUALIZE.equals( method ) )
        {
            setContext( (Context)args[0] );
            return null;
        }
        if( SERVICE.equals( method ) )
        {
            setServiceManager( (ServiceManager)args[0] );
            return null;
        }
        if( CONFIGURE.equals( method ) )
        {
            setConfiguration( (Configuration)args[0] );
            return null;
        }
        if( INITIALIZE.equals( method ) )
        {
            try
            {
                createInstance();
            }
            catch( Exception e )
            {
                badstate = true;
                throw e;
            }
            return null;
        }

        if( m_target == null )
            throw new IllegalStateException( "You need to call initialize() first!" );

        return method.invoke( m_target, args );
    }

    protected void createInstance()
        throws Exception
    {
        // select the longest constructor (argument-wise)
        Constructor[] constructors = getTargetClass().getConstructors();
        sortConstructors( constructors );
        Constructor constructor = constructors[0];

        // call it
        m_target = constructor.newInstance( getArguments( constructor ) );

        // for any lifecycle interfaces already implemented by the target
        ContainerUtil.enableLogging( m_target, getLog() );
        ContainerUtil.contextualize( m_target, getContext() );
        ContainerUtil.service( m_target, getServiceManager() );
        ContainerUtil.contextualize( m_target, getContext() );
        ContainerUtil.configure( m_target, getConfiguration() );
        ContainerUtil.parameterize( m_target, Parameters.fromConfiguration( getConfiguration() ) );
        ContainerUtil.initialize( m_target );

        initialized = true;
    }

    protected Object[] getArguments( Constructor c )
        throws Exception
    {
        Class[] paramTypes = c.getParameterTypes();
        List args = new ArrayList();

        for( int i = 0; i < paramTypes.length; i++ )
        {
            // first try a (reversed) avalon lifecycle ordering
            if( paramTypes[i].isAssignableFrom( Parameters.class ) )
            {
                args.add( i, Parameters.fromConfiguration( getConfiguration() ) );
                break;
            }
            if( paramTypes[i].isAssignableFrom( Configuration.class ) )
            {
                args.add( i, getConfiguration() );
                break;
            }
            if( paramTypes[i].isAssignableFrom( ServiceManager.class ) )
            {
                args.add( i, getServiceManager() );
                break;
            }
            if( getServiceManager().hasService( getRole( paramTypes[i] ) ) )
            {
                Object comp = getServiceManager().lookup( paramTypes[i].getName() );
                args.add( i, comp );
                break;
            }
            if( paramTypes[i].isAssignableFrom( Context.class ) )
            {
                args.add( i, getContext() );
                break;
            }

            Object comp = null;
            String role = getRole( paramTypes[i] );
            try
            {
                comp = getContext().get( role );
            }
            catch( ContextException ce )
            {
                // that's okay
            }
            if( comp != null )
            {
                args.add( i, comp );
                break;
            }
            if( paramTypes[i].isAssignableFrom( Logger.class ) )
            {
                args.add( i, getLog() );
                break;
            }

            throw new ServiceException( paramTypes[i].getName(), "Missing dependency!" );
        }

        return args.toArray();
    }

    protected String getRole( Class clazz )
    {
        try
        {
            Field field = clazz.getField( "ROLE" );
            int mods = field.getModifiers();
            if(     Modifier.isPublic( mods ) &&
                    Modifier.isStatic( mods ) &&
                    Modifier.isFinal( mods ) )
                return field.get( null ).toString();
            else
                return clazz.getName();
        }
        catch( NoSuchFieldException nsfe )
        {
            return clazz.getName();
        }
        catch( IllegalAccessException iae )
        {
            return clazz.getName();
        }
    }

    /**
     * Sort constructors in the order to be tried. This
     * implementation sorts by whether a constructor is
     * public, next by the number of arguments (descending).
     *
     * @return
     */
    protected static void sortConstructors( Constructor[] constr )
    {
        Arrays.sort( constr, new Comparator()
        {
            public int compare( Object o1, Object o2 )
            {
                Constructor c1 = (Constructor) o1;
                Constructor c2 = (Constructor) o2;

                // compare accessibility
                if( Modifier.isPublic( c1.getModifiers() ) )
                {
                    if( Modifier.isPublic( c2.getModifiers() ) )
                        return -1; // more important
                } else if( Modifier.isPublic( c2.getModifiers() ) )
                {
                    return 1; // more important
                }

                // both accessible, compare length
                int a1 = c1.getParameterTypes().length;
                int a2 = c2.getParameterTypes().length;

                if( a1 > a2 )
                    return -1; // more important
                if( a1 == a2 )
                    return 0;

                return 1;
            }
        }
        );
    }
}
