package tutorial;

import org.apache.avalon.framework.logger.Logger;

/**
 * A component that implements the Gizmo service.
 *
 * @avalon.component name="widget" lifestyle="singleton"
 * @avalon.service type="tutorial.Widget"
 */
public class DefaultWidget implements Widget
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
   public DefaultWidget( Logger logger )
   {
       m_logger = logger;
       m_logger.info( "I've been created" );
   }
}

