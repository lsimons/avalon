/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine.facilities;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.Naming;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.avalon.framework.atlantis.facilities.Manager;
import org.apache.avalon.framework.atlantis.core.Kernel;
import org.apache.avalon.framework.atlantis.core.Embeddor;
import org.apache.framework.context.Context;
import org.apache.framework.context.ContextException;
import org.apache.framework.configuration.Configuration;
import org.apache.framework.lifecycle.StartException;
import org.apache.framework.lifecycle.StopException;

import org.apache.avalon.framework.camelot.Deployer;

import org.apache.log.Logger;
import org.apache.framework.logger.AbstractLoggable;

import org.apache.jmx.introspector.DynamicMBeanFactory;
import org.apache.jmx.adaptor.RMIAdaptor;
import org.apache.jmx.adaptor.RMIAdaptorImpl;

/**
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class ManagerImpl extends AbstractLoggable implements Manager
{
    private MBeanServer mBeanServer;
    private Logger logger;
    private Embeddor embeddor;
    private Deployer deployer;
    private Kernel kernel;
    private RMIAdaptor adaptor;
        private Registry rmiRegistry;
        private int registryPort;
        private String computerName;
        private String adaptorName;
        private String registryString;

    public ManagerImpl()
    {
    }

    /////////////////////////
    /// LIFECYCLE METHODS ///
    /////////////////////////
    public void contextualize( Context context ) throws ContextException
    {
        try { this.mBeanServer = (MBeanServer)context.get( "javax.management.MBeanServer" ); }
        catch( Exception e ) {
            getLogger().error( "Invalid context - no MBeanServer supplied", e );
            throw new ContextException( "Invalid context - no MBeanServer supplied", e ); }
        try { this.embeddor = (Embeddor)context.get( "org.apache.framework.atlantis.core.Embeddor" ); }
        catch( Exception e ) {
            getLogger().error( "Invalid context - no Embeddor supplied", e );
            throw new ContextException( "Invalid context - no Embeddor supplied", e ); }
        try { this.kernel = (Kernel)context.get( "org.apache.framework.atlantis.core.Kernel" ); }
        catch( Exception e ) {
            getLogger().error( "Invalid context - no Kernel supplied", e );
            throw new ContextException( "Invalid context - no Kernel supplied", e ); }
        try { this.deployer = (Deployer)context.get( "org.apache.avalon.framework.camelot.Deployer" ); }
        catch( Exception e ) {
            getLogger().error( "Invalid context - no Deployer supplied", e );
            throw new ContextException( "Invalid context - no Deployer supplied", e ); }

        try { this.registryPort = (new Integer((String)context.get( "java.rmi.registry.port" ))).intValue(); }
        catch( Exception e ) {
            getLogger().error( "Invalid context - no port for RMI Registry supplied", e );
            throw new ContextException( "Invalid context - no port for RMI Registry supplied", e ); }
        try { this.computerName = (String)context.get( "java.rmi.registry.name" ); }
        catch( Exception e ) {
            getLogger().error( "Invalid context - no computer name for RMI Adaptor supplied", e );
            throw new ContextException( "Invalid context - no computer name for RMI Adaptor supplied", e ); }
        try { this.adaptorName = (String)context.get( "org.apache.jmx.adaptor.name" ); }
        catch( Exception e ) {
            getLogger().error( "Invalid context - no name for RMI Adaptor supplied", e );
            throw new ContextException( "Invalid context - no name for RMI Adaptor supplied", e ); }
        this.registryString = "//"+computerName+":"+registryPort+"/"+adaptorName;
    }
    public void start() throws StartException
    {
        try
        {
            mBeanServer.registerMBean(
                DynamicMBeanFactory.create( embeddor ),
                new ObjectName( "Embeddor" ) );
        }
        catch( Exception e ) { getLogger().error( "Unable to register MBean for Embeddor", e ); }
        try
        {
            mBeanServer.registerMBean(
                DynamicMBeanFactory.create( deployer ),
                new ObjectName( "Deployer" ) );
        }
        catch( Exception e ) { getLogger().error( "Unable to register MBean for Deployer", e ); }
        try
        {
            mBeanServer.registerMBean(
                DynamicMBeanFactory.create( kernel ),
                new ObjectName( "Kernel" ) );
        }
        catch( Exception e ) { getLogger().error( "Unable to register MBean for Kernel", e ); }
        try
        {
            mBeanServer.registerMBean(
                DynamicMBeanFactory.create( logger ),
                new ObjectName( "Logger" ) );
        }
        catch( Exception e ) { getLogger().error( "Unable to register MBean for Logger", e ); }

        // create a RMI adaptor for the MBeanServer and expose it
        try
        {
            this.adaptor = new RMIAdaptorImpl( this.mBeanServer );

            // TODO: improve this!
            this.rmiRegistry = LocateRegistry.createRegistry( registryPort );
            Naming.bind(this.registryString, this.adaptor);
        }
        catch( Exception e ) { getLogger().error( "Unable to bind JMX RMI Adaptor", e ); }
    }
    public void stop() throws StopException
    {
        try
        {
            mBeanServer.unregisterMBean( new ObjectName( "Embeddor" ) );
            mBeanServer.unregisterMBean( new ObjectName( "Kernel" ) );
            mBeanServer.unregisterMBean( new ObjectName( "Deployer" ) );
            mBeanServer.unregisterMBean( new ObjectName( "Logger" ) );
        }
        catch( Exception e )
        {
            if( getLogger() != null )
                getLogger().error( "error unregistering MBeans", e );
        }
        try
        {
            Naming.unbind(this.registryString);
        }
        catch( Exception e )
        {
            if( getLogger() != null )
                getLogger().error( "Unable to unbind JMX RMI Adaptor", e );
        }
    }

    ///////////////////////
    /// MANAGER METHODS ///
    ///////////////////////
    public MBeanServer getMBeanServer()
    {
        return this.mBeanServer;
    }
}