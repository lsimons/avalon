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
package org.apache.avalon.excalibur.naming.rmi;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.rmi.MarshalledObject;
import java.util.Hashtable;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.naming.spi.InitialContextFactory;

import org.apache.avalon.excalibur.naming.DefaultNamespace;
import org.apache.avalon.excalibur.naming.Namespace;
import org.apache.avalon.excalibur.naming.NamingProvider;
import org.apache.avalon.excalibur.naming.RemoteContext;

/**
 * Initial context factory for memorycontext.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.6 $
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/jndikit/
 */
public class RMIInitialContextFactory
        implements InitialContextFactory
{
    public Context getInitialContext( final Hashtable environment )
            throws NamingException
    {
        final NamingProvider provider = newNamingProvider( environment );
        environment.put( RemoteContext.NAMING_PROVIDER, provider );

        final Namespace namespace = newNamespace( environment );
        environment.put( RemoteContext.NAMESPACE, namespace );

        return new RemoteContext( environment, namespace.getNameParser().parse( "" ) );
    }

    protected NamingProvider newNamingProvider( final Hashtable environment )
            throws NamingException
    {
        final String url = (String)environment.get( Context.PROVIDER_URL );
        if( null == url )
        {
            return newNamingProvider( "localhost", 1977 );
        }
        else
        {
            if( !url.startsWith( "rmi://" ) )
            {
                throw new ConfigurationException( "Malformed url - " + url );
            }

            final int index = url.indexOf( ':', 6 );
            int end = index;

            int port = 1977;

            if( -1 == index )
            {
                end = url.length();
            }
            else
            {
                port = Integer.parseInt( url.substring( index + 1 ) );
            }

            final String host = url.substring( 6, end );

            return newNamingProvider( host, port );
        }
    }

    protected NamingProvider newNamingProvider( final String host, final int port )
            throws NamingException
    {
        Socket socket = null;

        try
        {
            socket = new Socket( host, port );

            final ObjectInputStream input =
                    new ObjectInputStream( new BufferedInputStream( socket.getInputStream() ) );

            final NamingProvider provider =
                    ((NamingProvider)((MarshalledObject)input.readObject()).get());

            socket.close();

            return provider;
        }
        catch( final Exception e )
        {
            final ServiceUnavailableException sue =
                    new ServiceUnavailableException( e.getMessage() );
            sue.setRootCause( e );
            throw sue;
        }
        finally
        {
            if( null != socket )
            {
                try
                {
                    socket.close();
                }
                catch( final IOException ioe )
                {
                }
            }
        }
    }

    protected Namespace newNamespace( final Hashtable environment )
            throws NamingException
    {
        try
        {
            final NamingProvider provider =
                    (NamingProvider)environment.get( RemoteContext.NAMING_PROVIDER );

            return new DefaultNamespace( provider.getNameParser() );
        }
        catch( final Exception e )
        {
            if( e instanceof NamingException )
            {
                throw (NamingException)e;
            }
            else
            {
                final ServiceUnavailableException sue =
                        new ServiceUnavailableException( e.getMessage() );
                sue.setRootCause( e );
                throw sue;
            }
        }
    }
}

