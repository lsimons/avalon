package tutorial;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;

public class HelloComponent extends AbstractLogEnabled 
  implements Serviceable
{

   /**
    * Servicing of the component by the container during 
    * which service dependencies declared under the component
    * can be resolved using the supplied service manager.
    */
    public void service( ServiceManager manager )
      throws ServiceException
    {
        RandomGenerator random = (RandomGenerator) manager.lookup( "random" );
        getLogger().info( "random: " + random.getRandom() );
    }
}
