
package tutorial;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * A configurable component.
 *
 * @avalon.component version="1.0" name="simple"
 */
public class HelloComponent extends AbstractLogEnabled 
  implements Configurable, Initializable
{
    private String m_source = "undefined";

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
        m_source = config.getChild( "source" ).getValue( "unknown" );
    }

   /**
    * Initialization of the component by the container.
    * @exception Exception if an initialization error occurs
    */
    public void initialize() throws Exception
    {
        getLogger().info( "initialization stage" );
        final String message = 
          "source: " + m_source;
        getLogger().info( message );
    }
}
