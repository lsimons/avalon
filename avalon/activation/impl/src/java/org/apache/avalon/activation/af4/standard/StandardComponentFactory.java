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

import java.lang.reflect.Proxy;

import org.apache.avalon.activation.af4.DefaultAppliance;
import org.apache.avalon.activation.af4.DefaultLifecycleCreateImpl;
import org.apache.avalon.activation.af4.DefaultLifecycleDestroyImpl;

import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.lifecycle.CreationException;
import org.apache.avalon.activation.lifecycle.DestructionException;
import org.apache.avalon.activation.lifecycle.Factory;
import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;

import org.apache.avalon.meta.info.Type;
import org.apache.avalon.meta.info.InfoDescriptor;

public class StandardComponentFactory 
    implements Factory, LogEnabled
{
    //-------------------------------------------------------------------
    // static
    //-------------------------------------------------------------------

    private static final Resources REZ =
      ResourceManager.getPackageResources( StandardComponentFactory.class );
    
    private Logger m_logger;
    private DefaultAppliance m_appliance;
    private DefaultLifecycleCreateImpl m_creationLifecycle;
    private DefaultLifecycleDestroyImpl m_destructionLifecycle;
    
    public StandardComponentFactory( DefaultAppliance appliance )
    {
        m_creationLifecycle = 
          new DefaultLifecycleCreateImpl();
        m_destructionLifecycle =
          new DefaultLifecycleDestroyImpl();
    }
    
    public void enableLogging( Logger logger )
    {
        m_logger = logger;
    }
    
   /**
    * Return the component deployment model. 
    */
    public ComponentModel getComponentModel()
    {
        return m_appliance.getComponentModel();
    }

   /**
    * Create a new instance of a component. 
    *
    * @exception CreationException
    */
    public Object newInstance() 
        throws CreationException
    {
        Class clazz = getComponentModel().getDeploymentClass();
        Object instance = null;
        try
        {
            instance = createNewInstance( clazz );
            if( getLogger().isDebugEnabled() )
            {
                int id = System.identityHashCode( instance );
                getLogger().debug( "new instance: " + id );
            }

            if( getLogger().isDebugEnabled() )
            {
                int id = System.identityHashCode( instance );
                getLogger().debug( "established: " + id );
            }
        }
        catch( RuntimeException e )
        {
            final String error = 
              REZ.getString( "lifestyle.new.error", getComponentModel().getQualifiedName() );
            throw new CreationException( error, e );
        }

        try
        {
            Object appliance = createProvider( instance );
            m_creationLifecycle.create( 
                m_appliance.getEngine(),
                getComponentModel(),
                instance
            );
            return appliance;
        }
        catch( RuntimeException e )
        {
            getLogger().error( e.getMessage() );
            final String error = 
              "Provider publication failure.";
            throw new CreationException( error, e );
        }
    }

   /**
    * Decommission and dispose of the supplied component. 
    *
    * @param instance the object to decommission
    */
    
    public void destroy( Object instance )
        throws DestructionException
    {
        if( instance == null ) 
            throw new NullPointerException( "instance" );
        if( Proxy.isProxyClass( instance.getClass() ) )
        {
            StandardApplianceInvocationHandler handler = 
              (StandardApplianceInvocationHandler) Proxy.getInvocationHandler( instance );
            handler.notifyDestroyed();
        }
        m_destructionLifecycle.destroy( 
            m_appliance.getEngine(), 
            m_appliance.getComponentModel(), 
            instance );
    }

    private Object createNewInstance( Class clazz )
      throws CreationException
    {
        try
        {
            return clazz.newInstance();
        }
        catch( Exception e )
        {
            final String error = 
              REZ.getString( 
                "lifecycle.instantiation.error", clazz.getName() );
            throw new CreationException( error, e );
        }
    }

    private Object getProviderInstance( Object instance )
    {
        if( Proxy.isProxyClass( instance.getClass() ) )
        {
            StandardApplianceInvocationHandler handler = 
              (StandardApplianceInvocationHandler) Proxy.getInvocationHandler( instance );
            return handler.getInstance();
        }
        else
        {
            return instance;
        }
    }

    private Object createProvider( Object instance ) 
        throws CreationException
    {
        ComponentModel model = getComponentModel();
        Type type = model.getType();
        InfoDescriptor info = type.getInfo();
        String attribute = info.getAttribute( "urn:activation:proxy", "true" );
        if( attribute.equals( "false" ) )
        {
            return instance;
        }

        Class[] classes = getComponentModel().getInterfaces();
        try
        {
            StandardApplianceInvocationHandler handler = 
              new StandardApplianceInvocationHandler( 
                getLogger(), m_appliance, instance );
            return Proxy.newProxyInstance( 
              getComponentModel().getDeploymentClass().getClassLoader(),
              classes,
              handler );
        }
        catch( RuntimeException e )
        {
            final String error = 
              "Proxy establishment failure in appliance: " + this;
            throw new CreationException( error, e );
        }
    }

    private Logger getLogger()
    {
        return m_logger;
    }
}
 
