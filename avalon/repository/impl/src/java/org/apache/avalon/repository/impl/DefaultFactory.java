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

import org.apache.avalon.repository.Repository ;
import org.apache.avalon.repository.RepositoryException ;
import org.apache.avalon.repository.RepositoryRuntimeException;
import org.apache.avalon.repository.provider.InitialContext ;
import org.apache.avalon.repository.provider.Factory ;
import org.apache.avalon.repository.util.RepositoryUtils ;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

/**
 * The default repository factory implementation.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.4.2.1 $
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
    * Creation of a new default repository manager factory.
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
            return new DefaultRepositoryCriteria( m_context );
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
    * Create a new instance of a repository 
    * using the supplied parameters.
    *
    * @param map a map of repository parameters
    * @return the repository
    */
    public Object create( Map map ) throws Exception
    {
        if( null == map )
        {
            throw new NullPointerException( "map" );
        }

        File root = getCache( map );
        String[] hosts = getHosts( map );
        return new DefaultRepository( root, hosts );
    }

    private File getCache( Map map )
    {
        return (File) map.get( 
            DefaultRepositoryCriteria.REPOSITORY_CACHE_DIR );
    }

    private String[] getHosts( Map map )
    {
        return (String[]) map.get( 
            DefaultRepositoryCriteria.REPOSITORY_REMOTE_HOSTS );
    }
}
