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
import org.apache.altrmi.server.PublicationException;
import org.apache.altrmi.server.ServerException;
import org.apache.altrmi.server.impl.AbstractServer;
import org.apache.altrmi.server.impl.socket.CompleteSocketCustomStreamServer;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.excalibur.instrument.manager.DefaultInstrumentManager;
import org.apache.excalibur.instrument.manager.InstrumentManagerClientLocalImpl;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;

/**
 *
 * @deprecated Please configure connectors in the instrument manager's configuration
 *  file.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.4 $ $Date: 2004/02/28 11:47:32 $
 * @since 4.1
 */
public class InstrumentManagerAltrmiServer
    implements Disposable
{
    /** The default port. */
    public static final int DEFAULT_PORT = 15555;

    private int m_port;
    private AbstractServer m_server;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public InstrumentManagerAltrmiServer( DefaultInstrumentManager manager )
        throws ServerException, PublicationException
    {
        this( manager, DEFAULT_PORT );
    }

    public InstrumentManagerAltrmiServer( DefaultInstrumentManager manager, int port )
        throws ServerException, PublicationException
    {
        m_port = port;

        InstrumentManagerClientLocalImpl client = new InstrumentManagerClientLocalImpl( manager );

        System.out.println( "Creating CompleteSocketCustomStreamServer..." );
        m_server = new CompleteSocketCustomStreamServer.WithSimpleDefaults( port );

        System.out.println( "Publishing InstrumentManagerClient..." );

        Class[] additionalFacadeClasses = new Class[]
        {
            InstrumentableDescriptor.class,
            InstrumentDescriptor.class,
            InstrumentSampleDescriptor.class
        };

        m_server.publish( client, "InstrumentManagerClient",
            new PublicationDescription( InstrumentManagerClient.class, additionalFacadeClasses ) );

        System.out.println( "Starting CompleteSocketObjectStreamServer..." );
        m_server.start();
        System.out.println( "Started on port: " + port );
    }

    /*---------------------------------------------------------------
     * Disposable Methods
     *-------------------------------------------------------------*/
    public void dispose()
    {
        m_server.stop();
        m_server = null;
    }
}

