package tutorial;

import java.io.File;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.activity.Initializable;

/**
 * HelloComponent from Merlin's Tutorial
 *
 * @avalon.component version="1.0" name="hello"
 * @author David BERNARD mailto:dwayne@java-fan.com
 */
public class HelloComponent extends AbstractLogEnabled implements
Contextualizable, Serviceable, Configurable, Initializable 
{
    private String m_source = "undefined";
    private File m_home = null;
    private File m_temp = null;
    private String m_name = "unknown";
    private String m_partition = "unknown";

   /**
    * Contextualization of the component by the container.
    * The context supplied by the container holds the
    * Merlin standard context entries for the home and
    * working directories, component name and partition.
    *
    * @avalon.meta.context type="tutorial.DemoContext"
    * @avalon.meta.entry key="urn:avalon:name" 
    * @avalon.meta.entry key="urn:avalon:partition" 
    * @avalon.meta.entry key="urn:avalon:home" type="java.io.File"
    * @avalon.meta.entry key="urn:avalon:temp" type="java.io.File"
    * @avalon.meta.entry key="cruncher" type="tutorial.NumberCruncher" optional="true"
    */
    public void contextualize( Context context ) throws ContextException
    {
        // from tutorial/003

        try
        {
            m_home = (File) context.get( "urn:avalon:home" );
            m_temp = (File) context.get( "urn:avalon:temp" );
            m_name = (String) context.get( "urn:avalon:name" );
            m_partition = (String) context.get( "urn:avalon:partition" );

            StringBuffer buffer = new StringBuffer( "standard entries" );
            buffer.append( "\n  name: " + m_name );
            buffer.append( "\n  home: " + m_home );
            buffer.append( "\n  temp: " + m_temp );
            buffer.append( "\n  partition: " + m_partition );
            getLogger().info( buffer.toString() );
        }
        catch( ContextException e )
        {
            getLogger().error( "standard context entry error", e );
        }

        // from tutorial/005
        try 
        {
            DemoContext c = (DemoContext) context;
            StringBuffer buffer = new StringBuffer( "domain entries" );
            buffer.append( "\n  name: " + c.getName() );
            buffer.append( "\n  home: " + c.getHomeDirectory() );
            buffer.append( "\n  temp: " + c.getWorkingDirectory() );
            buffer.append( "\n  partition: " + c.getPartition() );
            getLogger().info( buffer.toString() );
        } 
        catch (ClassCastException exc)
        {
            getLogger().warn( "not a DemoContext" );
        }

        // from tutorial/004
        try 
        {
            NumberCruncher cruncher = 
             (NumberCruncher) context.get( "cruncher" );
            getLogger().info( "result: " + cruncher.crunch());
        } 
        catch(ContextException exc)
        {
            getLogger().warn( "no cruncher" );
        }
    }

   /**
    * Servicing of the component by the container during
    * which service dependencies declared under the component
    * can be resolved using the supplied service manager.
    *
    * @param manager the service manager
    * @avalon.meta.dependency key="random" type="tutorial.RandomGenerator"
    */
    public void service( ServiceManager manager ) throws ServiceException
    {
        RandomGenerator random = 
          (RandomGenerator) manager.lookup( "random" );
        getLogger().info( "random: " + random.getRandom() );
    }

   /**
    * Configuration of the component by the container.  The
    * implementation get a child element named 'source' and
    * assigns the value of the element to a local variable.
    *
    * @param config the component configuration
    * @exception ConfigurationException if a configuration error occurs
    */
    public void configure( Configuration config ) throws
    ConfigurationException    {
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

