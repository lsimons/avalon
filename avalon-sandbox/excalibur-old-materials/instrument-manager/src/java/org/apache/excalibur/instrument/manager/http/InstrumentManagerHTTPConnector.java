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
package org.apache.excalibur.instrument.manager.http;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

import org.apache.excalibur.instrument.AbstractLogEnabledInstrumentable;

import org.apache.excalibur.instrument.manager.DefaultInstrumentManager;
import org.apache.excalibur.instrument.manager.InstrumentManagerConnector;
import org.apache.excalibur.instrument.manager.http.server.AbstractHTTPURLHandler;
import org.apache.excalibur.instrument.manager.http.server.HTTPServer;
import org.apache.excalibur.instrument.manager.InstrumentManagerClientLocalImpl;

/**
 * An HTTP connector which allows a client to connect to the ServiceManager
 *  using the HTTP protocol.  This connector makes use of an extremely
 *  lightweight internal HTTP server to provide this access.
 *
 * If the application is already running a full blown Servlet Engine, one
 *  alternative to this connector is to make use of the InstrumentManagerServlet.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/09/08 09:00:44 $
 * @since 4.1
 */
public class InstrumentManagerHTTPConnector
    extends AbstractLogEnabledInstrumentable
    implements InstrumentManagerConnector, Configurable, Startable
{
    /** The default port. */
    public static final int DEFAULT_PORT = 15080;
    
    public static final String ENCODING = "UTF-8";
    public static final String XML_BANNER = "<?xml version='1.0' encoding='" + ENCODING + "'?>";

    /** Reference to the actual instrument manager. */
    private DefaultInstrumentManager m_manager;
    
    /** The port to listen on for connections. */
    private int m_port;
    
    /** The address to bind the port server to.  Null for any address. */
    private InetAddress m_bindAddr;
    
    /** True if XML handlers should be registered. */
    private boolean m_xml;
    
    /** True if HTML handlers should be registered. */
    private boolean m_html;
    
    private HTTPServer m_httpServer;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new InstrumentManagerHTTPConnector.
     */
    public InstrumentManagerHTTPConnector()
    {
        setInstrumentableName( "http" );
    }

    /*---------------------------------------------------------------
     * InstrumentManagerConnector Methods
     *-------------------------------------------------------------*/
    /**
     * Set the InstrumentManager to which the Connecter will provide
     *  access.  This method is called before the new connector is
     *  configured or started.
     */
    public void setInstrumentManager( DefaultInstrumentManager manager )
    {
        m_manager = manager;
    }

    /*---------------------------------------------------------------
     * Configurable Methods
     *-------------------------------------------------------------*/
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        m_port = configuration.getAttributeAsInteger( "port", DEFAULT_PORT );
        
        String bindAddress = configuration.getChild( "bind" ).getValue( null );
        try
        {
            if ( null != bindAddress )
            {
                m_bindAddr = InetAddress.getByName( bindAddress );
            }
        }
        catch ( final UnknownHostException e )
        {
            throw new ConfigurationException(
                "Unable to resolve the bind point: " + bindAddress, e );
        }
        
        m_xml = configuration.getAttributeAsBoolean( "xml", true );
        m_html = configuration.getAttributeAsBoolean( "html", true );
        
        m_httpServer = new HTTPServer( m_port, m_bindAddr );
        m_httpServer.enableLogging( getLogger().getChildLogger( "server" ) );
        m_httpServer.setInstrumentableName( "server" );
        addChildInstrumentable( m_httpServer );
    }

    /*---------------------------------------------------------------
     * Startable Methods
     *-------------------------------------------------------------*/
    public void start()
        throws Exception
    {
        InstrumentManagerClientLocalImpl client = new InstrumentManagerClientLocalImpl( m_manager );
        
        // Register all of the helpers that we support
        if ( m_xml )
        {
            // XML
            String nameBase = "xml-";
            initAndRegisterHandler(
                new XMLInstrumentableHandler( client ), nameBase + "instrumentable" );
        }
        
        if ( m_html )
        {
            // HTML
            String nameBase = "html-";
            initAndRegisterHandler(
                new HTMLInstrumentableHandler( client ), nameBase + "instrumentable" );
            initAndRegisterHandler(
                new HTMLInstrumentHandler( client ), nameBase + "instrument" );
            initAndRegisterHandler(
                new HTMLSampleHandler( client ), nameBase + "sample" );
            initAndRegisterHandler(
                new HTMLSampleLeaseHandler( client ), nameBase + "sample-lease" );
            initAndRegisterHandler(
                new HTMLCreateSampleHandler( client ), nameBase + "create-sample" );
            initAndRegisterHandler(
                new SampleChartHandler( client ), "sample-chart" );
        }
        
        getLogger().debug( "Starting Instrument Manager HTTP Connector" );
        m_httpServer.start();
        getLogger().info( "Instrument Manager HTTP Connector listening on port: " + m_port );
    }

    public void stop()
        throws Exception
    {
        getLogger().debug( "Stopping Instrument Manager HTTP Connector" );
        m_httpServer.stop();
        m_httpServer = null;
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    private void initAndRegisterHandler( AbstractHTTPURLHandler handler, String name )
        throws Exception
    {
        handler.enableLogging( getLogger().getChildLogger( name ) );
        handler.setInstrumentableName( name );
        addChildInstrumentable( handler );
        
        m_httpServer.registerHandler( handler );
    }
}

