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
import org.apache.avalon.excalibur.logger.LogKitLoggerManager;
import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.excalibur.logger.SimpleLogKitManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.phoenix.interfaces.LogManager;

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
        ResourceManager.getPackageResources( DefaultLogManager.class );

    /**
     * Constant used to define LogManager class to use original log format.
     */
    private static final String VERSION_1 = SimpleLogKitManager.class.getName();

    /**
     * Constant used to define LogManager class to use excaliburs log format.
     */
    private static final String VERSION_1_1 = LogKitLoggerManager.class.getName();

    /**
     * Constant used to define LogManager class to use log4j log format and system.
     */
    private static final String VERSION_LOG4J =
        "org.apache.avalon.excalibur.logger.Log4JConfLoggerManager";

    /**
     * Create a Logger hierarchy for specified application.
     *
     * @param logs the configuration data for logging
     * @param context the context in which to create loggers
     * @return the Log hierarchy
     * @throws Exception if unable to create Loggers
     */
    public Logger createHierarchy( final Configuration logs,
                                   final Context context )
        throws Exception
    {

        final String version = logs.getAttribute( "version", "1.0" );

        if( getLogger().isDebugEnabled() )
        {
            final String message =
                REZ.getString( "logger-create",
                               context.get( BlockContext.APP_NAME ),
                               version );
            getLogger().debug( message );
        }
        final LoggerManager loggerManager = createLoggerManager( version );
        ContainerUtil.enableLogging( loggerManager, getLogger() );
        ContainerUtil.contextualize( loggerManager, context );
        ContainerUtil.configure( loggerManager, logs );
        return loggerManager.getDefaultLogger();
    }

    /**
     * Create a {@link LoggerManager} for specified version string.
     *
     * @param version the version string
     * @return the created {@link LoggerManager}
     */
    private LoggerManager createLoggerManager( final String version )
    {
        final String classname = getClassname( version );
        try
        {
            final ClassLoader classLoader = getClass().getClassLoader();
            final Class clazz = classLoader.loadClass( classname );
            return (LoggerManager)clazz.newInstance();
        }
        catch( final Exception e )
        {
            final String message =
                REZ.getString( "no-create-manager", version, e );
            throw new IllegalArgumentException( message );
        }
    }

    /**
     * Get the classname of {@link LoggerManager} that coresponds
     * to specified version.
     *
     * @param version the version string
     * @return the classname of {@link LoggerManager}
     */
    private String getClassname( final String version )
    {
        if( version.equals( "1.0" ) )
        {
            return VERSION_1;
        }
        else if( version.equals( "1.1" ) )
        {
            return VERSION_1_1;
        }
        else if( version.equals( "log4j" ) )
        {
            return VERSION_LOG4J;
        }
        else
        {
            final String message =
                REZ.getString( "unknown-version", version );
            throw new IllegalArgumentException( message );
        }
    }
}
