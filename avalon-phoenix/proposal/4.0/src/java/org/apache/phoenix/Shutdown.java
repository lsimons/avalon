/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.Naming;
import java.rmi.Remote;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.management.ObjectName;

import org.apache.avalon.framework.atlantis.core.Embeddor;

import org.apache.jmx.adaptor.RMIAdaptor;

/**
 * Class to exit phoenix. Call to shut down the server.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 */
public class Shutdown {
        private static int registryPort;
        private static final int DEFAULT_REGISTRY_PORT =
                                                    1111;
        private static String computerName;
        private static final String DEFAULT_COMPUTER_NAME =
                                                    "localhost";
        private static String adaptorName;
        private static final String DEFAULT_ADAPTOR_NAME =
                                                    "phoenix.manager.JMXAdaptor";
    public Shutdown() {

    }

    public void main( final String[] args )
    {
        final Shutdown shutdown = new Shutdown();

        try
        {
            shutdown.execute( args );
        }
        catch( final Throwable throwable )
        {
            System.out.println( "" );
            System.out.println( "" );
            System.out.println( "There was an uncaught exception:" );
            System.out.println( "---------------------------------------------------------" );
            System.out.println( throwable.toString() );
            //throwable.printStackTrace( System.out  );
            System.out.println( "---------------------------------------------------------" );
            System.out.println( "Please check the configuration files and restart phoenix." );
            System.out.println( "If the problem persists, contact the Avalon project.  See" );
            System.out.println( "http://jakarta.apache.org/avalon for more information." );
            System.out.println( "" );
            System.out.println( "" );
            System.exit( 1 );
        }
    }
    /////////////////////////
    /// EXECUTION METHODS ///
    /////////////////////////
    private void execute( final String[] args ) throws Exception
    {
        try
        {
            final PrivilegedExceptionAction action = new PrivilegedExceptionAction()
            {
                public Object run() throws Exception
                {
                    exec( args );
                    return null;
                }
            };

            AccessController.doPrivileged( action );
        }
        catch( final PrivilegedActionException pae )
        {
            // only "checked" exceptions will be "wrapped" in a PrivilegedActionException.
            throw pae.getException();
        }
    }
    private void exec( final String[] args ) throws Exception
    {
        this.parseCommandLineOptions( args );

        final Registry registry = LocateRegistry.getRegistry(this.registryPort);
        final RMIAdaptor remote = (RMIAdaptor)registry.lookup("//"+computerName+":"+registryPort+"/"+adaptorName);
        final Embeddor embeddor = (Embeddor)remote.getObjectInstance( new ObjectName( "Embeddor" ) );
        embeddor.stop();
    }
    //////////////////////
    /// HELPER METHODS ///
    //////////////////////
    private void parseCommandLineOptions( final String[] args )
    {
        // start with the defaults
        this.registryPort = this.DEFAULT_REGISTRY_PORT;
        this.adaptorName = this.DEFAULT_ADAPTOR_NAME;

        // try to get the computer name from system property
        if( (this.computerName = System.getProperty( "computer.name" )) == null )
            this.computerName = this.DEFAULT_COMPUTER_NAME;

        // TODO: parse options
    }
}
