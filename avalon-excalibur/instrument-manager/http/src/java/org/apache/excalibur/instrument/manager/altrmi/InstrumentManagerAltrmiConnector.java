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

package org.apache.excalibur.instrument.manager.altrmi;

import org.apache.altrmi.server.PublicationDescription;
import org.apache.altrmi.server.impl.AbstractServer;
import org.apache.altrmi.server.impl.socket.CompleteSocketCustomStreamServer;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.excalibur.instrument.manager.DefaultInstrumentManager;
import org.apache.excalibur.instrument.manager.InstrumentManagerClientLocalImpl;
import org.apache.excalibur.instrument.manager.InstrumentManagerConnector;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.5 $ $Date: 2004/02/28 11:47:32 $
 * @since 4.1
 */
public class InstrumentManagerAltrmiConnector
    extends AbstractLogEnabled
    implements InstrumentManagerConnector, Configurable, Startable
{
    /** The default port. */
    public static final int DEFAULT_PORT = 15555;

    private DefaultInstrumentManager m_manager;
    private int m_port;
    private AbstractServer m_server;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new InstrumentManagerAltrmiConnector.
     */
    public InstrumentManagerAltrmiConnector()
    {
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
    }

    /*---------------------------------------------------------------
     * Startable Methods
     *-------------------------------------------------------------*/
    public void start()
        throws Exception
    {
        getLogger().debug( "Starting Instrument Manager Altrmi Connector" );
        InstrumentManagerClientLocalImpl client = new InstrumentManagerClientLocalImpl( m_manager );

        // Create the socket server
        m_server = new CompleteSocketCustomStreamServer.WithSimpleDefaults( m_port );

        Class[] additionalFacadeClasses = new Class[]
        {
            InstrumentableDescriptor.class,
            InstrumentDescriptor.class,
            InstrumentSampleDescriptor.class
        };

        m_server.publish( client, "InstrumentManagerClient",
            new PublicationDescription( InstrumentManagerClient.class, additionalFacadeClasses ) );

        m_server.start();
        getLogger().info( "Instrument Manager Altrmi Connector listening on port: " + m_port );
        
        getLogger().info( "NOTICE: The AltRMI Connector is an \"experimental feature\" until "
            + "AltRMI reaches 1.0." );
    }

    public void stop()
        throws Exception
    {
        getLogger().debug( "Stopping Instrument Manager Altrmi Connector" );
        m_server.stop();
        m_server = null;
    }
}

