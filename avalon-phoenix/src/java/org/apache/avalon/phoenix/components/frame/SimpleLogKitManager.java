package org.apache.avalon.phoenix.components.frame;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.logger.AvalonFormatter;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.logger.LogKitManager;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.log.Priority;
import org.apache.log.output.io.FileTarget;

/**
 * @author <a href="mailto:colus@isoft.co.kr">Eung-ju Park</a>
 */
public class SimpleLogKitManager
    extends AbstractLoggable
    implements LogKitManager, Contextualizable, Configurable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultPolicy.class );

    private final static String  DEFAULT_FORMAT =
        "%{time} [%7.7{priority}] <<%{category}>> (%{context}): %{message}\\n%{throwable}";

    ///Base directory of applications working directory
    private File         m_baseDirectory;

    ///Hierarchy of Application logging
    private Hierarchy    m_logHierarchy    = new Hierarchy();

    public void contextualize( final Context context )
        throws ContextException
    {
        m_baseDirectory = (File)context.get( "app.home" );
    }

    public void configure( final Configuration conf )
        throws ConfigurationException
    {
        final Configuration[] targets = conf.getChildren( "log-target" );
        final HashMap targetSet = configureTargets( targets );
        final Configuration[] categories = conf.getChildren( "category" );
        configureCategories( categories, targetSet );
    }

    /**
     * Configure a set of logtargets based on config data.
     *
     * @param targets the target configuration data
     * @return a Map of target-name to target
     * @exception ConfigurationException if an error occurs
     */
    private HashMap configureTargets( final Configuration[] targets )
        throws ConfigurationException
    {
        final HashMap targetSet = new HashMap();

        for( int i = 0; i < targets.length; i++ )
        {
            final Configuration target = targets[ i ];
            final String name = target.getAttribute( "name" );
            String location = target.getAttribute( "location" ).trim();
            final String format = target.getAttribute( "format", DEFAULT_FORMAT );

            if( '/' == location.charAt( 0 ) )
            {
                location = location.substring( 1 );
            }

            final AvalonFormatter formatter = new AvalonFormatter( format );

            //Specify output location for logging
            final File file = new File( m_baseDirectory, location );

            //Setup logtarget
            FileTarget logTarget = null;
            
            try
            {
                logTarget = new FileTarget( file.getAbsoluteFile(), false, formatter );
            }
            catch( final IOException ioe )
            {
                final String message = REZ.getString( "frame.error.log.create", file );
                throw new ConfigurationException( message, ioe );
            }

            targetSet.put( name, logTarget );
        }

        return targetSet;
    }

    /**
     * Configure Logging categories.
     *
     * @param categories configuration data for categories
     * @param targets a hashmap containing the already existing taregt
     * @exception ConfigurationException if an error occurs
     */
    private void configureCategories( final Configuration[] categories, final HashMap targets )
        throws ConfigurationException
    {
        for( int i = 0; i < categories.length; i++ )
        {
            final Configuration category = categories[ i ];
            final String name = category.getAttribute( "name", "" );
            final String target = category.getAttribute( "target" );
            final String priorityName = category.getAttribute( "priority" );

            final Logger logger = getLogger( name );

            final LogTarget logTarget = (LogTarget)targets.get( target );
            if( null == target )
            {
                final String message = REZ.getString( "frame.error.target.locate", target );
                throw new ConfigurationException( message );
            }

            final Priority priority = Priority.getPriorityForName( priorityName );
            if( !priority.getName().equals( priorityName ) )
            {
                final String message = REZ.getString( "frame.error.priority.unknown", priorityName );
                throw new ConfigurationException( message );
            }

            if( name.equals( "" ) )
            {
                m_logHierarchy.setDefaultPriority( priority );
                m_logHierarchy.setDefaultLogTarget( logTarget );
            }
            else
            {
                logger.setPriority( priority );
                logger.setLogTargets( new LogTarget[] { logTarget } );
            }
        }
    }

    public Logger getLogger( final String category )
    {
        return m_logHierarchy.getLoggerFor( category );
    }
}
