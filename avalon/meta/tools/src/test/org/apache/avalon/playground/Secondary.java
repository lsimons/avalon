
package org.apache.avalon.playground;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;

/**
 * @avalon.component name="secondary-component" version="2.4"
 * @avalon.service type="org.apache.avalon.playground.SecondaryService" version="0.1"
 */
public class Secondary extends AbstractLogEnabled implements Serviceable, SecondaryService
{
    private Logger m_system = null;

    private PrimaryService m_primary = null;

   /**
    * Supply of a logging channel to the component.
    *
    * @param logger the logging channel
    * @avalon.logger name="system"
    */
    public void enableLogging( Logger logger )
    {
        super.enableLogging( logger );
        m_system = logger.getChildLogger( "system" );
    }

   /**
    * Supply of dependent services to this component by the container.
    *
    * @param manager the service manager
    * @avalon.dependency key="primary" 
    *     type="org.apache.avalon.playground.PrimaryService" 
    *     version="1.3" 
    */
    public void service( ServiceManager manager ) throws ServiceException
    {
        m_primary = (PrimaryService) manager.lookup( "primary" );
        m_system.info( "resolved primary service reference" );
    }
}