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

package org.apache.avalon.activation.appliance.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.ApplianceException;
import org.apache.avalon.activation.appliance.ApplianceRuntimeException;
import org.apache.avalon.activation.appliance.Engine;

import org.apache.avalon.composition.data.ServiceDirective;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.ServiceModel;
import org.apache.avalon.composition.runtime.Resolver;

import org.apache.avalon.framework.logger.Logger;

/**
 * The DefaultBlock is responsible for the management 
 * of the assembly of the subsidiary appliances, the coordination
 * of the deployment, decommissioning and eventual dissassembly of 
 * contained appliances, and the overall management of a containment 
 * context.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.13 $ $Date: 2004/02/06 15:27:13 $
 */
public class DefaultBlock extends AbstractBlock implements Resolver
{
    //-------------------------------------------------------------------
    // immmutable state
    //-------------------------------------------------------------------

    private final Object m_proxy;

    //-------------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------------

   /**
    * Creation of a new root block.
    *
    * @param model the root containment model
    */
    public DefaultBlock( ContainmentModel model )
    {
        this( model, null );
    }

   /**
    * Creation of a new block.
    *
    * @param context the block context
    */
    DefaultBlock( ContainmentModel model, Engine engine )
    {
        super( model, engine );

        //
        // build the default proxy
        //

        try
        {
            final Logger log = model.getLogger().getChildLogger( "proxy" );
            final BlockInvocationHandler handler = 
              new BlockInvocationHandler( log, this );
            final Class[] classes = getInterfaceClasses();
            
            m_proxy = Proxy.newProxyInstance( 
              model.getClassLoaderModel().getClassLoader(),
              classes,
              handler );
        }
        catch( Throwable e )
        {
            final String error = 
              "Composite service establishment failure in block: " + this;
            throw new ApplianceRuntimeException( error, e );
        }
    }

    //-------------------------------------------------------------------
    // Resolver
    //-------------------------------------------------------------------

    /**
     * Resolve a object to a value.
     *
     * @return the resolved object
     * @throws Exception if an error occurs
     */
    public Object resolve() throws Exception
    {
        return m_proxy;
    }

    /**
     * Release an object
     *
     * @param instance the object to be released
     */
    public void release( Object instance )
    {
        //
        // container proxy is a singleton reference
        //
    }

    //-------------------------------------------------------------------
    // implementation
    //-------------------------------------------------------------------

    private Class[] getInterfaceClasses() throws Exception
    {
        ContainmentModel model = getContainmentModel();
        ClassLoader loader = model.getClassLoaderModel().getClassLoader();
        ArrayList list = new ArrayList();
        ServiceModel[] services = model.getServiceModels();
        for( int i=0; i<services.length; i++ )
        {
            final ServiceModel service = services[i];
            list.add( service.getServiceClass() );
        }
        return (Class[]) list.toArray( new Class[0] );
    }

   /**
    * This makes a dynamic proxy for an object.  The object can be represented
    * by one, some or all of it's interfaces.
    */
    final class BlockInvocationHandler
        implements InvocationHandler
    {
        private final DefaultBlock m_block;
        private final Logger m_logger;

       /**
        * Create a proxy invocation handler.
        *
        * @param block the underlying block implementation
        * @exception if an invocation handler establishment error occurs
        */
        protected BlockInvocationHandler( final Logger logger, 
                                          final DefaultBlock block )
            throws Exception
        {
            if( block == null ) 
              throw new NullPointerException( "block" );
            m_block = block;
            m_logger = logger;
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
            if( proxy == null ) throw new NullPointerException( "proxy" );
            if( method == null ) throw new NullPointerException( "method" );

            //
            // if the invocation is against java.lang.Object then
            // delegate the operation to the block
            //

            final ContainmentModel model = getContainmentModel();
            Class source = method.getDeclaringClass();
            ServiceModel service = model.getServiceModel( source );
            if( null == service )
            {
                final String error = 
                 "Unable to resolve an provider for the class ["
                 + source.getName() 
                 + "].";
                throw new IllegalStateException( error );
            }

            Appliance provider = 
              (Appliance) m_block.locate( 
                service.getServiceProvider() );
            if( m_logger.isDebugEnabled() )
                m_logger.debug( "delegating: " +  method.getName() );

            //
            // resolve the service object from the appliance
            // and delegate the invocation to that provider
            //

            try
            {
                Object object = provider.resolve();
                return method.invoke( object, args );
            }
            catch( UndeclaredThrowableException e )
            {
                Throwable cause = e.getUndeclaredThrowable();
                if( cause != null ) throw cause;
                final String error = 
                  "Delegation error raised by: " + m_block;
                throw new ApplianceException( error, e );
            }
            catch( InvocationTargetException e )
            {
                Throwable cause = e.getTargetException();
                if( cause != null ) throw cause;
                final String error = 
                  "Delegation error raised by: " + m_block;
                throw new ApplianceException( error, e );
            }
            catch( Throwable e )
            {
                final String error =
                  "Composite service resolution failure for the class: '" 
                  + method.getDeclaringClass()
                  + "' for operation: '" + method.getName()
                  + "' in appliance: " + m_block;
                throw new ApplianceException( error, e );
            }
        }
    }
}
