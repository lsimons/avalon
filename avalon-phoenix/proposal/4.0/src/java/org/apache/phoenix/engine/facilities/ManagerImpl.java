/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.facilities;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.phoenix.facilities.Manager;
import org.apache.phoenix.core.Kernel;
import org.apache.phoenix.core.Embeddor;
import org.apache.framework.context.Context;
import org.apache.framework.context.ContextException;
import org.apache.framework.configuration.Configuration;
import org.apache.framework.lifecycle.StartException;
import org.apache.framework.lifecycle.StopException;

import org.apache.avalon.camelot.Deployer;

import org.apache.log.Logger;

import org.apache.jmx.introspector.DynamicMBeanFactory;

/**
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class ManagerImpl implements Manager
{
    private MBeanServer mBeanServer;
    private Logger logger;
    private Embeddor embeddor;
    private Deployer deployer;
    private Kernel kernel;

    public ManagerImpl()
    {
    }

    /////////////////////////
    /// LIFECYCLE METHODS ///
    /////////////////////////
    public void setLogger( Logger logger )
    {
        this.logger = logger;
    }
    public void contextualize( Context context ) throws ContextException
    {
        try { this.mBeanServer = (MBeanServer)context.get( "javax.management.MBeanServer" ); }
        catch( Exception e ) {
            logger.error( "Invalid context - no MBeanServer supplied", e );
            throw new ContextException( "Invalid context - no MBeanServer supplied", e ); }
        try { this.embeddor = (Embeddor)context.get( "org.apache.framework.atlantis.core.Embeddor" ); }
        catch( Exception e ) {
            logger.error( "Invalid context - no Embeddor supplied", e );
            throw new ContextException( "Invalid context - no Embeddor supplied", e ); }
        try { this.kernel = (Kernel)context.get( "org.apache.framework.atlantis.core.Kernel" ); }
        catch( Exception e ) {
            logger.error( "Invalid context - no Kernel supplied", e );
            throw new ContextException( "Invalid context - no Kernel supplied", e ); }
        try { this.deployer = (Deployer)context.get( "org.apache.avalon.camelot.Deployer" ); }
        catch( Exception e ) {
            logger.error( "Invalid context - no Deployer supplied", e );
            throw new ContextException( "Invalid context - no Deployer supplied", e ); }
    }
    public void start() throws StartException
    {
        try
        {
            mBeanServer.registerMBean(
                DynamicMBeanFactory.create( embeddor ),
                new ObjectName( "Embeddor" ) );
        }
        catch( Exception e ) { logger.error( "Unable to register MBean for Embeddor", e ); }
        try
        {
            mBeanServer.registerMBean(
                DynamicMBeanFactory.create( deployer ),
                new ObjectName( "Deployer" ) );
        }
        catch( Exception e ) { logger.error( "Unable to register MBean for Deployer", e ); }
        try
        {
            mBeanServer.registerMBean(
                DynamicMBeanFactory.create( kernel ),
                new ObjectName( "Kernel" ) );
        }
        catch( Exception e ) { logger.error( "Unable to register MBean for Kernel", e ); }
        try
        {
            mBeanServer.registerMBean(
                DynamicMBeanFactory.create( logger ),
                new ObjectName( "Logger" ) );
        }
        catch( Exception e ) { logger.error( "Unable to register MBean for Logger", e ); }
    }
    public void stop() throws StopException
    {
        try {
            mBeanServer.unregisterMBean( new ObjectName( "Embeddor" ) );
            mBeanServer.unregisterMBean( new ObjectName( "Kernel" ) );
            mBeanServer.unregisterMBean( new ObjectName( "Deployer" ) );
            mBeanServer.unregisterMBean( new ObjectName( "Logger" ) );
        }
        catch( Exception e )
        {
            logger.error( "error unregistering MBeans", e );
        }
    }
}