/* 
 * Copyright 2004 Apache Software Foundation
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

package org.apache.avalon.repository.impl;

import java.net.Authenticator;

/**
 * A proxy context.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/01/24 23:20:05 $
 */
public final class ProxyContext
{
    /**
     * Host.
     */
     private String m_host;

    /**
     * Port.
     */
     private int m_port;

    /**
     * Authenticator.
     */
     private Authenticator m_authenticator;

    /**
     * Creation of a new proxy context.
     * @param host the host name
     * @param port the port
     * @param authenticator the authenticator
     */
     public ProxyContext( String host, int port, Authenticator authenticator )
     {
         if( host == null ) throw new NullPointerException( "host" );

         m_host = host;
         m_port = port;
         m_authenticator = authenticator;
     }

    /**
     * Returns the proxy authenticator.
     * @return the authenticator
     */
     public Authenticator getAuthenticator()
     {
          return m_authenticator;
     }

    /**
     * Returns the proxy host name.
     * @return the host name
     */
     public String getHost()
     {
          return m_host;
     }

    /**
     * Returns the proxy port number.
     * @return the port
     */
     public String getPort()
     {
          return "" + m_port;
     }
}
