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

package org.apache.avalon.finder.ecm;

import org.apache.avalon.finder.Finder;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.configuration.Configuration;

/**
 * A default implementation of a finder service that provides 
 * support for pull-based service activation semantics. The default 
 * implementation deals with activation of standard avalon components
 * (i.e. components that declare semantics using the Avalon Meta 
 * contract).
 *
 * @avalon.component name="ecm" lifestyle="singleton"
 * @avalon.service type="org.apache.avalon.framework.service.ServiceManager"
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/04/04 15:00:55 $
 */
public class ECM implements ServiceManager
{
    //---------------------------------------------------------
    // immutable state
    //---------------------------------------------------------

    /**
     * The logging channel for this component.
     */
    private final Logger m_logger;

    /**
     * The finder that will handle pull-based service activation.
     */
    private final Finder m_finder;

    /**
     * The roled managers.
     */
    private final RoleManager m_manager;

    /**
     * The master configuration.
     */
    private final Configuration m_config;

    //---------------------------------------------------------
    // constructor
    //---------------------------------------------------------

   /**
    * Creation of a new ecm finder.  The finder is supplied 
    * a logging channel and a service manager.  The implementation
    * aquires a classic finder the handle pull based service 
    * aquisition and a role manager to handle key dereferencing.
    * 
    * @param logger the container assigned logging channel
    * @param manager the container assigned service manager
    * @param config the master configuration
    *
    * @avalon.dependency key="finder"
    *    type="org.apache.avalon.finder.Finder" 
    * @avalon.dependency key="roles"
    *    type="org.apache.avalon.finder.ecm.RoleManager" 
    */
    public ECM( 
      final Logger logger, ServiceManager manager, Configuration config ) 
      throws ServiceException
    {
        assertNotNull( logger, "logger" );
        assertNotNull( manager, "manager" );
        assertNotNull( config, "config" );

        m_logger = logger;
        m_config = config;
        m_finder = (Finder) manager.lookup( "finder" );
        m_manager = (RoleManager) manager.lookup( "roles" );

        getLogger().info( "commencing ecm initialization" );

        //
        // What to do here.
        //
        // Possible approach is to read in the roles file
        // and for all of the roles build the equivalent 
        // types and profiles in the model - or should we 
        // do this at runtime?
        //
    }

    //---------------------------------------------------------
    // ServiceManager
    //---------------------------------------------------------

   /**
    * Return true if the supplied key maps to a known 
    * service.
    * 
    * @return TRUE if the key identifies a known service
    */ 
    public boolean hasService( String key )
    {
        throw new UnsupportedOperationException( "hasService" );
    }

    public Object lookup( String key ) throws ServiceException
    {
        throw new UnsupportedOperationException( "lookup" );
    }

    public void release( Object object )
    {
        throw new UnsupportedOperationException( "release" );
    }

    //---------------------------------------------------------
    // private implementation
    //---------------------------------------------------------

    private void assertNotNull( Object object, String key )
    {
        if( null == object )
        {
            throw new NullPointerException( key );
        }
    }

    private Logger getLogger()
    {
        return m_logger;
    }
}
