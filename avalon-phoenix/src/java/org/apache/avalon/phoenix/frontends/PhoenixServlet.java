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
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.CascadingRuntimeException;
import org.apache.avalon.framework.ExceptionUtil;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AvalonFormatter;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.phoenix.components.embeddor.SingleAppEmbeddor;
import org.apache.avalon.phoenix.interfaces.Embeddor;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Priority;
import org.apache.log.output.io.FileTarget;

/**
 * WARNING: DO NOT USE THIS SERVLET FOR PRODUCTION SERVICE. THIS IS EXPERIMENTAL.
 * Servlet frontends for SingleAppEmbeddor.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 * @deprecated
 */
public class PhoenixServlet
    extends HttpServlet
    implements Runnable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( PhoenixServlet.class );

    private static final String DEFAULT_LOG_FILE = "/logs/phoenix.log";

    private static final String DEFAULT_FORMAT =
        "%7.7{priority} %23.23{time:yyyy-MM-dd' 'HH:mm:ss.SSS} [%8.8{category}] (%{context}): "
        + "%{message}\n%{throwable}";

    private Parameters m_parameters;

    private SingleAppEmbeddor m_embeddor;

    private String getInitParameter( final String name,
                                     final String defaultValue )
    {
        final String value = getInitParameter( name );
        if( null == value )
        {
            return defaultValue;
        }
        else
        {
            return value;
        }
    }

    public void init()
        throws ServletException
    {
        super.init();

        final ServletContext context = getServletContext();
        final String phoenixHome = getInitParameter( "phoenix.home", "/WEB-INF" );
        final String logDestination = getInitParameter( "log-destination" );
        final String logPriority = getInitParameter( "log-priority" );
        final String appName = getInitParameter( "application-name", "default" );
        final String appLoc =
            getInitParameter( "application-location", phoenixHome + "/" + appName );
        final String configFile =
            getInitParameter( "config-file", phoenixHome + "/conf/kernel.xml" );

        m_parameters = new Parameters();
        m_parameters.setParameter( "phoenix.home", context.getRealPath( phoenixHome ) );
        m_parameters.setParameter( "log-destination", context.getRealPath( logDestination ) );
        m_parameters.setParameter( "log-priority", logPriority );
        m_parameters.setParameter( "application-name", appName );
        m_parameters.setParameter( "application-location", context.getRealPath( appLoc ) );

        try
        {
            final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
            final Configuration kernelConf =
                builder.buildFromFile( context.getRealPath( configFile ) );
            final Configuration embeddorConf = kernelConf.getChild( "embeddor" );
            final String embeddorClassname = embeddorConf.getAttribute( "class" );

            m_embeddor = (SingleAppEmbeddor)Class.forName( embeddorClassname ).newInstance();

            ContainerUtil.enableLogging( m_embeddor, createLogger( m_parameters ) );
            ContainerUtil.parameterize( m_embeddor, m_parameters );
            ContainerUtil.configure( m_embeddor, embeddorConf );
            ContainerUtil.initialize( m_embeddor );

            final Thread thread = new Thread( this, "Phoenix-Monitor" );
            thread.start();
        }
        catch( final Throwable throwable )
        {
            log( REZ.getString( "main.exception.header" ) );
            log( "---------------------------------------------------------" );
            log( ExceptionUtil.printStackTrace( throwable ) );
            log( "---------------------------------------------------------" );
            log( REZ.getString( "main.exception.footer" ) );
            throw new ServletException( throwable );
        }

        getServletContext().setAttribute( Embeddor.ROLE, m_embeddor );
    }

    public void run()
    {
        try
        {
            m_embeddor.execute();
        }
        catch( final Throwable throwable )
        {
            log( REZ.getString( "main.exception.header" ) );
            log( "---------------------------------------------------------" );
            log( ExceptionUtil.printStackTrace( throwable ) );
            log( "---------------------------------------------------------" );
            log( REZ.getString( "main.exception.footer" ) );

            final String message = REZ.getString( "servlet.error.execute" );
            throw new CascadingRuntimeException( message, throwable );
        }
    }

    public void destroy()
    {
        getServletContext().removeAttribute( Embeddor.ROLE );

        try
        {
            ContainerUtil.shutdown( m_embeddor );
        }
        catch( final Throwable throwable )
        {
            log( REZ.getString( "main.exception.header" ) );
            log( "---------------------------------------------------------" );
            log( ExceptionUtil.printStackTrace( throwable ) );
            log( "---------------------------------------------------------" );
            log( REZ.getString( "main.exception.footer" ) );
        }
        m_embeddor = null;
        m_parameters = null;
    }

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
}
