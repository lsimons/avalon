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

import java.lang.reflect.Proxy;

import org.apache.avalon.activation.ApplianceException;
import org.apache.avalon.activation.LifestyleManager;

import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.util.DefaultState;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.Logger;

/**
 * Abstract appliance.
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/02/10 16:19:15 $
 */
public class DefaultAppliance extends AbstractAppliance
{
    //-------------------------------------------------------------------
    // static
    //-------------------------------------------------------------------

    private static final Resources REZ =
      ResourceManager.getPackageResources( 
        DefaultAppliance.class );

    //-------------------------------------------------------------------
    // immutable state
    //-------------------------------------------------------------------

    private final ComponentModel m_model;

    private final LifestyleManager m_lifestyle;

    private final DefaultState m_commissioned = new DefaultState();

    //-------------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------------

    public DefaultAppliance( ComponentModel model, LifestyleManager lifestyle )
    {
        super( model );
        m_model = model;
        m_lifestyle = lifestyle;
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
            m_lifestyle.commission();
            m_commissioned.setEnabled( true );
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
            m_lifestyle.decommission();
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
                "appliance.error.resolve.non-commission-state", 
                this.toString() );
            throw new IllegalStateException( error );
        }

        if( getComponentModel().getType().getInfo().
              getAttribute( "urn:activation:proxy", "true" ).equals( "false" ) )
        {
            return m_lifestyle.resolve();
        }
        else        
        {
            ComponentModel model = getComponentModel();
            Logger logger = model.getLogger().getChildLogger( "proxy" );
            ApplianceInvocationHandler handler = 
              new ApplianceInvocationHandler( this, logger, m_lifestyle.resolve() );

            try
            {
                return Proxy.newProxyInstance( 
                  model.getDeploymentClass().getClassLoader(),
                  model.getInterfaces(),
                  handler );
            }
            //catch( AccessControlException e )
            //{
            //    Permission p = e.getPermission();
            //    if( null != p )
            //    {
            //        final String warning = 
            //          "Proxy creation disabled due to insufficient permission: [" 
            //          + p.getName()
            //          + "].";
            //        getLogger().warn( warning );
            //    }
            //    else
            //    {
            //        final String warning = 
            //          "Proxy creation disabled due to access control restriction."; 
            //        getLogger().warn( warning );
            //    }
            //}
            catch( Throwable e )
            {
                final String error = 
                  "Proxy establishment failure in block: " + this;
                throw new ApplianceException( error, e );
            }
        }
    }

    /**
     * Release an object
     *
     * @param instance the object to be released
     */
    public void release( Object instance )
    {
        if( null == instance ) return;
        if( !m_commissioned.isEnabled() ) return;
        if( Proxy.isProxyClass( instance.getClass() ) )
        {
            ApplianceInvocationHandler handler = 
              (ApplianceInvocationHandler) Proxy.getInvocationHandler( instance );
            handler.release();
        }
        else
        {
            m_lifestyle.release( instance );
        }
    }

    //-------------------------------------------------------------------
    // implementation
    //-------------------------------------------------------------------

    /**
     * Return the model backing the handler.
     * @return the type that the appliance is managing
     */
    protected ComponentModel getComponentModel()
    {
        return m_model;
    }

    //-------------------------------------------------------------------
    // Object
    //-------------------------------------------------------------------

    public String toString()
    {
        return "appliance:" + getComponentModel().getQualifiedName();
    }
}