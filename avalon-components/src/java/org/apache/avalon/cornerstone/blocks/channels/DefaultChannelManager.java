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

package org.apache.avalon.cornerstone.blocks.channels;

import java.util.HashMap;
import org.apache.avalon.cornerstone.services.channels.ChannelManager;
import org.apache.avalon.cornerstone.services.channels.ServerChannelFactory;
import org.apache.avalon.cornerstone.services.channels.SocketChannelFactory;
import org.apache.avalon.framework.CascadingException;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * Implementation of ChannelManager.
 *
 * @phoenix:block
 * @phoenix:service name="org.apache.avalon.cornerstone.services.channels.ChannelManager"
 *
 * @author <a href="mailto:khoehn@smartstream.net">Kurt R. Hoehn</a>
 */
public class DefaultChannelManager
    extends AbstractLogEnabled
    implements ChannelManager, Contextualizable, Configurable, Initializable
{
    private final HashMap m_serverChannels = new HashMap();
    private final HashMap m_socketChannels = new HashMap();

    private Context m_context;
    private Configuration m_configuration;

    public void contextualize( final Context context )
        throws ContextException
    {
        m_context = context;
    }

    /**
     * Configure the ChannelManager.
     *
     * @param configuration the Configuration
     * @exception ConfigurationException if an error occurs
     */
    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_configuration = configuration;
    }

    public void initialize()
        throws Exception
    {
        final Configuration[] serverChannels = m_configuration.getChild( "server-channels" ).getChildren( "factory" );
        for( int i = 0; i < serverChannels.length; i++ )
        {
            final Configuration element = serverChannels[ i ];
            final String name = element.getAttribute( "name" );
            final String className = element.getAttribute( "class" );

            setupServerChannelFactory( name, className, element );
        }

        final Configuration[] socketChannels = m_configuration.getChild( "client-channels" ).getChildren( "factory" );
        for( int i = 0; i < socketChannels.length; i++ )
        {
            final Configuration element = socketChannels[ i ];
            final String name = element.getAttribute( "name" );
            final String className = element.getAttribute( "class" );

            setupSocketChannelFactory( name, className, element );
        }
    }

    protected void setupServerChannelFactory( final String name,
                                              final String className,
                                              final Configuration configuration )
        throws Exception
    {
        final Object object = createFactory( name, className, configuration );

        if( !( object instanceof ServerChannelFactory ) )
        {
            throw new Exception( "Error creating factory " + name +
                                 " with class " + className + " as " +
                                 "is does not implement the correct " +
                                 "interface (ServerChannelFactory)" );
        }

        m_serverChannels.put( name, object );
    }

    protected void setupSocketChannelFactory( final String name,
                                              final String className,
                                              final Configuration configuration )
        throws Exception
    {
        final Object object = createFactory( name, className, configuration );

        if( !( object instanceof SocketChannelFactory ) )
        {
            throw new Exception( "Error creating factory " + name +
                                 " with class " + className + " as " +
                                 "is does not implement the correct " +
                                 "interface (SocketChannelFactory)" );
        }

        m_socketChannels.put( name, object );
    }

    protected Object createFactory( final String name,
                                    final String className,
                                    final Configuration configuration )
        throws Exception
    {
        Object factory;
        try
        {
            final ClassLoader classLoader =
                (ClassLoader)Thread.currentThread().getContextClassLoader();
            final Class clazz = classLoader.loadClass( className );
            factory = (Object)clazz.newInstance();
        }
        catch( final Exception e )
        {
            final String message =
                "Error creating factory with class " + className;
            throw new CascadingException( message, e );
        }

        ContainerUtil.enableLogging( factory, getLogger() );
        ContainerUtil.contextualize( factory, m_context );
        ContainerUtil.configure( factory, configuration );
        ContainerUtil.initialize( factory );

        return factory;
    }

    /**
     * Retrieve a server channel factory by name.
     *
     * @param name the name of server channel factory
     * @return the ServerChannelFactory
     * @exception Exception if server channel factory is not available
     */
    public ServerChannelFactory getServerChannelFactory( String name )
        throws Exception
    {
        final ServerChannelFactory factory = (ServerChannelFactory)m_serverChannels.get( name );
        if( null != factory )
        {
            return factory;
        }
        else
        {
            final String message =
                "Unable to locate server channel factory named " + name;
            throw new Exception( message );
        }
    }

    /**
     * Retrieve a client socket channel by name.
     *
     * @param name the name of client socket channel factory
     * @return the SocketChannelFactory
     * @exception Exception if socket channel factory is not available
     */
    public SocketChannelFactory getSocketChannelFactory( String name )
        throws Exception
    {
        final SocketChannelFactory factory = (SocketChannelFactory)m_socketChannels.get( name );
        if( null != factory )
        {
            return factory;
        }
        else
        {
            final String message =
                "Unable to locate socket channel factory named " + name;
            throw new Exception( message );
        }
    }
}
