/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.logger;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.logger.DefaultLogKitManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.phoenix.interfaces.LogManager;
import org.apache.avalon.phoenix.metadata.SarMetaData;

/**
 * Interface that is used to manage Log objects for a Sar.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public class DefaultLogManager
    extends AbstractLogEnabled
    implements LogManager
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( SimpleLogKitManager.class );

    /**
     * Create a Logger hierarchy for specified application.
     *
     * @param metaData the metadata for application
     * @param logs the configuration data for logging
     * @param classLoader the ClassLoader for application
     * @return the Log hierarchy
     * @throws Exception if unable to create Loggers
     * @todo pass classLoader down into LogKitManager and
     *       use that to try to load targets.
     */
    public Logger createHierarchy( final SarMetaData metaData,
                                   final Configuration logs,
                                   final ClassLoader classLoader )
        throws Exception
    {
        final DefaultContext context = new DefaultContext();
        context.put( BlockContext.APP_NAME, metaData.getName() );
        context.put( BlockContext.APP_HOME_DIR, metaData.getHomeDirectory() );
        context.put( "classloader", classLoader );

        final String version = logs.getAttribute( "version", "1.0" );

        if( getLogger().isDebugEnabled() )
        {
            final String message =
                REZ.getString( "logger-create", metaData.getName(), version );
            getLogger().debug( message );
        }

        if( version.equals( "1.0" ) )
        {
            final SimpleLogKitManager manager = new SimpleLogKitManager();
            setupLogger( manager );
            manager.contextualize( context );
            manager.configure( logs );
            return manager.getDefaultLogger();
        }
        else if( version.equals( "1.1" ) )
        {
            final DefaultLogKitManager manager =
                new DefaultLogKitManager();
            setupLogger( manager );
            manager.contextualize( context );
            manager.configure( logs );
            return new LogKitHierarchyLogger( manager.getHierarchy() );
        }
        else
        {
            final String message = "Unknown log version specification";
            throw new IllegalArgumentException( message );
        }
    }
}
