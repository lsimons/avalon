/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import org.apache.avalon.Composer;
import org.apache.avalon.DefaultComponentManager;
import org.apache.avalon.Loggable;
import org.apache.avalon.camelot.CamelotUtil;
import org.apache.avalon.camelot.Deployer;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.avalon.util.ObjectUtil;
import org.apache.avalon.util.StringUtil;
import org.apache.avalon.util.cli.AbstractMain;
import org.apache.avalon.util.cli.CLArgsParser;
import org.apache.avalon.util.cli.CLOption;
import org.apache.avalon.util.cli.CLOptionDescriptor;
import org.apache.avalon.util.cli.CLUtil;
import org.apache.avalon.util.log.AvalonLogFormatter;
import org.apache.log.LogKit;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.log.Priority;
import org.apache.log.output.FileOutputLogTarget;

/**
 * The class to load the kernel and start it running.
 * 
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class Main
    extends AbstractMain
{
    private static final String    AVALON_HOME          = 
        System.getProperty( "avalon.home", ".." );

    private static final String    DEFAULT_LOG_FILE     = AVALON_HOME + "/logs/avalon.log";
    private static final String    DEFAULT_APPS_PATH    = AVALON_HOME + "/apps";

    private static final String    DEFAULT_KERNEL_CLASS =
        "org.apache.phoenix.engine.DefaultServerKernel";

    private static final int       DEBUG_LOG_OPT        = 'd';
    private static final int       HELP_OPT             = 'h';
    private static final int       KERNEL_CLASS_OPT     = 'k';
    private static final int       LOG_FILE_OPT         = 'l';
    private static final int       APPS_PATH_OPT        = 'a';

    //HACK: force resolution of .jar libraries before security manager is installed.
    //TODO: Is this still needed ????
    private static final Class     AWARE_DEPEND         = 
        org.apache.avalon.Component.class;
    private static final Class     PARSER_DEPEND        = 
        org.xml.sax.SAXException.class;
    private static final Class     CLIENT_DEPEND        = 
        org.apache.phoenix.Block.class;

    protected String               m_kernelClass        = DEFAULT_KERNEL_CLASS;
    protected String               m_appsPath           = DEFAULT_APPS_PATH;
    protected String               m_logFile            = DEFAULT_LOG_FILE;
    protected Logger               m_logger;

    /**
     * Initialise the options for command line parser.
     *
     */
    protected CLOptionDescriptor[] createCLOptions()
    {
        //TODO: localise
        final CLOptionDescriptor options[] = new CLOptionDescriptor[ 5 ];
        options[0] =
            new CLOptionDescriptor( "help",
                                    CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                    HELP_OPT,
                                    "display this help" );

        options[1] =
            new CLOptionDescriptor( "kernel-class",
                                    CLOptionDescriptor.ARGUMENT_REQUIRED,
                                    KERNEL_CLASS_OPT,
                                    "the classname of the kernel." );

        options[2] =
            new CLOptionDescriptor( "log-file",
                                    CLOptionDescriptor.ARGUMENT_REQUIRED,
                                    LOG_FILE_OPT,
                                    "the name of log file." );

        options[3] =
            new CLOptionDescriptor( "apps-path",
                                    CLOptionDescriptor.ARGUMENT_REQUIRED,
                                    APPS_PATH_OPT,
                                    "the path to apps/ directory that contains .sars" );

        options[4] =
            new CLOptionDescriptor( "debug-init",
                                    CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                    DEBUG_LOG_OPT,
                                    "use this option to specify enable debug " + 
                                    "initialisation logs." );
        return options;
    }

    /**
     * Main entry point.
     *
     * @param args[] the command line arguments
     * @exception Exception if an error occurs
     */
    public void main( final String args[] )
    { 
        final Main main = new Main();

        try { main.execute( args ); }
        catch( final Throwable throwable )
        {
            if( null != main.m_logger )
            {
                main.m_logger.error( "Unhandled exception", throwable );
            }

            System.err.println( "Error: " + 
                                StringUtil.printStackTrace( throwable, 8, true ) );
            System.exit( 1 );
        }
    }

    /**
     * Setup properties, classloader, policy, logger etc.
     *
     * @param clOptions the command line options
     * @exception Exception if an error occurs
     */
    protected void execute( final List clOptions )
        throws Exception
    {
        final int size = clOptions.size();
        boolean debugLog = false;

        for( int i = 0; i < size; i++ ) 
        {
            final CLOption option = (CLOption)clOptions.get( i );
            
            switch( option.getId() )
            {
            case 0: 
                System.err.println( "Error: Unknown argument" + option.getArgument() );
                //fall threw
            case HELP_OPT: 
                usage(); 
                return;

            case KERNEL_CLASS_OPT: m_kernelClass = option.getArgument(); break;
            case DEBUG_LOG_OPT: debugLog = true; break;
            case LOG_FILE_OPT: m_logFile = option.getArgument(); break;
            case APPS_PATH_OPT: m_appsPath = option.getArgument(); break;
            }
        }
        
        if( !debugLog ) LogKit.setGlobalPriority( Priority.DEBUG );

        try
        {
            final PrivilegedExceptionAction action = new PrivilegedExceptionAction() 
            {
                public Object run() throws Exception
                {        
                    execute();
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
    
    /**
     * Actually create and execute the main component of kernel.
     *
     * @exception Exception if an error occurs
     */
    protected void execute()
        throws Exception
    {
        //temporary logging hack ....
        
        // create a log channel for the loader.
        final FileOutputLogTarget logTarget = new FileOutputLogTarget( m_logFile );
        final AvalonLogFormatter formatter = new AvalonLogFormatter();
        formatter.setFormat( "%{time} [%7.7{priority}] <<%{category}>> " +
                             "(%{context}): %{message}\\n%{throwable}" );
        logTarget.setFormatter( formatter );
        
        LogKit.addLogTarget( m_logFile, logTarget );
        m_logger = LogKit.createLogger( LogKit.createCategory( "Avalon", Priority.DEBUG ), 
                                        new LogTarget[] { logTarget } );
        m_logger.info( "Loader started" );
        
        ServerKernel kernel = null;
        try
        {
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
            kernel = (ServerKernel)ObjectUtil.createObject( m_kernelClass );
        }
        catch( final Exception e )
        {
            throw new ConfigurationException( "Failed to create kernel", e );
        }

        kernel.setLogger( m_logger );
        kernel.init();//ialize();

        final File directory = new File( m_appsPath );
        final Deployer deployer = createDeployer();
        setupDeployer( kernel, deployer );

        CamelotUtil.deployFromDirectory( deployer, directory, ".sar" );

        //run kernel lifecycle
        kernel.start();
        kernel.run();  
        kernel.stop();      
        kernel.dispose();
        
        System.exit(0);
    }

    /**
     * Setup deployer including Logging/componentManager.
     *
     * @param kernel the kernel deploying to
     * @param deployer the deployer
     * @exception Exception if an error occurs
     */
    protected void setupDeployer( final ServerKernel kernel, final Deployer deployer )
        throws Exception
    {
        if( deployer instanceof Loggable )
        {
            ((Loggable)deployer).setLogger( m_logger );
        }     

        if( deployer instanceof Composer )
        {
            final DefaultComponentManager componentManager = new DefaultComponentManager();
            componentManager.put( "org.apache.avalon.camelot.Container", kernel );
            ((Composer)deployer).compose( componentManager );
        }
    }

    /**
     * Create a deployer for Sar objects.
     * Overide this in sub-classes if necessary.
     *
     * @return the created deployer
     */
    protected Deployer createDeployer()
    {
        return new DefaultSarDeployer();
    }
}
