/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine;

import org.apache.avalon.Composer;
import org.apache.avalon.atlantis.AbstractKernel;
import org.apache.avalon.atlantis.Application;
import org.apache.avalon.camelot.Entry;
import org.apache.log.LogKit;

/**
 * This is the base abstract ServerKernel which other implementations should extend.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class AbstractServerKernel 
    extends AbstractKernel
    implements ServerKernel
{
    public AbstractServerKernel()
    {
        m_entryClass = ServerApplicationEntry.class;
        m_applicationClass = ServerApplication.class;
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
}
