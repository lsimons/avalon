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
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.7 $ $Date: 2003/07/24 16:15:15 $
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

