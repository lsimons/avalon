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

import org.apache.avalon.util.defaults.Defaults;
import org.apache.avalon.util.defaults.DefaultsFinder;
import org.apache.avalon.util.defaults.SimpleDefaultsFinder;
import org.apache.avalon.util.defaults.SystemDefaultsFinder;
import org.apache.avalon.util.factory.impl.Criteria ;
import org.apache.avalon.util.factory.impl.Parameter ;


/**
 * A Criteria is a class holding the values supplied by a user 
 * for application to a factory.
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.2 $
 */
public class RepositoryCriteria extends Criteria
{
    //--------------------------------------------------------------
    // static
    //--------------------------------------------------------------

   /**
    * Repository cache directory parameter descriptor.
    */
    public static final String REPOSITORY_CACHE_DIR = "avalon.repository.cache.dir";
    private static final Parameter REPOSITORY_CACHE_DIR_PARAM = 
      new Parameter( 
        REPOSITORY_CACHE_DIR,
        File.class,
        null );

   /**
    * Repository proxy host parameter descriptor.
    */
    public static final String REPOSITORY_PROXY_HOST = "avalon.repository.proxy.host";
    private static final Parameter REPOSITORY_PROXY_HOST_PARAM = 
      new Parameter( 
        REPOSITORY_PROXY_HOST,
        String.class,
        null );

   /**
    * Repository proxy port parameter descriptor.
    */
    public static final String REPOSITORY_PROXY_PORT = "avalon.repository.proxy.port";
    private static final Parameter REPOSITORY_PROXY_PORT_PARAM = 
      new Parameter( 
        REPOSITORY_PROXY_PORT,
        Integer.class,
        null );

   /**
    * Repository proxy username parameter descriptor.
    */
    public static final String REPOSITORY_PROXY_USERNAME = "avalon.repository.proxy.username";
    private static final Parameter REPOSITORY_PROXY_USERNAME_PARAM = 
      new Parameter( 
        REPOSITORY_PROXY_USERNAME,
        String.class,
        null );

   /**
    * Repository proxy password parameter descriptor.
    */
    public static final String REPOSITORY_PROXY_PASSWORD = "avalon.repository.proxy.password";
    private static final Parameter REPOSITORY_PROXY_PASSWORD_PARAM = 
      new Parameter( 
        REPOSITORY_PROXY_PASSWORD,
        String.class,
        null );

   /**
    * Repository proxy password parameter descriptor.
    */
    public static final String REPOSITORY_REMOTE_HOSTS = "avalon.repository.host";
    public static final Parameter REPOSITORY_REMOTE_HOSTS_PARAM = 
      new Parameter( 
        REPOSITORY_REMOTE_HOSTS,
        String[].class,
        null );

   /**
    * The factory parameters template.
    */
    public static final Parameter[] PARAMS = new Parameter[]{
           REPOSITORY_CACHE_DIR_PARAM,
           REPOSITORY_PROXY_HOST_PARAM,
           REPOSITORY_PROXY_PORT_PARAM,
           REPOSITORY_PROXY_USERNAME_PARAM,
           REPOSITORY_PROXY_PASSWORD_PARAM,
           REPOSITORY_REMOTE_HOSTS_PARAM };


   /** 
    * The name of the static defaults property resource.
    */
    public static final String DEFAULTS = "default.properties";

   /** 
    * recognized single keys
    */
    private static final String [] SINGLE_KEYS = {
        REPOSITORY_CACHE_DIR,
        REPOSITORY_PROXY_HOST,
        REPOSITORY_PROXY_PORT,
        REPOSITORY_PROXY_USERNAME,
        REPOSITORY_PROXY_PASSWORD
    };

   /** 
    * regognized multivalue keys
    */
    public static final String [] MULTI_VALUE_KEYS = {
        REPOSITORY_REMOTE_HOSTS
    };

    //--------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------

    /**
     * Creation of a new criteria.
     * @param context the initial context
     */
    public RepositoryCriteria( InitialContext context ) throws RepositoryException
    {
        super( PARAMS );

        Properties bootstrap = getDefaultProperties();

        //
        // Create the finder (discovery policy), construct the defaults, and
        // macro expand the values.
        //

        final DefaultsFinder[] finders = {
            new SimpleDefaultsFinder( new Properties[] { bootstrap }, false ), 
            new SystemDefaultsFinder() };
        
        Defaults defaults = new Defaults( SINGLE_KEYS, MULTI_VALUE_KEYS, finders );
        Defaults.macroExpand( defaults, new Properties[]{ System.getProperties() } );

        //
        // Here we start to populate the empty repository configuration using
        // the values stored in the defaults.
        //

        String cache = 
          defaults.getProperty( REPOSITORY_CACHE_DIR );
        if( null == cache )
        {
            put( 
              REPOSITORY_CACHE_DIR, 
              context.getInitialCacheDirectory() );
        }
        else
        {
            put( 
              REPOSITORY_CACHE_DIR, 
              new File( cache ) );
        }

        try
        {
            String[] hosts = 
              defaults.getEnumerated( REPOSITORY_REMOTE_HOSTS );
            if( null != hosts )
            {
                put( REPOSITORY_REMOTE_HOSTS, hosts );
            }
            else
            {
                put( REPOSITORY_REMOTE_HOSTS, context.getInitialHosts() );
            }
        }
        catch ( Throwable e )
        {
            final String error = 
              "Failed to set remote repositories.";
            throw new RepositoryException( error, e );
        }

        if( defaults.containsKey( REPOSITORY_PROXY_HOST ) )
        {    
            put(
              REPOSITORY_PROXY_HOST, 
              new Integer( defaults.getProperty( REPOSITORY_PROXY_HOST ) ) );

            if( defaults.containsKey( REPOSITORY_PROXY_PORT ) )
            {
                put(
                  REPOSITORY_PROXY_PORT, 
                  new Integer( defaults.getProperty( REPOSITORY_PROXY_PORT ) ) );
            }

            if( defaults.containsKey( REPOSITORY_PROXY_USERNAME ) )
            {
                put(
                  REPOSITORY_PROXY_USERNAME, 
                  defaults.getProperty( REPOSITORY_PROXY_USERNAME ) );
            }

            if( defaults.containsKey( REPOSITORY_PROXY_PASSWORD ) )
            {
                put(
                  REPOSITORY_PROXY_PASSWORD, 
                  defaults.getProperty( REPOSITORY_PROXY_PASSWORD ) );
            }
        }
    }


    //--------------------------------------------------------------
    // Criteria
    //--------------------------------------------------------------

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
            return Defaults.getStaticProperties( RepositoryCriteria.class, DEFAULTS );
        }
        catch ( IOException e )
        {
            throw new RepositoryException( 
             "Failed to load implementation defaults resource: "
             + DEFAULTS, e );
        }
    }
}
