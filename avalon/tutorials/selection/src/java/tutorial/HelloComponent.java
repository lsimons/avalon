package tutorial;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;

/**
 * Demonstration component that will be provided with a 
 * service established via a named profile.
 * @avalon.component name="hello" lifestyle="singleton"
 */
public class HelloComponent extends AbstractLogEnabled 
  implements Serviceable
{

   /**
    * Servicing of the component by the container during 
    * which service dependencies declared under the component
    * can be resolved using the supplied service manager.
    *
    * @avalon.dependency type="tutorial.RandomGenerator" key="random"
    */
    public void service( ServiceManager manager )
      throws ServiceException
    {
        RandomGenerator random = (RandomGenerator) manager.lookup( "random" );
        getLogger().info( "supplied random: " + random.getRandom() );
    }
}
