package tutorial;

import org.apache.avalon.framework.logger.Logger;

/**
 * A component that implements the Gizmo service.
 *
 * @avalon.component name="gizmo" lifestyle="singleton"
 * @avalon.service type="tutorial.Gizmo"
 */
public class DefaultGizmo implements Gizmo
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
}
