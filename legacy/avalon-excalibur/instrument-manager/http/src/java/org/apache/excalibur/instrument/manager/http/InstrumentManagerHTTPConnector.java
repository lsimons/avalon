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

package org.apache.excalibur.instrument.manager.http;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

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
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.6 $ $Date: 2004/03/06 14:01:28 $
 * @since 4.1
 */
public class InstrumentManagerHTTPConnector
    extends AbstractLogEnabledInstrumentable
    implements InstrumentManagerConnector, Configurable, Startable
{
    /** The default port. */
    public static final int DEFAULT_PORT = 15080;
    
    /** The encoding to use when writing out pages and reading in parameters. */
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
                new XMLInstrumentManagerHandler( client ), nameBase + "instrument-manager" );
            initAndRegisterHandler(
                new XMLInstrumentableHandler( client ), nameBase + "instrumentable" );
            initAndRegisterHandler(
                new XMLInstrumentHandler( client ), nameBase + "instrument" );
            initAndRegisterHandler( new XMLSampleHandler( client ), nameBase + "sample" );
            initAndRegisterHandler(	new XMLSnapshotHandler( client ), nameBase + "snapshot" );
            initAndRegisterHandler(	new XMLSnapshotsHandler( client ), nameBase + "snapshots" );
        }
        
        if ( m_html )
        {
            // HTML
            String nameBase = "html-";
            initAndRegisterHandler(
                new HTMLInstrumentManagerHandler( client ), nameBase + "instrument-manager" );
            initAndRegisterHandler(
                new HTMLInstrumentableHandler( client ), nameBase + "instrumentable" );
            initAndRegisterHandler(
                new HTMLInstrumentHandler( client ), nameBase + "instrument" );
            initAndRegisterHandler( new HTMLSampleHandler( client ), nameBase + "sample" );
            initAndRegisterHandler(
                new HTMLSampleLeaseHandler( client ), nameBase + "sample-lease" );
            initAndRegisterHandler(
                new HTMLCreateSampleHandler( client ), nameBase + "create-sample" );
            initAndRegisterHandler( new SampleChartHandler( client ), "sample-chart" );
            initAndRegisterHandler( new HTMLRootHandler( client ), nameBase + "root" );
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

