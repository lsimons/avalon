/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.monitor;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * The PassiveMonitor is used to passively check a set of resources to see if they have
 * changed.  It will be implemented as a Component, that can be retrieved from
 * the ComponentManager.  It defaults to checking every 1 minute.  The configuration
 * looks like this:
 *
 * <pre>
 *   &lt;monitor&gt;
 *     &lt;init-resources&gt;
 *       &lt;-- This entry can be repeated for every resource you want to register immediately --&gt;
 *
 *       &lt;resource key="<i>file:./myfile.html</i>" class="<i>org.apache.avalon.excalibur.monitor.FileMonitor</i>"/&gt;
 *     &lt;/init-resources&gt;
 *   &lt;/monitor&gt;
 * </pre>
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version $Id: PassiveMonitor.java,v 1.8 2002/05/13 12:17:40 donaldp Exp $
 */
public final class PassiveMonitor
    extends AbstractLoggable
    implements Monitor, ThreadSafe, Configurable
{
    private static final Class[] m_constructorParams = new Class[]{String.class};
    private Map m_resources = new HashMap();
    private Map m_lastModified = Collections.synchronizedMap( new HashMap() );

    public final void configure( final Configuration conf )
        throws ConfigurationException
    {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Configuration[] initialResources = conf.getChild( "init-resources" ).getChildren( "resource" );

        for( int i = 0; i < initialResources.length; i++ )
        {
            String key = initialResources[ i ].getAttribute( "key", "*** KEY NOT SPECIFIED ***" );
            String className = initialResources[ i ].getAttribute( "class", "*** CLASSNAME NOT SPECIFIED ***" );

            try
            {
                Class clazz = loader.loadClass( className );
                Constructor initializer = clazz.getConstructor( PassiveMonitor.m_constructorParams );
                this.addResource( (Resource)initializer.newInstance( new Object[]{key} ) );

                if( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "Initial Resource: \"" + key + "\" Initialized." );
                }
            }
            catch( Exception e )
            {
                if( getLogger().isWarnEnabled() )
                {
                    getLogger().warn( "Initial Resource: \"" + key +
                                      "\" Failed (" + className + ").", e );
                }
            }
        }
    }

    /**
     * Add a resource to monitor.  The resource key referenced in the other
     * interfaces is derived from the resource object.
     */
    public final void addResource( final Resource resource )
    {

        synchronized( m_resources )
        {
            if( m_resources.containsKey( resource.getResourceKey() ) )
            {
                Resource original = (Resource)m_resources.get( resource.getResourceKey() );
                original.addPropertyChangeListenersFrom( resource );
            }
            else
            {
                m_resources.put( resource.getResourceKey(), resource );
            }
        }
    }

    /**
     * Find a monitored resource.  If no resource is available, return null
     */
    public final Resource getResource( final String key )
    {
        synchronized( m_resources )
        {
            Resource resource = (Resource)m_resources.get( key );

            if( resource != null )
            {
                Long lastModified = (Long)m_lastModified.get( key );

                if( lastModified != null )
                {
                    resource.testModifiedAfter( lastModified.longValue() );
                }

                m_lastModified.put( key, new Long( System.currentTimeMillis() ) );
            }

            return resource;
        }
    }

    /**
     * Remove a monitored resource by key.
     */
    public final void removeResource( final String key )
    {
        synchronized( m_resources )
        {
            Resource resource = (Resource)m_resources.remove( key );
            resource.removeAllPropertyChangeListeners();
        }
    }

    /**
     * Remove a monitored resource by reference.
     */
    public final void removeResource( final Resource resource )
    {
        this.removeResource( resource.getResourceKey() );
    }
}
