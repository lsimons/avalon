
package tutorial;

import java.io.File;

import org.apache.avalon.framework.context.Context;

/**
 * An example of an convinience interface that extends the 
 * standard Avalon Context interface.
 */
public interface DemoContext extends Context
{

   /**
    * Return the component name.
    * @return the component name
    */
    String getName();

   /**
    * Return the name of the partition assigned to the component.
    * @return the partition name
    */
    String getPartition();

   /**
    * Return the home directory.
    * @return the directory
    */
    File getHomeDirectory();

   /**
    * Return the temporary working directory.
    * @return the directory
    */
    File getWorkingDirectory();
}
