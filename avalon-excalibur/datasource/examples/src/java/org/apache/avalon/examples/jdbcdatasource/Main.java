/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:
 
 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
 
 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.
 
 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the 
 Apache Software Foundation, please see <http://www.apache.org/>.
 
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
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 14:13:58 $
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
        logManager.contextualize( context );
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

