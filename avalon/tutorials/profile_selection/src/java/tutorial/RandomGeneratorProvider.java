
package tutorial;

import java.util.Random;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * An implementation of a random number generator.
 */
public class RandomGeneratorProvider extends AbstractLogEnabled 
  implements Configurable, RandomGenerator
{

    private Random m_random = null;

   /**
    * Configuration of the component by the container.  The 
    * implementation get a child element named 'source' and 
    * assigns the value of the element to a local variable.
    *
    * @param config the component configuration
    * @exception ConfigurationException if a configuration error occurs
    */
    public void configure( Configuration config ) throws ConfigurationException
    {
        getLogger().info( "configuration stage" );
        long seed = config.getChild( "seed" ).getValueAsLong( 0 );
        getLogger().info( "seed: " + seed );
        m_random = new Random( System.currentTimeMillis() * seed );
    }

   /**
    * Return a random integer
    * @return the random number
    */
    public int getRandom()
    {
        return m_random.nextInt();
    }
}
