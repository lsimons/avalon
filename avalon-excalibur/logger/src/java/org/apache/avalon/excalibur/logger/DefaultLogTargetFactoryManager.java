/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.logger;

import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogEnabled;

/**
 * Default LogTargetFactoryManager implementation.  It populates the LogTargetFactoryManager
 * from a configuration file.
 *
 * @author <a href="mailto:giacomo@apache.org">Giacomo Pati</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 02:34:14 $
 * @since 4.0
 */
public class DefaultLogTargetFactoryManager
    extends AbstractLogEnabled
    implements LogTargetFactoryManager, Contextualizable, Configurable
{
    /** Map for name to logger mapping */
    private final Map m_factories = new HashMap();

    /** The context object */
    private Context m_context;

    /**
     * Retrieves a LogTargetFactory from a name. Usually
     * the factory name refers to a element name. If
     * this LogTargetFactoryManager does not have the match a null
     * will be returned.
     *
     * @param factoryName The name of a configured LogTargetFactory.
     * @return the LogTargetFactory or null if none is found.
     */
    public final LogTargetFactory getLogTargetFactory( final String factoryName )
    {
        final LogTargetFactory factory = (LogTargetFactory)m_factories.get( factoryName );
        return factory;
    }

    /**
     * Reads a context object.
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
     * Reads a configuration object and creates the category mapping.
     *
     * @param configuration  The configuration object.
     * @throws ConfigurationException if the configuration is malformed
     */
    public final void configure( final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration[] confs = configuration.getChildren( "factory" );
        for( int i = 0; i < confs.length; i++ )
        {
            final String factoryClass = confs[ i ].getAttribute( "class" );
            final String factoryType = confs[ i ].getAttribute( "type" );

            //FIXME(GP): Is this the right way to load a class or
            //           should the ContextClassLoader be used?
            final LogTargetFactory logTargetFactory;
            try
            {
                Class clazz = null;

                //First lets try the context ClassLoader
                final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                if( null != classLoader )
                {
                    try
                    {
                        clazz = classLoader.loadClass( factoryClass );
                    }
                    catch( final ClassNotFoundException cnfe )
                    {
                    }
                }

                //Okay now lets try classLoader this class was loaded from
                if( null == clazz )
                {
                    clazz = getClass().getClassLoader().loadClass( factoryClass );
                }

                logTargetFactory = (LogTargetFactory)clazz.newInstance();
            }
            catch( final ClassNotFoundException cnfe )
            {
                throw new ConfigurationException( "cannot find LogTargetFactory class " + factoryClass, cnfe );
            }
            catch( final InstantiationException ie )
            {
                throw new ConfigurationException( "cannot instantiate LogTargetFactory class " + factoryClass, ie );
            }
            catch( final IllegalAccessException iae )
            {
                throw new ConfigurationException( "cannot access LogTargetFactory class " + factoryClass, iae );
            }

            if( logTargetFactory instanceof LogEnabled )
            {
                ( (LogEnabled)logTargetFactory ).enableLogging( getLogger() );
            }

            if( logTargetFactory instanceof Contextualizable )
            {
                try
                {
                    ( (Contextualizable)logTargetFactory ).contextualize( m_context );
                }
                catch( final ContextException ce )
                {
                    throw new ConfigurationException( "cannot contextualize LogTargetFactory " + factoryClass, ce );
                }
            }

            if( logTargetFactory instanceof Configurable )
            {
                ( (Configurable)logTargetFactory ).configure( confs[ i ] );
            }

            if( logTargetFactory instanceof LogTargetFactoryManageable )
            {
                ( (LogTargetFactoryManageable)logTargetFactory ).setLogTargetFactoryManager( this );
            }

            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "added new LogTargetFactory of type " + factoryType );
            }
            m_factories.put( factoryType, logTargetFactory );
        }
    }
}
