/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.lifecycle;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.CascadingException;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.phoenix.components.lifecycle.ComponentEntry;
import org.apache.avalon.phoenix.components.lifecycle.ResourceAccessor;

/**
 * This is a class to help an Application manage lifecycle of
 * <code>Blocks</code> and <code>BlockListeners</code>. The
 * class will run each individual Entry through each lifecycle stage,
 * and manage erros in a consistent way.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class LifecycleHelper
    extends AbstractLogEnabled
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( LifecycleHelper.class );

    //Constants to designate stages
    private static final int STAGE_CREATE = 0;
    private static final int STAGE_LOGGER = 1;
    private static final int STAGE_CONTEXT = 2;
    private static final int STAGE_COMPOSE = 3;
    private static final int STAGE_CONFIG = 4;
    private static final int STAGE_PARAMETER = 5;
    private static final int STAGE_INIT = 6;
    private static final int STAGE_START = 7;
    private static final int STAGE_STOP = 8;
    private static final int STAGE_DISPOSE = 9;
    private static final int STAGE_DESTROY = 10;

    /**
     * Method to run a <code>Block</code> through it's startup phase.
     * This will involve notification of <code>BlockListener</code>
     * objects, creation of the Block/Block Proxy object, calling the startup
     * Avalon Lifecycle methods and updating State property of BlockEntry.
     * Errors that occur during shutdown will be logged appropriately and
     * cause exceptions with useful messages to be raised.
     *
     * @param name the name o f the component
     * @param entry the entry representing object
     * @throws Exception if an error occurs when block passes
     *            through a specific lifecycle stage
     */
    public Object startup( final String name,
                           final ComponentEntry entry,
                           final ResourceAccessor accessor )
        throws Exception
    {
        int stage = 0;
        try
        {
            //Creation stage
            stage = STAGE_CREATE;
            notice( name, stage );
            final Object object = accessor.createObject( entry );

            //LogEnabled stage
            stage = STAGE_LOGGER;
            if( object instanceof LogEnabled )
            {
                notice( name, stage );
                final Logger logger = accessor.createLogger( entry );
                ContainerUtil.enableLogging( object, logger );
            }

            //Contextualize stage
            stage = STAGE_CONTEXT;
            if( object instanceof Contextualizable )
            {
                notice( name, stage );
                final Context context = accessor.createContext( entry );
                ContainerUtil.contextualize( object, context );
            }

            //Composition stage
            stage = STAGE_COMPOSE;
            if( object instanceof Composable )
            {
                notice( name, stage );
                final ComponentManager componentManager =
                    accessor.createComponentManager( entry );
                ContainerUtil.compose( object, componentManager );
            }
            else if( object instanceof Serviceable )
            {
                notice( name, stage );
                final ServiceManager manager =
                    accessor.createServiceManager( entry );
                ContainerUtil.service( object, manager );
            }

            //Configuring stage
            stage = STAGE_CONFIG;
            if( object instanceof Configurable )
            {
                notice( name, stage );
                final Configuration configuration =
                    accessor.createConfiguration( entry );
                ContainerUtil.configure( object, configuration );
            }

            //Parameterizing stage
            stage = STAGE_PARAMETER;
            if( object instanceof Parameterizable )
            {
                notice( name, stage );
                final Parameters parameters =
                    accessor.createParameters( entry );
                ContainerUtil.parameterize( object, parameters );
            }

            //Initialize stage
            stage = STAGE_INIT;
            if( object instanceof Initializable )
            {
                notice( name, stage );
                ContainerUtil.initialize( object );
            }

            //Start stage
            stage = STAGE_START;
            if( object instanceof Startable )
            {
                notice( name, stage );
                ContainerUtil.start( object );
            }

            return object;
        }
        catch( final Throwable t )
        {
            fail( name, stage, t );

            //fail() throws an exception so next
            //line will never be executed
            return null;
        }
    }

    /**
     * Method to run a <code>Block</code> through it's shutdown phase.
     * This will involve notification of <code>BlockListener</code>
     * objects, invalidating the proxy object, calling the shutdown
     * Avalon Lifecycle methods and updating State property of BlockEntry.
     * Errors that occur during shutdown will be logged appropraitely.
     *
     * @param entry the entry containing Block
     */
    public void shutdown( final String name,
                          final ComponentEntry entry )
    {
        final Object object = entry.getObject();
        entry.invalidate();

        //Stoppable stage
        if( object instanceof Startable )
        {
            notice( name, STAGE_STOP );
            try
            {
                ContainerUtil.stop( object );
            }
            catch( final Throwable t )
            {
                safeFail( name, STAGE_STOP, t );
            }
        }

        //Disposable stage
        if( object instanceof Disposable )
        {
            notice( name, STAGE_DISPOSE );
            try
            {
                ContainerUtil.dispose( object );
            }
            catch( final Throwable t )
            {
                safeFail( name, STAGE_DISPOSE, t );
            }
        }

        notice( name, STAGE_DESTROY );
    }

    /**
     * Utility method to report that a lifecycle stage is about to be processed.
     *
     * @param name the name of block that caused failure
     * @param stage the stage
     */
    private void notice( final String name, final int stage )
    {
        if( getLogger().isDebugEnabled() )
        {
            final String message =
                REZ.getString( "lifecycle.stage.notice",
                               name,
                               new Integer( stage ) );
            getLogger().debug( message );
        }
    }

    /**
     * Utility method to report that there was an error processing
     * specified lifecycle stage.
     *
     * @param name the name of block that caused failure
     * @param stage the stage
     * @param t the exception thrown
     */
    private void safeFail( final String name, final int stage, final Throwable t )
    {
        //final String reason = t.getMessage();
        final String reason = t.toString();
        final String message =
            REZ.getString( "lifecycle.fail.error", name, new Integer( stage ), reason );
        getLogger().error( message );
    }

    /**
     * Utility method to report that there was an error processing
     * specified lifecycle stage. It will also rethrow an exception
     * with a better error message.
     *
     * @param name the name of block that caused failure
     * @param stage the stage
     * @param t the exception thrown
     * @throws java.lang.Exception containing error
     */
    private void fail( final String name, final int stage, final Throwable t )
        throws Exception
    {
        //final String reason = t.getMessage();
        final String reason = t.toString();
        final String message =
            REZ.getString( "lifecycle.fail.error",
                           name,
                           new Integer( stage ), reason );
        getLogger().error( message );
        throw new CascadingException( message, t );
    }
}
