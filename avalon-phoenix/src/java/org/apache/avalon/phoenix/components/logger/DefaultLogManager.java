/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.logger;

import java.io.File;
import org.apache.avalon.excalibur.logger.DefaultLogKitManager;
import org.apache.avalon.excalibur.logger.LogKitManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.phoenix.interfaces.LogManager;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.log.Hierarchy;

/**
 * Interface that is used to manage Log objects for a Sar.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultLogManager
    extends AbstractLoggable
    implements LogManager
{
    public Hierarchy createHierarchy( final SarMetaData metaData,
                                      final Configuration logs )
        throws Exception
    {
        final DefaultContext context = new DefaultContext();
        context.put( BlockContext.APP_NAME, metaData.getName() );
        context.put( BlockContext.APP_HOME_DIR, metaData.getHomeDirectory() );

        final String version = logs.getAttribute( "version", "1.0" );
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
