/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

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

package org.apache.avalon.phoenix.frontends;

import java.io.File;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.ExceptionUtil;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.AvalonFormatter;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.phoenix.interfaces.ContainerConstants;
import org.apache.avalon.phoenix.interfaces.Embeddor;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Priority;
import org.apache.log.output.io.FileTarget;

/**
 * The class to load the kernel and start it running.
 *
 * @author Peter Donald
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public final class CLIMain
    extends Observable
    implements Runnable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( CLIMain.class );

    private static final String DEFAULT_LOG_FILE =
        File.separator + "logs" + File.separator + "phoenix.log";

    private static final String DEFAULT_CONF_FILE =
        File.separator + "conf" + File.separator + "kernel.xml";

    private static final String DEFAULT_FORMAT =
        "%7.7{priority} %23.23{time:yyyy-MM-dd' 'HH:mm:ss.SSS} [%8.8{category}] (%{context}): "
        + "%{message}\n%{throwable}";

    ///The embeddor attached to frontend
    private Embeddor m_embeddor;

    ///The code to return to system using exit code
    private int m_exitCode;

    private ShutdownHook m_hook;

    private boolean m_shuttingDown;

    /**
     * Main entry point.
     *
     * @param args the command line arguments
     */
    public int main( final String[] args,
                     final Map data,
                     final boolean blocking )
    {
        try
        {
            final String command = "java " + getClass().getName() + " [options]";
            final CLISetup setup = new CLISetup( command );

            if( false == setup.parseCommandLineOptions( args ) )
            {
                return 0;
            }

            System.out.println();
            System.out.println( ContainerConstants.SOFTWARE + " " + ContainerConstants.VERSION );
            System.out.println();

            final Parameters parameters = setup.getParameters();
            final String phoenixHome = System.getProperty( "phoenix.home", ".." );
            parameters.setParameter( "phoenix.home", phoenixHome );
            if( !parameters.isParameter( "phoenix.configfile" ) )
            {
                final String filename = phoenixHome + DEFAULT_CONF_FILE;
                final File configFile =
                    new File( filename ).getCanonicalFile();

                // setting default
                parameters.setParameter( "phoenix.configfile",
                                         configFile.toString() );
            }

            execute( parameters, data, blocking );
        }
        catch( final Throwable throwable )
        {
            handleException( throwable );
        }

        return m_exitCode;
    }

    /**
     * Actually create and execute the main component of embeddor.
     */
    private void execute( final Parameters parameters,
                          final Map data,
                          final boolean blocking )
    {
        if( !startup( parameters, data ) )
        {
            return;
        }

        final boolean disableHook = parameters.getParameterAsBoolean( "disable-hook", false );
        if( false == disableHook )
        {
            m_hook = new ShutdownHook( this );
            Runtime.getRuntime().addShutdownHook( m_hook );
        }

        // If an Observer is present in the data object, then add it as an observer for
        //  m_observable
        Observer observer = (Observer)data.get( Observer.class.getName() );
        if( null != observer )
        {
            addObserver( observer );
        }

        if( blocking )
        {
            run();
        }
        else
        {
            final Thread thread = new Thread( this, "Phoenix-Monitor" );
            thread.setDaemon( false );
            thread.start();
        }
    }

    public void run()
    {
        try
        {
            m_embeddor.execute();
        }
        catch( final Throwable throwable )
        {
            handleException( throwable );
        }
        finally
        {
            if( null != m_hook )
            {
                Runtime.getRuntime().removeShutdownHook( m_hook );
            }
            shutdown();
        }
    }

    /**
     * Startup the embeddor.
     */
    private synchronized boolean startup( final Parameters parameters,
                                          final Map data )
    {
        try
        {
            final String configFilename = parameters.getParameter( "phoenix.configfile" );
            final Configuration root = getConfigurationFor( configFilename );
            final Configuration configuration = root.getChild( "embeddor" );
            final String embeddorClassname = configuration.getAttribute( "class" );
            m_embeddor = (Embeddor)Class.forName( embeddorClassname ).newInstance();

            ContainerUtil.enableLogging( m_embeddor,
                                         createLogger( parameters ) );
            ContainerUtil.contextualize( m_embeddor,
                                         new DefaultContext( data ) );
            ContainerUtil.parameterize( m_embeddor, parameters );
            ContainerUtil.configure( m_embeddor, configuration );
            ContainerUtil.initialize( m_embeddor );
        }
        catch( final Throwable throwable )
        {
            handleException( throwable );
            return false;
        }

        return true;
    }

    /**
     * Uses {@link org.apache.log.Hierarchy} to create a new
     * logger using "Phoenix" as its category, DEBUG as its
     * priority and the log-destination from Parameters as its
     * destination.
     * TODO: allow configurable priorities and multiple
     * logtargets.
     */
    private Logger createLogger( final Parameters parameters )
        throws Exception
    {
        final String phoenixHome = parameters.getParameter( "phoenix.home" );
        final String logDestination =
            parameters.getParameter( "log-destination", phoenixHome + DEFAULT_LOG_FILE );
        final String logPriority =
            parameters.getParameter( "log-priority", "INFO" );
        final AvalonFormatter formatter = new AvalonFormatter( DEFAULT_FORMAT );
        final File file = new File( logDestination );
        final FileTarget logTarget = new FileTarget( file, false, formatter );

        //Create an anonymous hierarchy so no other
        //components can get access to logging hierarchy
        final Hierarchy hierarchy = new Hierarchy();
        final org.apache.log.Logger logger = hierarchy.getLoggerFor( "Phoenix" );
        logger.setLogTargets( new LogTarget[]{logTarget} );
        logger.setPriority( Priority.getPriorityForName( logPriority ) );
        logger.info( "Logger started" );
        return new LogKitLogger( logger );
    }

    /**
     * Shut the embeddor down.
     * This method is designed to only be called from within the ShutdownHook.
     * To shutdown Pheonix, call shutdown() below.
     */
    protected void forceShutdown()
    {
        if( null == m_hook || null == m_embeddor )
        {
            //We were shutdown gracefully but the shutdown hook
            //thread was not removed. This can occur when an earlier
            //shutdown hook caused a shutdown() request to be processed
            return;
        }

        final String message = REZ.getString( "main.abnormal-exit.notice" );
        System.out.print( message );
        System.out.print( " " );
        System.out.flush();

        shutdown();
    }

    /**
     * Shut the embeddor down.
     *
     * Note must be public so that the Frontend can
     * shut it down via reflection.
     */
    public synchronized void shutdown()
    {
        // Depending on how the shutdown process is initiated, it is possible
        //  that the shutdown() method can be recursively called from within
        //  the call to notifyObservers below.
        if( !m_shuttingDown )
        {
            m_shuttingDown = true;

            //Null hook so it is not tried to be removed
            //when we are shutting down. (Attempting to remove
            //hook during shutdown raises an exception).
            m_hook = null;

            if( null != m_embeddor )
            {
                final String message = REZ.getString( "main.exit.notice" );
                System.out.println( message );
                System.out.flush();

                try
                {
                    ContainerUtil.shutdown( m_embeddor );
                }
                catch( final Throwable throwable )
                {
                    handleException( throwable );
                }
                finally
                {
                    m_embeddor = null;
                }
            }

            // Notify any observers that Phoenix is shutting down
            setChanged();
            notifyObservers( "shutdown" );
        }
    }

    /**
     * Print out exception and details to standard out.
     *
     * @param throwable the exception that caused failure
     */
    private void handleException( final Throwable throwable )
    {
        System.out.println( REZ.getString( "main.exception.header" ) );
        System.out.println( "---------------------------------------------------------" );
        System.out.println( "--- Message ---" );
        System.out.println( throwable.getMessage() );
        System.out.println( "--- Stack Trace ---" );
        System.out.println( ExceptionUtil.printStackTrace( throwable ) );
        System.out.println( "---------------------------------------------------------" );
        System.out.println( REZ.getString( "main.exception.footer" ) );

        m_exitCode = 1;
    }

    private Configuration getConfigurationFor( final String location )
        throws Exception
    {
        final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        return builder.buildFromFile( location );
    }
}

final class ShutdownHook
    extends Thread
{
    private final CLIMain m_main;

    protected ShutdownHook( final CLIMain main )
    {
        m_main = main;
    }

    /**
     * Run the shutdown hook.
     */
    public void run()
    {
        m_main.forceShutdown();
    }
}
