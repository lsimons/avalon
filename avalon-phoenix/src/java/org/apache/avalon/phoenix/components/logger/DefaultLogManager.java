/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.logger;

import org.apache.avalon.excalibur.logger.DefaultLogKitManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.phoenix.interfaces.LogManager;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.log.Hierarchy;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

/**
 * Interface that is used to manage Log objects for a Sar.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class DefaultLogManager
    extends AbstractLogEnabled
    implements LogManager
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( SimpleLogKitManager.class );

    public Hierarchy createHierarchy( final SarMetaData metaData,
                                      final Configuration logs )
        throws Exception
    {
        final DefaultContext context = new DefaultContext();
        context.put( BlockContext.APP_NAME, metaData.getName() );
        context.put( BlockContext.APP_HOME_DIR, metaData.getHomeDirectory() );

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
            return manager.getHierarchy();
        }
        else if( version.equals( "1.1" ) )
        {
            final DefaultLogKitManager manager = new DefaultLogKitManager();
            setupLogger( manager );
            manager.contextualize( context );
            manager.configure( logs );
            return manager.getHierarchy();
        }
        else
        {
            throw new IllegalArgumentException( "Unknown log version specification" );
        }
    }
}
