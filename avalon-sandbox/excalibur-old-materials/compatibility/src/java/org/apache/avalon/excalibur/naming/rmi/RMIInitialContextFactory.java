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
 * @author Peter Donald
 * @version $Revision: 1.2 $
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
                ( (NamingProvider)( (MarshalledObject)input.readObject() ).get() );

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

