

package org.apache.avalon.composition.model.testc;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;

//
// TODO check why we are importing and implementing Serviceable
// but its not in the class definition
//

public class TestC extends AbstractLogEnabled
  implements Initializable, C
{
    public void initialize() throws Exception
    {
        getLogger().info( "hello from C" );
    }

    public void service( ServiceManager manager ) throws ServiceException
    {
        getLogger().info( "service stage" );
        Logger logger = getLogger().getChildLogger( "service" );
        logger.info( "lookup A" );
        manager.lookup( "a" );
        logger.info( "lookup A2" );
        manager.lookup( "a2" );
        logger.info( "lookup B" );
        manager.lookup( "b" );
        logger.info( "ok" );
    }
}
