/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.apps.sevak.demo;

import java.io.File;
//avalon imports
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.phoenix.BlockContext;
//sevak imports
import org.apache.avalon.apps.sevak.Sevak;

/**
 * @phoenix:block
 *
 * Test Sevak (Tomcat wrapper).
 *
 * @author  Vinay Chandran<vinayc77@yahoo.com>
 * @version 1.0
 */
public class SevakTest
        extends AbstractLogEnabled
        implements Contextualizable, Serviceable, Configurable, Initializable {
    private BlockContext m_context;
    private Configuration m_configuration;
    private Sevak m_sevak;

    public void contextualize( final Context context ) {
        getLogger().info( "SevakTest.contextualize()" );
        m_context = (BlockContext) context;
    }

    public void configure( final Configuration configuration )
            throws ConfigurationException {

        m_configuration = configuration;

    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable
     * @phoenix:dependency name="org.apache.avalon.apps.sevak.Sevak"
     */
    public void service( final ServiceManager serviceManager )
            throws ServiceException {
        getLogger().info( "SevakTest.service()" );
        m_sevak = (Sevak) serviceManager.lookup( Sevak.class.getName() );
    }

    public void initialize()
            throws Exception {
        getLogger().info( "SevakTest.initialize()" );
        Configuration[] contexts = m_configuration.getChildren( "Context" );
        for( int i = 0; i < contexts.length; i++ ) {
            String ctx = contexts[ i ].getAttribute( "docBase" );
            String ctxPath = contexts[ i ].getAttribute( "path" );
            ctxPath = ctxPath.replace( '/', File.separatorChar );
            ctxPath = ctxPath.replace( '\\', File.separatorChar );
            String ctxFullPath = m_context.getBaseDirectory().getAbsolutePath()
                    + File.separatorChar + ctxPath;
            //System.out.println("Deploying " + ctx + " " + ctxFullPath);
            m_sevak.deploy( ctx, new File( ctxFullPath ) );
        }

    }


}
