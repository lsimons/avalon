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
package org.apache.avalon.excalibur.logger.logkit;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.avalon.excalibur.logger.DefaultLogTargetFactoryManager;
import org.apache.avalon.excalibur.logger.DefaultLogTargetManager;
import org.apache.avalon.excalibur.logger.LogTargetFactoryManageable;
import org.apache.avalon.excalibur.logger.LogTargetFactoryManager;
import org.apache.avalon.excalibur.logger.LogTargetManager;
import org.apache.avalon.excalibur.logger.util.LoggerUtil;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Priority;
import org.apache.log.util.Closeable;

/**
 * Tie this object to a LoggerManagerTee, give it the Hierachy
 * that LogKitAdapter operates upon and it will populate it
 * from the Configuration object passed via configure().
 * Note: this class assumes that this is a new Hierarchy,
 * freshly created with new Hierarchy() not populated before.
 *
 * @author <a href="mailto:giacomo@apache.org">Giacomo Pati</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 * @author <a href="http://cvs.apache.org/~atagunov">Anton Tagunov</a>
 * @version CVS $Revision: 1.4 $ $Date: 2003/06/12 18:57:45 $
 * @since 4.0
 */
public class LogKitConfHelper extends AbstractLogEnabled implements
        Contextualizable,
        Configurable,
        Disposable
{
    /* The hierarchy to operate upon */
    private final Hierarchy m_hierarchy;

    /* Creates an instance of LogKitLoggerHelper. */
    public LogKitConfHelper( final Hierarchy hierarchy )
    {
        if ( hierarchy == null ) throw new NullPointerException( "hierarchy" );
        m_hierarchy = hierarchy;
    }

    /** Set of log targets */
    final private Set m_targets = new HashSet();

    /** The context object */
    private Context m_context;

    /**
     * Reads a context object that will be supplied to the log target factory manager.
     *
     * @param context The context object.
     * @throws ContextException if the context is malformed
     */
    public final void contextualize( final Context context )
            throws ContextException
    {
        m_context = context;
    }

    /**
     * Populates the underlying <code>Hierarchy</code>.
     *
     * @param configuration  The configuration object.
     * @throws ConfigurationException if the configuration is malformed
     */
    public final void configure( final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration factories = configuration.getChild( "factories" );
        final LogTargetFactoryManager targetFactoryManager = setupTargetFactoryManager( factories );

        final Configuration targets = configuration.getChild( "targets" );
        final LogTargetManager targetManager = setupTargetManager( targets, targetFactoryManager );

        final Configuration categories = configuration.getChild( "categories" );
        setupLoggers( targetManager,
                      null,
                      categories,
                      true,
                      categories.getAttributeAsBoolean( "additive", false ) );
    }

    /**
     * Setup a LogTargetFactoryManager
     *
     * @param configuration  The configuration object.
     * @throws ConfigurationException if the configuration is malformed
     */
    private final LogTargetFactoryManager setupTargetFactoryManager( 
            final Configuration configuration )
        throws ConfigurationException
    {
        final DefaultLogTargetFactoryManager targetFactoryManager = new DefaultLogTargetFactoryManager();

        ContainerUtil.enableLogging( targetFactoryManager, getLogger() );

        try
        {
            ContainerUtil.contextualize( targetFactoryManager, m_context );
        }
        catch( final ContextException ce )
        {
            throw new ConfigurationException( "cannot contextualize default factory manager", ce );
        }

        ContainerUtil.configure( targetFactoryManager, configuration );

        return targetFactoryManager;
    }

    /**
     * Setup a LogTargetManager
     *
     * @param configuration  The configuration object.
     * @throws ConfigurationException if the configuration is malformed
     */
    private final LogTargetManager setupTargetManager( final Configuration configuration,
            final LogTargetFactoryManager targetFactoryManager )
        throws ConfigurationException
    {
        final DefaultLogTargetManager targetManager = new DefaultLogTargetManager();

        ContainerUtil.enableLogging( targetManager, getLogger() );

        if( targetManager instanceof LogTargetFactoryManageable )
        {
            targetManager.setLogTargetFactoryManager( targetFactoryManager );
        }

        ContainerUtil.configure( targetManager, configuration );

        return targetManager;
    }

    /**
     * Setup Loggers
     *
     * @param categories []  The array object of configurations for categories.
     * @param root shows if we're processing the root of the configuration
     * @throws ConfigurationException if the configuration is malformed
     */
    private final void setupLoggers( final LogTargetManager targetManager,
                                     final String parentCategory,
                                     final Configuration parentElement,
                                     boolean root,
                                     final boolean defaultAdditive )
        throws ConfigurationException
    {
        boolean rootLoggerConfigured = false;

        final Configuration[] categories = parentElement.getChildren( "category" );

        if( null != categories )
        {
            for( int i = 0; i < categories.length; i++ )
            {
                final Configuration category = categories[ i ];
                final String name = category.getAttribute( "name" );
                final String loglevel = category.getAttribute( "log-level" ).toUpperCase();
                final boolean additive = category.
                    getAttributeAsBoolean( "additive", defaultAdditive );
        
                final Configuration[] targets = category.getChildren( "log-target" );
                final LogTarget[] logTargets = new LogTarget[ targets.length ];
                for( int j = 0; j < targets.length; j++ )
                {
                    final String id = targets[ j ].getAttribute( "id-ref" );
                    logTargets[ j ] = targetManager.getLogTarget( id );
                    if( !m_targets.contains( logTargets[ j ] ) )
                    {
                        m_targets.add( logTargets[ j ] );
                    }
                }
        
                final String fullCategory;
                final org.apache.log.Logger logger;
        
                if ( "".equals( name ) )
                {
                    if ( !root )
                    {
                        final String message = "'category' element with empty name not " +
                                "at the root level: " + category.getLocation();
                        throw new ConfigurationException( message );
                    }
        
                    if ( logTargets.length == 0 )
                    {
                        final String message = "At least one log-target should be " +
                                "specified for the root category " + category.getLocation();
                        throw new ConfigurationException( message );
                    }
        
                    fullCategory = null;
                    logger = m_hierarchy.getRootLogger();
                    rootLoggerConfigured = true;
                }
                else
                {
                    fullCategory = LoggerUtil.getFullCategoryName( parentCategory, name );
                    logger = m_hierarchy.getLoggerFor( fullCategory );
                }
        
                if( getLogger().isDebugEnabled() )
                {
                    /**
                     * We have to identify ourselves now via 'LogKitConfHelper:'
                     * because we are likely to be logging to a shared bootstrap
                     * logger, not to a dedicated category Logger.
                     */
                    final String message = "LogKitConfHelper: adding logger for category '" +
                            ( fullCategory != null ? fullCategory : "" ) + "'";
                    getLogger().debug( message );
                }
        
                logger.setPriority( Priority.getPriorityForName( loglevel ) );
                logger.setLogTargets( logTargets );
                logger.setAdditivity( additive );
        
                setupLoggers( targetManager, fullCategory, category, false, defaultAdditive );
            }
        }

        if ( root && !rootLoggerConfigured )
        {
            final String message = 
                    "No configuration for root category (<category name=''/>) found in "+
                            parentElement.getLocation();
            throw new ConfigurationException( message );
        }
    }

    /**
     * Closes all our LogTargets.
     */
    public void dispose()
    {
        final Iterator iterator = m_targets.iterator();
        while( iterator.hasNext() )
        {
            final LogTarget target = (LogTarget)iterator.next();
            if( target instanceof Closeable )
            {
                ( (Closeable)target ).close();
            }
        }
    }
}
