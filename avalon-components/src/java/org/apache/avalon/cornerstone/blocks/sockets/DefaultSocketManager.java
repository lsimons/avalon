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

 4. The names "Jakarta", "Apache Avalon", "Avalon Components", "Avalon
    Framework" and "Apache Software Foundation"  must not be used to endorse
    or promote products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

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

package org.apache.avalon.cornerstone.blocks.sockets;

import java.util.HashMap;
import org.apache.avalon.cornerstone.services.sockets.ServerSocketFactory;
import org.apache.avalon.cornerstone.services.sockets.SocketFactory;
import org.apache.avalon.cornerstone.services.sockets.SocketManager;
import org.apache.avalon.framework.CascadingException;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * Implementation of SocketManager.
 *
 * @phoenix:block
 * @phoenix:service name="org.apache.avalon.cornerstone.services.sockets.SocketManager"
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public class DefaultSocketManager
    extends AbstractLogEnabled
    implements SocketManager, Contextualizable, Configurable, Initializable
{
    protected final HashMap m_serverSockets = new HashMap();
    protected final HashMap m_sockets = new HashMap();

    protected Context m_context;
    protected Configuration m_configuration;

    public void contextualize( final Context context )
    {
        m_context = context;
    }

    /**
     * Configure the SocketManager.
     *
     * @param configuration the Configuration
     * @exception ConfigurationException if an error occurs
     * @phoenix:configuration-schema type="http://relaxng.org/ns/structure/1.0"
     */
    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_configuration = configuration;
    }

    public void initialize()
        throws Exception
    {
        final Configuration[] serverSockets =
            m_configuration.getChild( "server-sockets" ).getChildren( "factory" );

        for( int i = 0; i < serverSockets.length; i++ )
        {
            final Configuration element = serverSockets[ i ];
            final String name = element.getAttribute( "name" );
            final String className = element.getAttribute( "class" );

            setupServerSocketFactory( name, className, element );
        }

        final Configuration[] clientSockets =
            m_configuration.getChild( "client-sockets" ).getChildren( "factory" );

        for( int i = 0; i < clientSockets.length; i++ )
        {
            final Configuration element = clientSockets[ i ];
            final String name = element.getAttribute( "name" );
            final String className = element.getAttribute( "class" );

            setupClientSocketFactory( name, className, element );
        }
    }

    protected void setupServerSocketFactory( final String name,
                                             final String className,
                                             final Configuration configuration )
        throws Exception
    {
        final Object object = createFactory( name, className, configuration );

        if( !( object instanceof ServerSocketFactory ) )
        {
            throw new Exception( "Error creating factory " + name +
                                 " with class " + className + " as " +
                                 "it does not implement the correct " +
                                 "interface (ServerSocketFactory)" );
        }

        m_serverSockets.put( name, object );
    }

    protected void setupClientSocketFactory( final String name,
                                             final String className,
                                             final Configuration configuration )
        throws Exception
    {
        final Object object = createFactory( name, className, configuration );

        if( !( object instanceof SocketFactory ) )
        {
            throw new Exception( "Error creating factory " + name +
                                 " with class " + className + " as " +
                                 "it does not implement the correct " +
                                 "interface (SocketFactory)" );
        }

        m_sockets.put( name, object );
    }

    protected Object createFactory( final String name,
                                    final String className,
                                    final Configuration configuration )
        throws Exception
    {
        Object factory = null;

        try
        {
            final ClassLoader classLoader =
                Thread.currentThread().getContextClassLoader();
            factory = classLoader.loadClass( className ).newInstance();
        }
        catch( final Exception e )
        {
            throw new CascadingException( "Error creating factory with class " +
                                          className, e );
        }

        ContainerUtil.enableLogging( factory, getLogger() );
        ContainerUtil.contextualize( factory, m_context );
        ContainerUtil.configure( factory, configuration );
        ContainerUtil.initialize( factory );

        return factory;
    }

    /**
     * Retrieve a server socket factory by name.
     *
     * @param name the name of server socket factory
     * @return the ServerSocketFactory
     * @exception Exception if server socket factory is not available
     */
    public ServerSocketFactory getServerSocketFactory( String name )
        throws Exception
    {
        final ServerSocketFactory factory = (ServerSocketFactory)m_serverSockets.get( name );

        if( null != factory )
        {
            return factory;
        }
        else
        {
            throw new Exception( "Unable to locate server socket factory " +
                                 "named " + name );
        }
    }

    /**
     * Retrieve a client socket factory by name.
     *
     * @param name the name of client socket factory
     * @return the SocketFactory
     * @exception Exception if socket factory is not available
     */
    public SocketFactory getSocketFactory( final String name )
        throws Exception
    {
        final SocketFactory factory = (SocketFactory)m_sockets.get( name );

        if( null != factory )
        {
            return factory;
        }
        else
        {
            throw new Exception( "Unable to locate client socket factory " +
                                 "named " + name );
        }
    }
}
