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

package org.apache.avalon.activation.af4.standard;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

import org.apache.avalon.activation.af4.DefaultAppliance;
import org.apache.avalon.activation.af4.ApplianceInvocationHandler;

import org.apache.avalon.activation.appliance.ApplianceException;
import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.meta.info.InfoDescriptor;

/**
* This makes a dynamic proxy for an object.  The object can be represented
* by one, some or all of it's interfaces.
*
*/
final class StandardApplianceInvocationHandler
    implements ApplianceInvocationHandler
{
    private final Object    m_instance;
    private boolean         m_destroyed;
    private Logger          m_logger;
    private DefaultAppliance    m_appliance;
   /**
    * Create a proxy invocation handler.
    *
    * @param instance the underlying provider 
    */
    StandardApplianceInvocationHandler( 
        Logger logger, 
        DefaultAppliance appliance,
        Object instance )
    {
        m_instance = instance;
        m_logger = logger;
        m_appliance = appliance;
        m_destroyed = false;
    }

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
            return method.invoke( m_instance, args );
        }
        catch( Throwable e )
        {
            e = handleInvocationThrowable( e );
            throw e;
        }
    }

    protected void finalize() throws Throwable
    {
        if( !m_destroyed )
        {
            final String message = 
              "Releasing component [" 
              + System.identityHashCode( m_instance ) + "] (" 
              + getComponentModel().getType().getInfo().getLifestyle()
              + "/" 
              + InfoDescriptor.getCollectionPolicyKey( 
                  getComponentModel().getCollectionPolicy() ) 
              + ").";
            m_logger.debug( message );
            m_appliance.release( m_instance, true );
        }
    }

    public Object getInstance()
    {
        return m_instance;
    }

    public void notifyDestroyed()
    {
        m_destroyed = true;
    }
    
   /**
    * Return the component deployment model. 
    */
    private ComponentModel getComponentModel()
    {
        return m_appliance.getComponentModel();
    }

    private Throwable handleInvocationThrowable( Throwable e )
    {
        final String error = 
          "Delegation error raised by component: " 
          + getComponentModel().getQualifiedName();
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
            else
            {
                break;
            }
        }
        return e;
    }
}
