package tutorial;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * A component that implements the Gizmo service.
 *
 * @avalon.component name="gizmo" lifestyle="singleton"
 * @avalon.service type="tutorial.Gizmo"
 */
public class DefaultGizmo implements Gizmo, Configurable
{
   //---------------------------------------------------------
   // immutable state
   //---------------------------------------------------------

  /**
   * The logging channel supplied by the container.
   */
   private final Logger m_logger;

   //---------------------------------------------------------
   // constructor
   //---------------------------------------------------------

  /**
   * Creation of a new hello facility.
   * @param logger a logging channel
   */
   public DefaultGizmo( Logger logger )
   {
       m_logger = logger;
       m_logger.info( "I've been created" );
   }

   //---------------------------------------------------------
   // configurable
   //---------------------------------------------------------

   /**
    * Configuration of the gizmo by the container.
    * @param config the supplied configuration
    */
    public void configure( Configuration config ) throws ConfigurationException
    {
        final String message = config.getChild( "message" ).getValue( "" );
        m_logger.info( "I've been configured with the message: [" + message + "]" );
    }
}
