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

 4. The names "Jakarta", "Apache Avalon", "Avalon Cornerstone", "Avalon
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

package org.apache.avalon.cornerstone.blocks.rmification;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.cornerstone.services.rmification.RMIfication;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * FIXME: INPROGRESS and NOT TESTED
 * Default implementation of <code>RMIfication</code>.
 *
 * @phoenix:block
 * @phoenix:service name="org.apache.avalon.cornerstone.services.rmification.RMIfication"
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 * @author Mauro Talevi
 * @version $Revision: 1.15 $
 */
public class DefaultRMIfication
    extends AbstractLogEnabled
    implements Configurable, Initializable, Disposable, RMIfication
{
    private static final boolean DEFAULT_CREATE_REGISTRY = true;

    private boolean m_createRegistry;
    private int m_port;
    private Registry m_registry;
    private Map m_remotes;

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_createRegistry = configuration.getChild( "createRegistry", true ).getValueAsBoolean( DEFAULT_CREATE_REGISTRY );
        m_port = configuration.getChild( "port", true ).getValueAsInteger( Registry.REGISTRY_PORT );
    }

    public void initialize()
        throws Exception
    {
        m_remotes = new HashMap();
        // check if SecurityManager is set
        if( System.getSecurityManager() == null )
        {
            System.setSecurityManager( new RMISecurityManager() );
            if( getLogger().isInfoEnabled() )
            {
                final String message = "RMISecurityManager set";
                getLogger().info( message );
            }
        }

        if( m_createRegistry )
        {
            m_registry = LocateRegistry.createRegistry( m_port );
            if( getLogger().isInfoEnabled() )
            {
                final String message = "RMI registry created on port " + m_port;
                getLogger().info( message );
            }
        }
        else
        {
            m_registry = LocateRegistry.getRegistry( m_port );

            if( getLogger().isInfoEnabled() )
            {
                final String message = "Found RMI registry on port " + m_port;
                getLogger().info( message );
            }
        }
    }

    public void dispose()
    {
        m_registry = null;
        m_remotes.clear();
        m_remotes = null;
    }

    public void export( final Remote remote )
        throws RemoteException
    {
        UnicastRemoteObject.exportObject( remote );

        if( getLogger().isDebugEnabled() )
        {
            final String message = "Exported Remote " + remote.toString();
            getLogger().debug( message );
        }
    }

    public void unexport( final Remote remote )
        throws RemoteException
    {
        UnicastRemoteObject.unexportObject( remote, true );

        if( getLogger().isDebugEnabled() )
        {
            final String message = "Unexported Remote " + remote.toString();
            getLogger().debug( message );
        }
    }

    public void publish( final Remote remote, final String publicationName )
        throws RemoteException, MalformedURLException
    {
        synchronized( m_remotes )
        {
            export( remote );
            m_registry.rebind( publicationName, remote );

            m_remotes.put( publicationName, remote );
        }

        if( getLogger().isDebugEnabled() )
        {
            final String message = "Published " + publicationName;
            getLogger().debug( message );
        }
    }

    public void unpublish( final String publicationName )
        throws RemoteException, NotBoundException, MalformedURLException
    {
        synchronized( m_remotes )
        {
            final Remote remote = (Remote)m_remotes.get( publicationName );

            m_registry.unbind( publicationName );
            unexport( remote );

            m_remotes.remove( publicationName );
        }

        if( getLogger().isDebugEnabled() )
        {
            final String message = "Unpublished " + publicationName;
            getLogger().debug( message );
        }
    }
}
