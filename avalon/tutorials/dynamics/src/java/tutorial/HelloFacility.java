package tutorial;

import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.DeploymentModel;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.activity.Executable;

import org.apache.avalon.meta.info.ReferenceDescriptor;

/**
 * A demonstration of a facility that invoces the runtime creation
 * and deployment of other components.
 *
 * @avalon.component name="hello" lifestyle="singleton"
 */
public class HelloFacility 
  implements Contextualizable, Executable
{
   //---------------------------------------------------------
   // immutable state
   //---------------------------------------------------------

  /**
   * The logging channel supplied by the container.
   */
   private final Logger m_logger;

   //---------------------------------------------------------
   // mutable state
   //---------------------------------------------------------

  /**
   * The containment model (establish via contexualization)
   */
   private ContainmentModel m_model;

   //---------------------------------------------------------
   // constructor
   //---------------------------------------------------------

  /**
   * Creation of a new hello facility.
   * @param logger a logging channel
   */
   public HelloFacility( Logger logger )
   {
       m_logger = logger;
   }

   //---------------------------------------------------------
   // Contextualizable
   //---------------------------------------------------------

  /**
   * Contextulaization of the facility by the container during 
   * which we are supplied with the root containment model.
   *
   * @param context the supplied context
   * @avalon.entry key="urn:composition:containment.model" 
   *    type="org.apache.avalon.composition.model.ContainmentModel" 
   * @exception ContextException if a contextualization error occurs
   */
   public void contextualize( Context context ) throws ContextException
   {
       m_model = 
         (ContainmentModel) context.get( 
           "urn:composition:containment.model" );
   }

   //---------------------------------------------------------
   // Executable
   //---------------------------------------------------------

   public void execute() throws Exception
   {
       //
       // create a reference to the widget service
       //

       ReferenceDescriptor reference = new ReferenceDescriptor( Widget.class.getName() );

       //
       // get hold of a model representing a widget deployment scenario
       //

       DeploymentModel model = m_model.getModel( reference );
       getLogger().info( "got the widget model: " + model );

       //
       // commission the model and resolve a component instance
       //

       model.commission();
       Widget widget = (Widget) model.resolve();
       getLogger().info( "got the widget instance: " + widget );
   }

   //---------------------------------------------------------
   // private
   //---------------------------------------------------------

   private Logger getLogger()
   {
       return m_logger;
   }
}
