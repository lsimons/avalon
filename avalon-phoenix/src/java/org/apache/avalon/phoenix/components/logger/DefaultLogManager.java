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
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.log.Logger;

/**
 * Interface that is used to manage Log objects for a Sar.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultLogManager
    extends AbstractLoggable
    implements LogManager
{
    public Logger createLogger( final String name, final File baseDirectory, final Configuration logs )
        throws Exception
    {
        final DefaultContext context = new DefaultContext();
        context.put( BlockContext.APP_NAME, name );
        context.put( BlockContext.APP_HOME_DIR, baseDirectory );

        final String version = logs.getAttribute( "version", "1.0" );
        if( version.equals( "1.0" ) )
        {
            final SimpleLogKitManager manager = new SimpleLogKitManager();
            setupLogger( manager );
            manager.contextualize( context );
            manager.configure( logs );
            return manager.getLogger( name );
        }
        else if( version.equals( "1.1" ) )
        {
            final DefaultLogKitManager manager = new DefaultLogKitManager();
            setupLogger( manager );
            manager.contextualize( context );
            manager.configure( logs );
            return manager.getLogger( name );
        }
        else
        {
            throw new IllegalArgumentException( "Unknown log version specification" );
        }
    }
}
