/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
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

package org.apache.avalon.repository.impl ;


import java.io.File ;
import java.io.IOException;
import java.io.InputStream;

import java.util.Map ;
import java.util.ArrayList ;
import java.util.Properties ;
import java.net.Authenticator ;
import java.net.MalformedURLException ;
import java.net.URL ;

import org.apache.avalon.util.defaults.Defaults ;
import org.apache.avalon.util.defaults.DefaultsFinder ;
import org.apache.avalon.util.defaults.SimpleDefaultsFinder ;
import org.apache.avalon.util.defaults.SystemDefaultsFinder ;
import org.apache.avalon.util.factory.Factory ;

import org.apache.avalon.repository.Repository ;
import org.apache.avalon.repository.RepositoryException ;
import org.apache.avalon.repository.RepositoryRuntimeException;
import org.apache.avalon.repository.provider.InitialContext ;
import org.apache.avalon.repository.provider.CacheManager ;
import org.apache.avalon.repository.util.RepositoryUtils ;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

/**
 * The default repository factory implementation.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.1 $
 */
public class DefaultFactory implements Factory
{
    //--------------------------------------------------------------------------
    // static
    //--------------------------------------------------------------------------

    private static Resources REZ =
        ResourceManager.getPackageResources( DefaultFactory.class );

    private String[] m_hosts;

    //--------------------------------------------------------------------------
    // state
    //--------------------------------------------------------------------------

    private final InitialContext m_context;

    //--------------------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------------------

   /**
    * Creation of a new default repository factory.
    * @param context the initial context
    * @exception NullPointerException if the supplied context is null
    */
    public DefaultFactory( InitialContext context )
    {
        if( null == context ) 
          throw new NullPointerException( "context" ); 
        m_context = context;
    }

    //--------------------------------------------------------------------------
    // Factory
    //--------------------------------------------------------------------------

   /**
    * Create a new instance of the default criteria.
    * @return a new default criteria instance
    */
    public Map createDefaultCriteria()
    {
        try
        {
            return new RepositoryCriteria( m_context );
        }
        catch( Throwable e )
        {
            final String error =
              "Could not create default factory criteria.";
            throw new RepositoryRuntimeException( error, e );
        }
    }

   /**
    * Create a new instance of a repository administrator 
    * using the default parameters.
    * @return the application instance
    * @exception Exception if a repository creation error occurs
    */
    public Object create() throws Exception
    {
        return create( createDefaultCriteria() );
    }

   /**
    * Create a new instance of a repository administrator 
    * using the supplied parameters.
    *
    * @param map a map of repository parameters
    * @return the repository
    */
    public Object create( Map map ) throws Exception
    {
        if( null == map )
          throw new NullPointerException( "map" );

        File root = getCache( map );
        String[] hosts = getHosts( map );
        ProxyContext proxy = createProxyContext( map );

        CacheManager cache = 
          new DefaultCacheManager( root, proxy );

        //
        // TODO: add a criteria key to enable selection of 
        // either the repository or the cache manager as the 
        // service to return
        //

        return cache.createRepository( hosts );
    }

    private File getCache( Map map )
    {
        return (File) map.get( 
            RepositoryCriteria.REPOSITORY_CACHE_DIR );
    }

    private String[] getHosts( Map map )
    {
        return (String[]) map.get( 
            RepositoryCriteria.REPOSITORY_REMOTE_HOSTS );
    }

    private ProxyContext createProxyContext( Map map )
    {
        final String proxyHostName = 
          (String) map.get( 
            RepositoryCriteria.REPOSITORY_PROXY_HOST );

        if( null == proxyHostName )
        {
            return null;
        }
        else
        {    
            final String proxyUsername = 
              (String) map.get( 
                RepositoryCriteria.REPOSITORY_PROXY_USERNAME );

            final String proxyPassword = 
              (String) map.get( 
                RepositoryCriteria.REPOSITORY_PROXY_PASSWORD );

            Authenticator authenticator = 
              new DefaultAuthenticator( proxyUsername, proxyPassword );

            Integer proxyPort = 
              (Integer) map.get( 
                RepositoryCriteria.REPOSITORY_PROXY_PORT );
            if( null == proxyPort ) proxyPort = new Integer( 0 );

            return new ProxyContext( 
                proxyHostName, 
                proxyPort.intValue(), 
                authenticator );
        }

    }
}
