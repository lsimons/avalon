/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine;

import org.apache.avalon.Composer;
import org.apache.avalon.atlantis.Kernel;
import org.apache.avalon.atlantis.AbstractKernel;
import org.apache.avalon.atlantis.Application;
import org.apache.avalon.camelot.Entry;
import org.apache.log.LogKit;

/**
 * The ServerKernel is the core of the Phoenix system.
 * The kernel is responsible for orchestrating low level services 
 * such as loading, configuring and destroying blocks. It also 
 * gives access to basic facilities such as scheduling sub-systems,
 * protected execution contexts, naming and directory services etc.
 *
 * Note that no facilities are available until after the Kernel has been 
 * configured and initialized.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class PhoenixKernel 
    extends AbstractKernel
    implements Kernel
{
    private final static String BANNER = Constants.SOFTWARE + " " + Constants.VERSION;

    public PhoenixKernel()
    {
        m_entryClass = ServerApplicationEntry.class;
        m_applicationClass = ServerApplication.class;
    }

    public void init() 
        throws Exception 
    {
        System.out.println();
        System.out.println( BANNER );

        super.init();
    }

    protected Application newApplication( final String name, final Entry entry )
        throws Exception
    {
        //It is here where you could return new EASServerApplication()
        //if you wanted to host multiple different types of apps
        return new DefaultServerApplication();
    }

    /**
     * Prepare an application before it is initialized.
     * Overide to provide functionality. 
     * Usually used to setLogger(), contextualize, compose, configure.
     *
     * @param name the name of application
     * @param entry the application entry 
     * @param application the application instance
     * @exception Exception if an error occurs
     */
    protected void prepareApplication( final String name, 
                                       final Entry entry, 
                                       final Application application )
        throws Exception
    {
        final ServerApplicationEntry saEntry = (ServerApplicationEntry)entry;
        final ServerApplication saApplication = (ServerApplication)application;

        setupLogger( saApplication, LogKit.getLoggerFor( name ) );
        saApplication.contextualize( saEntry.getContext() );
        
        if( saApplication instanceof Composer )
        { 
            ((Composer)saApplication).compose( saEntry.getComponentManager() );
        }

        saApplication.configure( saEntry.getConfiguration() );
    }

    protected void postAdd( final String name, final Entry entry )
    {
    }
}
