/* 
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avalon.activation.csi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.PrivilegedActionException;

import org.apache.avalon.activation.Appliance;
import org.apache.avalon.activation.ApplianceException;
import org.apache.avalon.activation.LifestyleManager;

import org.apache.avalon.composition.model.ComponentModel;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.Logger;

/**
 * This makes a dynamic proxy for an object.  The object can be represented
 * by one, some or all of it's interfaces.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/02/10 16:14:14 $
 */
public final class SecureInvocationHandler implements InvocationHandler
{
    //-------------------------------------------------------------------
    // immutable state
    //-------------------------------------------------------------------

    private final Appliance m_appliance;
    private final Logger m_logger;

    //-------------------------------------------------------------------
    // immutable state
    //-------------------------------------------------------------------

    private Object m_instance;
    private boolean m_destroyed = false;

    //-------------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------------

   /**
    * Create a proxy invocation handler.
    *
    * @param instance the underlying provider 
    */
    protected SecureInvocationHandler( Appliance appliance, Logger logger, Object instance )
    {
        m_appliance = appliance;
        m_logger = logger;
        m_instance = instance;
    }

    //-------------------------------------------------------------------
    // implementation
    //-------------------------------------------------------------------

   /**
    * Invoke the specified method on underlying object.
    * This is called by the proxy object.
    *
    * @param proxy the proxy object
    * @param method the method invoked on proxy object
    * @param args the arguments supplied to method
    * @return the return value of method
    * @throws Throwable if an error occurs
    */
    public Object invoke( final Object proxy,
      final Method method,
      final Object[] args )
      throws Throwable
    {
        if( proxy == null ) 
          throw new NullPointerException( "proxy" );
        if( method == null ) 
          throw new NullPointerException( "method" );
        if( m_destroyed ) 
          throw new IllegalStateException( "destroyed" );

        try
        {
            return secureInvocation( method, m_instance, args );
        }
        catch( Throwable e )
        {
            throw handleInvocationThrowable( e );
        }
    }

    private Logger getLogger()
    {
        return m_logger;
    }

    protected void release()
    {
        if( !m_destroyed )
        {
            m_destroyed = true;
            final String message = 
              "Releasing component [" 
              + System.identityHashCode( m_instance ) + "] (" 
              + m_appliance.toString()
              + ").";
            getLogger().debug( message );
            m_appliance.release( m_instance );
        }
    }

    protected void finalize() throws Throwable
    {
        if( !m_destroyed && ( null != m_instance ) )
        {
            final String message = 
              "Finalizing proxy [" 
              + System.identityHashCode( m_instance ) + "] (" 
              + m_appliance.toString()
              + ").";
            getLogger().debug( message );
            release();
        }
    }

    private Object getInstance() throws Exception
    {
        return m_instance;
    }

    private Object secureInvocation( 
      final Method method, final Object object, final Object[] args )
      throws Exception
    {
        //if( ! m_secured )
        //{
            return method.invoke( object, args );
        //}
        //else
        //{
        //    Object result = AccessController.doPrivileged( 
        //    new PrivilegedExceptionAction()
        //    {
        //        public Object run() throws Exception
        //        {
        //            return method.invoke( object, args );
        //        }
        //    }, m_accessControlContext );
        //    return result;
        //}
    }
        
    private Throwable handleInvocationThrowable( Throwable e )
    {
        final String error = 
          "Delegation error raised by component: " 
          + m_appliance.toString();
        while( true )
        {
            if( e instanceof UndeclaredThrowableException )
            {
                Throwable cause = 
                  ((UndeclaredThrowableException) e).getUndeclaredThrowable();
                if( cause == null )
                  return new ApplianceException( error, e );
                e = cause;
            }
            else if( e instanceof InvocationTargetException )
            {
                Throwable cause = 
                  ((InvocationTargetException) e).getTargetException();
                if( cause == null )
                  return new ApplianceException( error, e );
                e = cause;
            }
            else if( e instanceof PrivilegedActionException )
            {
                Throwable cause = 
                  ((PrivilegedActionException) e).getException();
                if( cause == null )
                    return new ApplianceException( error, e );
                e = cause;
            }
            else
            {
                break;
            }
        }
        return e;
    }
}
