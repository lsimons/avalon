/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.examples.jdbcdatasource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.avalon.excalibur.component.DefaultRoleManager;
import org.apache.avalon.excalibur.component.ExcaliburComponentManager;
import org.apache.avalon.excalibur.logger.DefaultLogKitManager;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.context.DefaultContext;

import org.apache.log.Hierarchy;
import org.apache.log.Logger;
import org.apache.log.Priority;

/**
 * This example application creates a conmponent which makes use of a JdbcDataSource to
 *  connect to a Hypersonic SQL database.  It then adds a row to a table that it creates
 *  displaying a list of all the rows in the table.
 *
 * Note, this code ignores exceptions to keep the code simple.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.3 $ $Date: 2002/11/07 05:20:09 $
 * @since 4.1
 */
public class Main
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    private Main() {}

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Creates and initializes the component m_manager using config files.
     *
     * @return the ECM for use.
     *
     * @throws Exception if we cannot build the ECM
     */
    private static ExcaliburComponentManager createComponentManager()
        throws Exception
    {
        // Create a context to use.
        DefaultContext context = new DefaultContext();
        // Add any context variables here.
        context.makeReadOnly();

        // Create a ConfigurationBuilder to parse the config files.
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();

        // Load in the configuration files
        Configuration logKitConfig     = builder.build( "../conf/logkit.xml" );
        Configuration rolesConfig      = builder.build( "../conf/roles.xml" );
        Configuration componentsConfig = builder.build( "../conf/components.xml" );

        // Setup the LogKitManager
        DefaultLogKitManager logManager = new DefaultLogKitManager();
        Logger lmLogger = Hierarchy.getDefaultHierarchy().
            getLoggerFor( logKitConfig.getAttribute( "logger", "lm" ) );
        lmLogger.setPriority(
            Priority.getPriorityForName( logKitConfig.getAttribute( "log-level", "INFO" ) ) );
        logManager.setLogger( lmLogger );
        logManager.configure( logKitConfig );

        // Setup the RoleManager
        DefaultRoleManager roleManager = new DefaultRoleManager();
        roleManager.setLogger(
            logManager.getLogger( rolesConfig.getAttribute( "logger", "rm" ) ) );
        roleManager.configure( rolesConfig );

        // Set up the ComponentManager
        ExcaliburComponentManager manager = new ExcaliburComponentManager();
        manager.setLogger(
            logManager.getLogger( componentsConfig.getAttribute( "logger", "cm" ) ) );
        manager.setLogKitManager( logManager );
        manager.contextualize( context );
        manager.setRoleManager( roleManager );
        manager.configure( componentsConfig );
        manager.initialize();

        return manager;
    }

    /**
     * Loop and handle requests from the user.
     *
     * @param helloDB  The HelloDBService we are using to handle our requests
     */
    private static void handleRequests( HelloDBService helloDB )
    {
        System.out.println();
        System.out.println( "Please enter a title to be added to the database" );
        System.out.println( "    (RESET deletes all titles, LIST lists all titles, QUIT or EXIT to quit)" );

        BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );
        String title;
        boolean quit = false;
        do
        {
            System.out.print  ( ": " );
            try
            {
                title = in.readLine();
            }
            catch (IOException e)
            {
                title = "";
            }

            if ( title.length() > 0 )
            {
                if ( title.equalsIgnoreCase( "RESET" ) )
                {
                    System.out.println( "Deleting all titles currently in the database..." );
                    helloDB.deleteRows();
                }
                else if ( title.equalsIgnoreCase( "LIST" ) )
                {
                    System.out.println( "Listing all titles currently in the database..." );
                    helloDB.logRows();
                }
                else if ( title.equalsIgnoreCase( "QUIT" ) || title.equalsIgnoreCase( "EXIT" ) )
                {
                    quit = true;
                }
                else
                {
                    System.out.println( "Adding title '" + title + "' to the database..." );
                    helloDB.addRow( title );
                }
            }
        }
        while ( !quit );

        System.out.println();
    }

    /*---------------------------------------------------------------
     * Main method
     *-------------------------------------------------------------*/
    /**
     * All of the guts of this example exist in the main method.
     *
     * @param args  The command line arguments
     *
     * @throws Exception if there is any problem running this example
     */
    public static void main( String[] args )
        throws Exception
    {
        System.out.println( "Running the JdbcDataSource Example Application" );

        // Create the ComponentManager
        ExcaliburComponentManager manager = createComponentManager();
        try
        {
            // Obtain a reference to the HelloDBService instance
            HelloDBService helloDB = (HelloDBService)manager.lookup( HelloDBService.ROLE );
            try
            {
                handleRequests( helloDB );
            }
            finally
            {
                // Release the HelloDBService instance
                manager.release( helloDB );
                helloDB = null;
            }
        }
        finally
        {
            // Dispose the ComponentManager
            manager.dispose();
        }

        System.out.println();
        System.out.println( "Exiting..." );
        System.exit(0);
    }
}

