/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avalon.excalibur.logger.test;

import java.io.InputStream;

import org.apache.avalon.excalibur.logger.DefaultLogKitManager;
import org.apache.avalon.excalibur.logger.LogKitManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.log.Priority;
import org.apache.log.format.PatternFormatter;
import org.apache.log.output.io.StreamTarget;

/**
 * LogKitManagementTest.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.1 $ $Date: 2004/03/17 13:22:38 $
 */
public class LogKitManagementTest
{
    ///Format of default formatter
    private static final String FORMAT =
        "%7.7{priority} %5.5{time}   [%8.8{category}] (%{context}): %{message}\\n%{throwable}";

    //The default logger
    private Logger m_logger;

    protected Priority m_logPriority = Priority.DEBUG;
    protected LogKitManager m_lkm;

    public static void main( String[] args ) throws Exception
    {
        LogKitManagementTest lkmt = new LogKitManagementTest();
        lkmt.initialize();
        lkmt.test();
        lkmt.dispose();
    }

    protected void test()
    {
        Logger log1 = m_lkm.getLogger( "cocoon" );
        log1.debug( "this is the cocoon logger" );
        log1.info( "this is the cocoon logger" );
        log1.info( "this is the cocoon logger" );
        Logger log2 = log1.getChildLogger( "classloader" );
        log2.debug( "this is the childlogger classloader of the cocoon logger" );
        log2.info( "this is the childlogger classloader of the cocoon logger" );
        Logger log3 = m_lkm.getLogger( "cocoon.classloader" );
        log3.debug( "this is the cocoon.classloader logger" );
        log3.info( "this is the cocoon.classloader logger" );
        Logger log4 = m_lkm.getLogger( "foo" );
        log4.debug( "this is the foo logger" );
        log4.info( "this is the foo logger" );
    }

    /** Return the logger */
    protected Logger getLogger()
    {
        return m_logger;
    }

    /**
     * Initializes the ComponentLocator
     *
     * The configuration file is determined by the class name plus .xtest appended,
     * all '.' replaced by '/' and loaded as a resource via classpath
     */
    protected void initialize()
        throws Exception
    {
        final String resourceName = this.getClass().getName().replace( '.', '/' ) + ".xtest";
        System.out.println( "ResourceName = " + resourceName );
        initialize( this.getClass().getClassLoader().getResource( resourceName ).openStream() );
    }

    /**
     * Initializes the ComponentLocator
     *
     * @param testconf The configuration file is passed as a <code>InputStream</code>     *
     * A common way to supply a InputStream is to overwrite the initialize() method
     * in the sub class, do there whatever is needed to get the right InputStream object
     * supplying a conformant xtest configuartion and pass it to this initialize method.
     * the mentioned initialize method is also the place to set a different logging priority
     * to the member variable m_logPriority.
     */
    protected final void initialize( final InputStream testconf )
        throws Exception
    {
        m_logger = setupLogger();
        m_logger.debug( "LogKitManagementTest.initialize" );

        final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        final Configuration conf = builder.build( testconf );
        final Context context = setupContext( conf.getChild( "context" ) );

        final DefaultLogKitManager lkm = new DefaultLogKitManager();
        lkm.enableLogging( new LogKitLogger( m_logger ) );
        lkm.contextualize( context );
        lkm.configure( conf );
        m_lkm = lkm;
    }

    /**
     * Disposes the <code>ComponentLocator</code>
     */
    final private void dispose()
    {
    }

    /**
     * Set up logger configuration
     */
    private Logger setupLogger()
        throws Exception
    {
        //FIXME(GP): This method should setup a LogConfigurator and LogManager
        //           according to the configuration spec. not yet completed/implemented
        //           It will return a default logger for now.
        final Logger logger = Hierarchy.getDefaultHierarchy().getLoggerFor( "" );
        logger.setPriority( m_logPriority );

        final PatternFormatter formatter = new PatternFormatter( FORMAT );
        final StreamTarget target = new StreamTarget( System.out, formatter );
        logger.setLogTargets( new LogTarget[]{target} );

        return logger;
    }

    /**
     * set up a context according to the xtest configuration specifications context
     * element.
     *
     * A method addContext(DefaultContext context) is called here to enable subclasses
     * to put additional objects into the context programmatically.
     */
    private Context setupContext( final Configuration configuration )
        throws Exception
    {
        //FIXME(GP): This method should setup the Context object according to the
        //           configuration spec. not yet completed
        final DefaultContext context = new DefaultContext();
        return ( context );
    }
}
