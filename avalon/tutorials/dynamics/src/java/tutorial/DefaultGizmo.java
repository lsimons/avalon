package tutorial;

import java.io.File;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;

/**
 * A component that implements the Gizmo service.
 *
 * @avalon.component name="gizmo" lifestyle="singleton"
 * @avalon.service type="tutorial.Gizmo"
 */
public class DefaultGizmo implements Gizmo, Contextualizable
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
   // Contextualizable
   //---------------------------------------------------------

   /**
    * Contextualization of the gizmo by the container.
    * @param context the supplied runtime context
    * @avalon.entry key="urn:avalon:home"
    */
    public void contextualize( Context context ) throws ContextException
    {
        File home = (File) context.get( "urn:avalon:home" );
        m_logger.info( "home: " + home );
    }

   //---------------------------------------------------------
   // Object
   //---------------------------------------------------------

   public String toString()
   {
       return "[gizmo:" + System.identityHashCode( this ) + "]";
   }
}
