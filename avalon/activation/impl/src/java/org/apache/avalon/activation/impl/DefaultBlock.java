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

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.Logger;

import org.apache.avalon.util.i18n.ResourceManager;
import org.apache.avalon.util.i18n.Resources;

/**
 * Composite appliance.
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.3 $ $Date: 2004/03/15 12:58:43 $
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

    //-------------------------------------------------------------------
    // Object
    //-------------------------------------------------------------------

    public String toString()
    {
        return "block:" + getContainmentModel().getQualifiedName();
    }
}
