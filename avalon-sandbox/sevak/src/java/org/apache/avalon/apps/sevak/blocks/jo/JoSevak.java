/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002,2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.avalon.apps.sevak.blocks.jo;

import com.tagtraum.framework.log.C_Log;
import com.tagtraum.framework.log.Log;
import com.tagtraum.framework.server.ServerException;
import com.tagtraum.framework.util.BuildException;
import com.tagtraum.framework.util.I_Builder;
import com.tagtraum.framework.util.URLHelper;
import com.tagtraum.jo.I_JoHost;
import com.tagtraum.jo.I_JoServletContextPeer;
import com.tagtraum.jo.I_JoServletService;
import com.tagtraum.jo.JoServletService;
import com.tagtraum.jo.builder.I_JoHostBuilder;
import com.tagtraum.jo.builder.JoPropertyServiceBuilder;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.avalon.apps.sevak.Sevak;
import org.apache.avalon.apps.sevak.SevakException;
import org.apache.avalon.apps.sevak.SevakContext;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.phoenix.BlockContext;

/**
 * @phoenix:block
 * @phoenix:service name="org.apache.avalon.apps.sevak.Sevak"
 *
 * Jo Wrapper.
 *
 *
 * @see <a href="http://www.tagtraum.com/jo.html">Jo! Project Page</a>
 *
 * @author  Hendrik Schreiber & Paul Hammant
 * @version 1.0
 */
public class JoSevak
    extends AbstractLogEnabled
    implements Sevak, Startable, Contextualizable, Configurable, Initializable
{
    private Log m_log;
    private I_JoServletService m_joServletService;
    private I_Builder m_builder;
    private URL m_buildURL;
    private BlockContext m_context;
    private String m_hostName;

    /**
     * Constrct a Sevak instance that wraps Jo!
     */
    public JoSevak()
    {
        m_log = com.tagtraum.framework.log.Log.getLog( "Jo" );
    }

    /**
     * Contextualize the block
     * @param context the context
     */
    public void contextualize( final Context context )
    {
        this.m_context = (BlockContext)context;
    }

    /**
     * Configure the block
     * @param configuration the configuration
     * @throws ConfigurationException If an error during configuration
     */
    public void configure( final Configuration configuration ) throws ConfigurationException
    {
        m_hostName = configuration.getChild( "hostname" ).getValue( "localhost" );
    }

    /**
     * Initialize the block
     * @throws Exception if a problem during initialization
     */
    public void initialize() throws Exception
    {

        m_buildURL = URLHelper.make( ( (BlockContext)m_context ).getBaseDirectory().toString()
                                     + "/etc/" );

        JoServletService._setJoHome( ( (BlockContext)m_context ).getBaseDirectory() );
        // ugly hack...
        getLogger().info( "JO_HOME = " + JoServletService._getJoHome() );

        new JoAvalonLogListener( "Jo", getLogger() );
        m_builder = (I_Builder)new JoPropertyServiceBuilder();
        m_builder.setName( "Jo" );
        m_builder.setURL( m_buildURL );
        m_joServletService = (I_JoServletService)m_builder.build();
    }

    /**
     * Start the server
     * @throws Exception if a problem
     */
    public final void start() throws Exception
    {
        m_joServletService.start();
    }

    /**
     * Stop the server
     * @throws Exception if a problem
     */
    public void stop() throws Exception
    {
        try
        {
            if( m_joServletService != null )
            {
                m_joServletService.stop();
            }
            m_joServletService = null;
            m_builder = null;
        }
        catch( ServerException e )
        {
            m_log.log( e, C_Log.ERROR );
            getLogger().error( "Error uring stopping of Jo!", e );
        }
    }

    public void deploy( String context, File pathToWebAppFolder, SevakContext sevakContext )
        throws SevakException
    {
        throw new SevakException( "Method not implemented" );
    }

    /**
     * Deploy the server
     * @param context the context
     * @param pathToWebAppFolder the path to the web app
     * @throws SevakException if a problem deploying
     */
    public void deploy( String context, File pathToWebAppFolder ) throws SevakException
    {
        String warUrl = "TODO";
        try
        {
            warUrl = pathToWebAppFolder.toURL().toString();
            // now build the servletcontext
            I_JoHost host = m_joServletService.getHost( m_hostName );
            if( host == null )
            {
                throw new JoException( "Host '" + m_hostName + "' does not exist." );
            }
            I_JoHostBuilder builder = (I_JoHostBuilder)host.getBuilder();

            /*
             * @param host Host to add the webapps to
             * @param url url the docbase is relative to.
             * @param name name of the webapp
             * @param docbase relative or absolute docbase
             * @param mappping URI this webapp will be mapped to
             */

            m_log.log( "ContextPath: " + context );
            m_log.log( "WARUrl: " + warUrl );
            if( host.getNamedServletContextPeer( context ) != null )
            {
                throw new JoException( "Webapp " + context + " is already deployed. You need to"
                                       + " undeploy it in order to deploy a different WAR with the same name." );
            }
            I_JoServletContextPeer peer = builder.buildWebApp( host, null, context, warUrl, context );
            // now start the webapp
            if( peer == null )
            {
                throw new JoException( "Failed to built webapp. Please check logs." );
            }
            peer.start();
            m_log.log( "Successfully deployed " + warUrl + " to " + context );
        }
        catch( JoException je )
        {
            m_log.log( "Problem deploying " + warUrl + " to " + context, C_Log.ERROR );
            m_log.log( je, C_Log.ERROR );
            throw new SevakException( "Jo could not deploy " + warUrl + " to " + context + ": "
                                      + je.getMessage() );
        }
        catch( BuildException be )
        {
            m_log.log( "Problem deploying " + warUrl + " to " + context, C_Log.ERROR );
            m_log.log( be, C_Log.ERROR );
            throw new SevakException( "Jo could not build deployment : " + be.getMessage() );
        }
        catch( MalformedURLException mufe )
        {
            m_log.log( "Problem deploying " + warUrl + " to " + context
                       + " as URL cannot be made from path", C_Log.ERROR );
            m_log.log( mufe, C_Log.ERROR );
            throw new SevakException( "Jo could not build deployment : " + mufe.getMessage() );
        }
    }

    public void deploy( String context, File pathToWebAppFolder, ServiceManager serviceManager ) throws SevakException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Undeploy a webapp
     * @param context The context for the webapp
     * @throws SevakException If a problem during operation
     */
    public void undeploy( String context ) throws SevakException
    {
        I_JoHost host = m_joServletService.getHost( m_hostName );
        if( host == null )
        {
            throw new SevakException( "Host '" + m_hostName + "' does not exist." );
        }
        try
        {
            host.removeServletContextPeer( context );
        }
        catch( Exception e )
        {
            m_log.log( "Problem undeploying webapp " + context, C_Log.ERROR );
            m_log.log( e, C_Log.ERROR );
            throw new SevakException( "Jo could not undeploy " + context );
        }
    }
}
