/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.camelot;

import java.net.URL;
import java.util.HashMap;
import org.apache.avalon.component.Component;
import org.apache.avalon.logger.AbstractLoggable;

/**
 * This is the component that creates the components.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultFactory
    extends AbstractLoggable
    implements Factory
{
    protected static class LoaderEntry
    {
        Loader      m_loader;
        long        m_lastModified;
    }

    protected final HashMap        m_loaders      = new HashMap();

    /**
     * Create a component whos position is indicated by locator.
     *
     * @param locator the locator indicating the component location
     * @return the component
     * @exception FactoryException if an error occurs
     */
    public Object create( final Locator locator )
        throws FactoryException
    {
        final Loader loader = getLoaderFor( locator.getLocation() );

        try { return loader.load( locator.getName() ); }
        catch( final Exception e )
        {
            throw new FactoryException( "Unable to create " + locator.getName() +
                                        " from " + locator.getLocation(), e );
        }
    }

    public Object create( final Locator locator, final Class clazz )
        throws FactoryException
    {
        final Object object = create( locator );

        if( !clazz.isInstance( object ) )
        {
            throw new FactoryException( "Created object of type " + object.getClass().getName() +
                                        " not compatable with type " + clazz.getName() );
        }

        return object;
    }

    protected Loader getLoaderFor( final URL url )
    {
        final String location = url.toString();
        LoaderEntry loader = (LoaderEntry)m_loaders.get( location );

        if( null == loader )
        {
            getLogger().info( "Creating ClassLoader for " + location );
            loader = new LoaderEntry();

            loader.m_loader = setupLoader( url );
            loader.m_lastModified = System.currentTimeMillis();

            m_loaders.put( location, loader );
        }
        else
        {
            //TODO: Check it up to date and reload if necessary
        }

        return loader.m_loader;
    }

    protected Loader setupLoader( final URL url )
    {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final Loader loader = createLoader( url, classLoader );
        setupLogger( loader );

        return loader;
    }

    /**
     * Create a new loader.
     * Put in another method so that it can be overridden.
     *
     * @param location the location the Loader will load from
     * @return the loader
     */
    protected Loader createLoader( final URL url, final ClassLoader classLoader )
    {
        return new DefaultLoader( url, classLoader );
    }
}
