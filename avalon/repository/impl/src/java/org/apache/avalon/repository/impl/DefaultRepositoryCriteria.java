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

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.Hashtable;
import java.util.Map;
import java.net.URL;
import java.net.MalformedURLException;

import org.apache.avalon.repository.RepositoryException;
import org.apache.avalon.repository.provider.InitialContext;
import org.apache.avalon.repository.provider.RepositoryCriteria;

import org.apache.avalon.util.criteria.Criteria;
import org.apache.avalon.util.criteria.Parameter;
import org.apache.avalon.util.criteria.PackedParameter;
import org.apache.avalon.util.defaults.Defaults;
import org.apache.avalon.util.defaults.DefaultsFinder;
import org.apache.avalon.util.defaults.SimpleDefaultsFinder;
import org.apache.avalon.util.defaults.SystemDefaultsFinder;
import org.apache.avalon.util.defaults.DefaultsBuilder;


/**
 * A Criteria is a class holding the values supplied by a user 
 * for application to a factory.
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.6 $
 */
public class DefaultRepositoryCriteria extends Criteria implements RepositoryCriteria
{
    //--------------------------------------------------------------
    // static
    //--------------------------------------------------------------

   /**
    * Repository cache directory parameter descriptor.
    */
    public static final String REPOSITORY_CACHE_DIR = InitialContext.CACHE_KEY;
    private static final Parameter REPOSITORY_CACHE_DIR_PARAM = 
      new Parameter( 
        REPOSITORY_CACHE_DIR,
        File.class,
        null );

   /**
    * Repository proxy password parameter descriptor.
    */
    public static final String REPOSITORY_REMOTE_HOSTS = InitialContext.HOSTS_KEY;
    public static final Parameter REPOSITORY_REMOTE_HOSTS_PARAM = 
      new PackedParameter( 
        REPOSITORY_REMOTE_HOSTS,
        ",",
        null );

   /**
    * The factory parameters template.
    */
    public static final Parameter[] PARAMS = new Parameter[]{
           REPOSITORY_CACHE_DIR_PARAM,
           REPOSITORY_REMOTE_HOSTS_PARAM };

   /** 
    * The name of the static defaults property resource.
    */
    public static final String DEFAULTS = "avalon.properties";

   /** 
    * recognized single keys
    */
    private static final String [] SINGLE_KEYS = Parameter.getKeys( PARAMS );

   /** 
    * recognized multivalue keys
    */
    public static final String [] MULTI_VALUE_KEYS = {};

    //--------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------

    /**
     * Creation of a new criteria.
     * @param context the initial context
     */
    public DefaultRepositoryCriteria( InitialContext context ) 
      throws RepositoryException
    {
        super( PARAMS );

        //
        // create the consolidated properties
        //

        try
        {

            final String key = context.getApplicationKey();
            final File work = context.getInitialWorkingDirectory();
            Properties defaults = getDefaultProperties();
            DefaultsBuilder builder = new DefaultsBuilder( key, work );
            Properties properties = 
              builder.getConsolidatedProperties( defaults, SINGLE_KEYS );

            //
            // Populate the empty repository criteria using
            // the values from the consilidated defaults.
            //

            for( int i=0; i<SINGLE_KEYS.length; i++ )
            {
                final String propertyKey = SINGLE_KEYS[i];
                final String value = properties.getProperty( propertyKey );
                if( null != value )
                {
                    put( propertyKey, value );
                }
            }
        }
        catch( IOException ioe )
        {
            final String error = 
              "Failed to resolve repository parameters.";
            throw new RepositoryException( error, ioe );
        }
    }

    //--------------------------------------------------------------
    // RepositoryCriteria
    //--------------------------------------------------------------

    public void setCacheDirectory( File cache )
    {
        put( REPOSITORY_CACHE_DIR, cache );
    }

    public void setHosts( String[] hosts )
    {
        put( REPOSITORY_REMOTE_HOSTS, hosts );
    }

    public String toString()
    {
        return "[repository: " + super.toString() + "]";
    }

    //--------------------------------------------------------------
    // private
    //--------------------------------------------------------------

    private Properties getDefaultProperties() throws RepositoryException
    {
        try
        {
            return Defaults.getStaticProperties( DefaultRepositoryCriteria.class );
        }
        catch ( IOException e )
        {
            throw new RepositoryException( 
             "Failed to load implementation defaults resource for the class: "
             + DefaultRepositoryCriteria.class.getName(), e );
        }
    }
}
