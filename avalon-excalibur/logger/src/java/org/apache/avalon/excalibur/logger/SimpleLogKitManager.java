/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.avalon.excalibur.logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.AvalonFormatter;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.log.Priority;
import org.apache.log.output.io.FileTarget;

/**
 * A {@link LoggerManager} that supports the old &lt;logs version="1.0"/&gt;
 * style logging configuration from
 * <a href="http://jakarta.apache.org/avalon/phoenix">Phoenix</a>.
 *
 * @author <a href="mailto:colus@isoft.co.kr">Eung-ju Park</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public class SimpleLogKitManager
    extends AbstractLogEnabled
    implements LoggerManager, Contextualizable, Configurable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( SimpleLogKitManager.class );

    private static final String DEFAULT_FORMAT =
        "%7.7{priority} %23.23{time:yyyy-MM-dd' 'HH:mm:ss.SSS} [%8.8{category}] (%{context}): "
        + "%{message}\n%{throwable}";

    ///Base directory of applications working directory
    private File m_baseDirectory;

    /**
     *  Hierarchy of Application logging
     */
    private final Hierarchy m_hierarchy = new Hierarchy();

    /**
     * The root logger in hierarchy.
     */
    private final Logger m_logkitLogger = m_hierarchy.getLoggerFor( "" );

    /**
     * The root logger wrapped using AValons Logging Facade.
     */
    private org.apache.avalon.framework.logger.Logger m_logger =
        new LogKitLogger( m_logkitLogger );

    /**
     * Contextualize the manager. Requires that the "app.home" entry
     * be set to a File object that points at the base directory for logs.
     *
     * @param context the context
     * @throws ContextException if missing context entry
     */
    public void contextualize( final Context context )
        throws ContextException
    {
        m_baseDirectory = (File)context.get( "app.home" );
    }

    /**
     * Interpret configuration to build loggers.
     *
     * @param configuration the configuration
     * @throws ConfigurationException if malformed configuration
     */
    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration[] targets = configuration.getChildren( "log-target" );
        final HashMap targetSet = configureTargets( targets );
        final Configuration[] categories = configuration.getChildren( "category" );
        configureCategories( categories, targetSet );
    }

    /**
     * Retrieve a logger by name.
     *
     * @param name the name of logger
     * @return the specified Logger
     */
    public org.apache.avalon.framework.logger.Logger
        getLoggerForCategory( final String name )
    {
        return m_logger.getChildLogger( name );
    }

    /**
     * Retrieve the root logger.
     *
     * @return the root Logger
     */
    public org.apache.avalon.framework.logger.Logger getDefaultLogger()
    {
        return m_logger;
    }

    /**
     * Configure a set of logtargets based on config data.
     *
     * @param targets the target configuration data
     * @return a Map of target-name to target
     * @throws ConfigurationException if an error occurs
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
                logTarget = new FileTarget( file.getAbsoluteFile(), true, formatter );
            }
            catch( final IOException ioe )
            {
                final String message =
                    REZ.getString( "target.nocreate", name, file, ioe.getMessage() );
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
     * @throws ConfigurationException if an error occurs
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

            final Logger logger =
                m_logkitLogger.getChildLogger( name );

            final LogTarget logTarget = (LogTarget)targets.get( target );
            if( null == target )
            {
                final String message = REZ.getString( "unknown-target", target, name );
                throw new ConfigurationException( message );
            }

            final Priority priority = Priority.getPriorityForName( priorityName );
            if( !priority.getName().equals( priorityName ) )
            {
                final String message = REZ.getString( "unknown-priority", priorityName, name );
                throw new ConfigurationException( message );
            }

            if( getLogger().isDebugEnabled() )
            {
                final String message =
                    REZ.getString( "category-create", name, target, priorityName );
                getLogger().debug( message );
            }

            if( name.equals( "" ) )
            {
                //TODO: Use m_logkitLogger instead
                m_hierarchy.setDefaultPriority( priority );
                m_hierarchy.setDefaultLogTarget( logTarget );
            }
            else
            {
                logger.setPriority( priority );
                logger.setLogTargets( new LogTarget[]{logTarget} );
            }
        }
    }
}
