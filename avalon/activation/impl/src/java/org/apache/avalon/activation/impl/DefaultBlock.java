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

package org.apache.avalon.activation.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;

import org.apache.avalon.activation.Appliance;
import org.apache.avalon.activation.ApplianceException;
import org.apache.avalon.activation.ApplianceRuntimeException;

import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.ServiceModel;
import org.apache.avalon.composition.provider.SystemContext;
import org.apache.avalon.composition.util.DefaultState;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.Logger;

/**
 * Composite appliance.
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/02/10 16:19:15 $
 */
public class DefaultBlock extends AbstractAppliance
{
    //-------------------------------------------------------------------
    // static
    //-------------------------------------------------------------------

    private static final Resources REZ =
      ResourceManager.getPackageResources( 
        DefaultBlock.class );

    //-------------------------------------------------------------------
    // immutable state
    //-------------------------------------------------------------------

    private final ContainmentModel m_model;

    private final SystemContext m_system;

    private final DefaultState m_commissioned = new DefaultState();

    //-------------------------------------------------------------------
    // mutable state
    //-------------------------------------------------------------------

    private Object m_proxy;

    //-------------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------------

    public DefaultBlock( SystemContext system, ContainmentModel model )
      throws ApplianceRuntimeException
    {
        super( model );

        m_model = model;
        m_system = system;
    }

    //-------------------------------------------------------------------
    // Commissionable
    //-------------------------------------------------------------------

   /**
    * Commission the appliance. 
    *
    * @exception Exception if a commissioning error occurs
    */
    public void commission() throws Exception
    {
        synchronized( m_commissioned )
        {
            if( m_commissioned.isEnabled() ) return;
            try
            {
                final Logger log = m_model.getLogger().getChildLogger( "proxy" );
                final BlockInvocationHandler handler = 
                  new BlockInvocationHandler( log, this );
                final Class[] classes = getInterfaceClasses();
            
                m_proxy = Proxy.newProxyInstance( 
                  m_model.getClassLoaderModel().getClassLoader(),
                  classes,
                  handler );

                m_commissioned.setEnabled( true );
            }
            catch( Throwable e )
            {
                final String error = 
                  "Composite service establishment failure in block: " + this;
                throw new ApplianceRuntimeException( error, e );
            }
        }
    }

   /**
    * Decommission the appliance.  Once an appliance is 
    * decommissioned it may be re-commissioned.
    */
    public void decommission()
    {
        synchronized( m_commissioned )
        {
            if( !m_commissioned.isEnabled() ) return;
            if( null != m_proxy )
            {
                m_proxy = null;
            }
            m_commissioned.setEnabled( false );
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
        if( !m_commissioned.isEnabled() )
        {
            final String error = 
              REZ.getString( 
                "block.error.resolve.non-commission-state", 
                this.toString() );
            throw new IllegalStateException( error );
        }
        return m_proxy;
    }

    /**
     * Release an object
     *
     * @param instance the object to be released
     */
    public void release( Object instance )
    {
        // ignore
    }

    //-------------------------------------------------------------------
    // implementation
    //-------------------------------------------------------------------

    /**
     * Return the model backing the handler.
     * @return the type that the appliance is managing
     */
    protected ContainmentModel getContainmentModel()
    {
        return m_model;
    }

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
            {
                throw new NullPointerException( "block" );
            }

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

            DeploymentModel provider = service.getServiceProvider();

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


    //-------------------------------------------------------------------
    // Object
    //-------------------------------------------------------------------

    public String toString()
    {
        return "block:" + getContainmentModel().getQualifiedName();
    }
}
