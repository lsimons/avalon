
package tutorial;

import java.util.Map;
import java.io.File;

import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.context.ContextException;


/**
 * A demonstration class that that we will instantiate via 
 * context directives within the component declaration.
 */
public class DemoContextProvider extends DefaultContext implements DemoContext
{

   /**
    * A custom context type implementation must provide
    * the following constructor.
    * @param entries a map of context entries
    */
    public DemoContextProvider( Map entries )
    {
        super( entries );
    }
 
   /**
    * Return the component name.
    * @return the component name
    */
    public String getName()
    {
        try
        {
            return (String) super.get( "urn:avalon:name" );
        }
        catch( ContextException ce )
        {
            // should not happen 
            throw new RuntimeException( ce.toString() );
        }
    }

   /**
    * Return the name of the partition assigned to the component.
    * @return the partition name
    */
    public String getPartition()
    {
        try
        {
            return (String) super.get( "urn:avalon:partition" );
        }
        catch( ContextException ce )
        {
            // should not happen 
            throw new RuntimeException( ce.toString() );
        }
    }

   /**
    * Return the home directory.
    * @return the home directory
    */
    public File getHomeDirectory()
    {
        try
        {
            return (File) super.get( "urn:avalon:home" );
        }
        catch( ContextException ce )
        {
            // should not happen 
            throw new RuntimeException( ce.toString() );
        }
    }


   /**
    * Return the temporary working directory.
    * @return the temp directory
    */
    public File getWorkingDirectory()
    {
        try
        {
            return (File) super.get( "urn:avalon:temp" );
        }
        catch( ContextException ce )
        {
            // should not happen 
            throw new RuntimeException( ce.toString() );
        }
    }
}
