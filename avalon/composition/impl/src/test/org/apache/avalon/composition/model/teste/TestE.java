

package org.apache.avalon.composition.model.teste;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;

//
// TODO check why we are importing and implementing Serviceable
// but its not in the class definition
//

public class TestE extends AbstractLogEnabled
  implements Initializable, E
{
    public void initialize() throws Exception
    {
        getLogger().info( "hello from E" );
    }

    public void service( ServiceManager manager ) throws ServiceException
    {
        getLogger().info( "service stage" );
        Logger logger = getLogger().getChildLogger( "service" );
        logger.info( "lookup A" );
        manager.lookup( "a" );
        logger.info( "ok" );
    }
}
