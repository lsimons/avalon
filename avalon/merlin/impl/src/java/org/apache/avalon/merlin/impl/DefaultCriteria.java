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

package org.apache.avalon.merlin.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Map;

import org.apache.avalon.merlin.KernelCriteria;
import org.apache.avalon.merlin.KernelRuntimeException;

import org.apache.avalon.repository.provider.InitialContext;

import org.apache.avalon.util.defaults.Defaults;
import org.apache.avalon.util.defaults.DefaultsFinder;
import org.apache.avalon.util.defaults.SimpleDefaultsFinder;
import org.apache.avalon.util.defaults.SystemDefaultsFinder;
import org.apache.avalon.util.env.Env;
import org.apache.avalon.util.env.EnvAccessException;
import org.apache.avalon.util.exception.ExceptionHelper;
import org.apache.avalon.util.criteria.CriteriaException;
import org.apache.avalon.util.criteria.Criteria;
import org.apache.avalon.util.criteria.Parameter;
import org.apache.avalon.util.criteria.PackedParameter;


/**
 * A Criteria is a class holding the values supplied by a user 
 * for application to a factory.
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.15 $
 */
public class DefaultCriteria extends Criteria implements KernelCriteria
{
    //--------------------------------------------------------------
    // static
    //--------------------------------------------------------------

    private static final String AVALON_PROPERTIES = "avalon.properties";
    private static final String MERLIN_PROPERTIES = "merlin.properties";

    private static final File USER_DIR = getBaseDirectory();

    private static final File USER_HOME = 
      new File( System.getProperty( "user.home" ) );

    private static final File TEMP_DIR = 
      new File( System.getProperty( "java.io.tmpdir" ) );

    private static final File AVALON_HOME_DIR = 
      getAvalonHomeDirectory();

    private static final File MERLIN_HOME_DIR = 
      getMerlinHomeDirectory();

   /**
    * Return the avalon home directory using the ${avalon.home}, AVALON_HOME,
    * and fallback ${user.home}/.avalon as the search order.
    * @return the avalon home directory
    */
    private static File getAvalonHomeDirectory()
    {
        return getEnvironment( "AVALON_HOME", "avalon.home", ".avalon" );
    }

   /**
    * Return the merlin home directory using the ${merlin.home}, MERLIN_HOME,
    * and fallback ${user.home}/.merlin as the search order.
    * @return the merlin home directory
    */
    private static File getMerlinHomeDirectory()
    {
        return getEnvironment( "MERLIN_HOME", "merlin.home", ".merlin" );
    }

   /**
    * Return a directory taking into account a supplied env symbol, 
    * a property key and a property filename.  Use the supplied key
    * to locate a system property with falback to the supplied 
    * sysbol, with fallback to the supplied path relative to ${user.home}.
    * 
    * @return the derived directory
    */
    private static File getEnvironment( String symbol, String key, String path )
    {
        try
        {
            String home = 
              System.getProperty( key, Env.getEnvVariable( symbol ) );

            if( null != home ) return new File( home ).getCanonicalFile();

            return new File(
              System.getProperty( "user.home" ) 
              + File.separator 
              + path ).getCanonicalFile();

        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while attempting to access symbol [" 
              + symbol 
              + "] environment variable.";
            final String message = 
              ExceptionHelper.packException( error, e, true );
            throw new RuntimeException( message );
        }
    }

   /**
    * The factory parameters template.
    * @return the set of parameters constraining the criteria
    */
    private static Parameter[] buildParameters( InitialContext context )
    { 
        return new Parameter[]{
            new Parameter( 
              MERLIN_REPOSITORY,
              File.class, new File( AVALON_HOME_DIR, "repository" ) ),
            new Parameter( 
              MERLIN_HOME,
              File.class, MERLIN_HOME_DIR ),
            new Parameter( 
              MERLIN_SYSTEM,
              File.class, new File( MERLIN_HOME_DIR, "system" ) ),
            new Parameter( 
              MERLIN_CONFIG,
              File.class, new File( MERLIN_HOME_DIR, "config" ) ),
            new PackedParameter( 
              MERLIN_INSTALL, ",", new String[0] ),
            new PackedParameter( 
              MERLIN_DEPLOYMENT, ",", new String[0] ),
            new Parameter( 
              MERLIN_KERNEL, URL.class, null ),
            new Parameter( 
              MERLIN_OVERRIDE, String.class, null ),
            new Parameter( 
              MERLIN_DIR, File.class, context.getInitialWorkingDirectory() ),
            new Parameter( 
              MERLIN_TEMP, File.class, TEMP_DIR ),
            new Parameter( 
              MERLIN_CONTEXT, File.class, null ),
            new Parameter( 
              MERLIN_ANCHOR, File.class, null ),
            new Parameter( 
              MERLIN_INFO, Boolean.class, new Boolean( false ) ),
            new Parameter( 
              MERLIN_DEBUG, Boolean.class, new Boolean( false ) ),
            new Parameter( 
              MERLIN_AUDIT, Boolean.class, new Boolean( false ) ),
            new Parameter( 
              MERLIN_SERVER, Boolean.class, new Boolean( true ) ),
            new Parameter( 
              MERLIN_AUTOSTART, Boolean.class, new Boolean( true ) ),
            new Parameter( 
              MERLIN_LANG, String.class, null )
         };
    }

    //--------------------------------------------------------------
    // immutable state
    //--------------------------------------------------------------

    private final InitialContext m_context;

    //--------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------

   /**
    * Creation of a new default kernel criteria.
    */
    public DefaultCriteria( InitialContext context )
    {
        super( buildParameters( context ) );

        m_context = context;

        //
        // static defaults are the most primative
        //

        Properties avalonStatic = getStaticProperties( AVALON_PROPERTIES );
        Properties merlinStatic = getStaticProperties( MERLIN_PROPERTIES );

        //
        // then comes environment variables
        //

        Properties env = getEnvinronment();

        //
        // then the system properites
        //

        Properties system = System.getProperties();

        //
        // get the application properties
        //

        Properties avalonSystem = 
          getLocalProperties( getAvalonHomeDirectory(), AVALON_PROPERTIES );
        Properties merlinSystem = 
          getLocalProperties( getMerlinHomeDirectory(), MERLIN_PROPERTIES );

        //
        // ${user.home} overrides environment
        //

        Properties avalonHome = 
          getLocalProperties( USER_HOME, AVALON_PROPERTIES );
        Properties merlinHome = 
          getLocalProperties( USER_HOME, MERLIN_PROPERTIES );

        //
        // and ${merlin.dir} overrides ${user.home}
        //

        File work = getWorkingDirectory();
        Properties avalonWork = 
          getLocalProperties( work, AVALON_PROPERTIES );
        Properties merlinWork = 
          getLocalProperties( work, MERLIN_PROPERTIES );

        //
        // Create the finder (discovery policy), construct the defaults, and
        // macro expand the values.
        //

        final Properties[] parameters = 
          new Properties[] { 
            avalonStatic, 
            merlinStatic, 
            avalonSystem, 
            merlinSystem, 
            env, 
            avalonHome, 
            avalonWork, 
            merlinHome, 
            merlinWork };

        final DefaultsFinder[] finders = 
          new DefaultsFinder[]{
            new SimpleDefaultsFinder( 
              parameters, 
              false ), 
            new SystemDefaultsFinder() 
          };
        
        Defaults defaults = 
          new Defaults( 
             Parameter.getKeys( super.getParameters() ), new String[0], finders );

        //printProperties( defaults, "defaults" );

        //
        // add ${merlin.dir} to assist in synbol expansion then expand
        // symbols (done twice to handle nested defintions)
        //

        defaults.setProperty( "merlin.dir", work.toString() );
        Defaults.macroExpand( defaults, new Properties[]{ system, avalonStatic, env } );
        Defaults.macroExpand( defaults, new Properties[]{ system, avalonStatic, env } );

        //
        // following aquistion of the default parameters we need to assign
        // them as criteria values before we expose the criteria instance to the
        // client
        //

        put( "merlin.dir", work.toString() );
        ArrayList errors = new ArrayList();
        Parameter[] params = super.getParameters();
        for( int i=0; i<params.length; i++ )
        {
            Parameter param = params[i];
            final String key = param.getKey();
            if( !key.equals( "merlin.dir" ) && !key.equals( "merlin.implementation" ))
            {
                try
                {
                    put( key, defaults.getProperty( key ) );
                }
                catch( Exception re )
                {
                    errors.add( re );
                }
            }
        }

        //
        // check for any errors created in the process and dump a 
        // notice to System.err
        //

        if( errors.size() > 0 )
        {
            Throwable[] throwables = 
              (Throwable[]) errors.toArray( new Throwable[0] );
            
            if( errors.size() > 1 )
            {
                final String report = 
                  "Multiple errors (ignored) while resolving defaults.";
                String message = 
                  ExceptionHelper.packException( report, throwables, false );
                System.err.println( message );
            }
            else
            {
                final String report = 
                  "One error (ignored) occured while resolving defaults.";
                String message = 
                  ExceptionHelper.packException( report, throwables[0], false );
                System.err.println( message );
            }
        }
    }

    //--------------------------------------------------------------
    // Criteria
    //--------------------------------------------------------------

   /**
    * Return a string representation of the kernel criteria.
    * @return the criteria as a string
    */
    public String toString()
    {
        return super.toString();
    }

    //--------------------------------------------------------------
    // KernelCriteria
    //--------------------------------------------------------------

   /**
    * Return the root directory to the shared repository.
    * @return the avalon home root repository directory
    */
    public File getRepositoryDirectory()
    {
        return (File) get( MERLIN_REPOSITORY );
    }

   /**
    * Return the lang code.  A null value indicates that the 
    * default language applies.
    * @return the language code
    */
    public String getLanguageCode()
    {
        return (String) get( MERLIN_LANG );
    }

   /**
    * Return the root directory to the merlin installation
    * @return the merlin home directory
    */
    public File getHomeDirectory()
    {
        return (File) get( MERLIN_HOME );
    }

   /**
    * Return the root directory to the merlin system repository
    * @return the merlin system repository directory
    */
    public File getSystemDirectory()
    {
        return (File) get( MERLIN_SYSTEM );
    }

   /**
    * Return the sequence of deployment urls
    * @return the block urls
    */
    public URL[] getDeploymentURLs()
    {
        String[] blocks = (String[]) get( MERLIN_DEPLOYMENT );

        ArrayList list = new ArrayList();
        File base = getWorkingDirectory();
        for( int i=0; i<blocks.length; i++ )
        {
            String path = blocks[i];
            try
            {
                URL url = resolveURL( base, path );
                list.add( url );
            }
            catch( Throwable e )
            {
                final String error = 
                 "Unable to transform the token: ["
                 + path
                 + "] due to an unexpected error.";
                throw new KernelRuntimeException( error, e );
            }
        }
        return (URL[]) list.toArray( new URL[0] ); 
    }

   /**
    * Return the root directory to the merlin configurations
    * @return the merlin configuration directory
    */
    public File getConfigDirectory()
    {
        return (File) get( MERLIN_CONFIG );
    }

   /**
    * Return the url to the kernel configuration
    * @return the kernel configuration url
    */
    public URL getKernelURL()
    {
        URL url = (URL) get( MERLIN_KERNEL );
        if( null != url ) return url;
        File conf = getConfigDirectory();
        if( null == conf ) return null;
        File kernel = new File( conf, "kernel.xml" );
        if( kernel.exists() )
        {
            return toURL( kernel );
        }
        else
        {
            return null;
        }
    }

   /**
    * Return the url to the configuration override targets.
    * @return the override url
    */
    public String getOverridePath()
    {
        return (String) get( MERLIN_OVERRIDE );
    }

   /**
    * Return the working client directory.
    * @return the working directory
    */
    public File getWorkingDirectory()
    {
        return m_context.getInitialWorkingDirectory();
    }

   /**
    * Return the temporary directory.
    * @return the temp directory
    */
    public File getTempDirectory()
    {
        return (File) get( MERLIN_TEMP );
    }

   /**
    * Return the context directory from which relative 
    * runtime home directories will be established for 
    * components referencing urn:avalon:home
    *
    * @return the context directory
    */
    public File getContextDirectory()
    {
        File context = (File) get( MERLIN_CONTEXT );
        if( null == context )
        {
            return new File( getWorkingDirectory(), "home" );
        }
        else
        {
            return resolveWorkingFile( context );
        }
    }

   /**
    * Return the anchor directory to be used when resolving 
    * library declarations in classload specifications.
    *
    * @return the anchor directory
    */
    public File getAnchorDirectory()
    {
        File anchor = (File) get( MERLIN_ANCHOR );
        return resolveWorkingFile( anchor );
    }

   /**
    * Return info generation policy.  If TRUE the parameters 
    * related to deployment will be listed on startup. 
    *
    * @return the info policy
    */
    public boolean isInfoEnabled()
    {
        Boolean value = (Boolean) get( MERLIN_INFO );
        if( null != value ) return value.booleanValue();
        return false;
    }

   /**
    * Return debug policy.  If TRUE all logging channels will be 
    * set to debug level (useful for debugging).
    *
    * @return the debug policy
    */
    public boolean isDebugEnabled()
    {
        Boolean value = (Boolean) get( MERLIN_DEBUG );
        if( null != value ) return value.booleanValue();
        return false;
    }

   /**
    * Return audit policy.  If TRUE a model listing will be generated.
    *
    * @return the audit policy
    */
    public boolean isAuditEnabled()
    {
        Boolean value = (Boolean) get( MERLIN_AUDIT );
        if( null != value ) return value.booleanValue();
        return false;
    }

   /**
    * Return the autostart policy.  If TRUE (the default) the 
    * deployment of the application container will be initiated
    * following kernel initialization.
    *
    * @return the autostart policy
    */
    public boolean isAutostartEnabled()
    {
        Boolean value = (Boolean) get( MERLIN_AUTOSTART );
        if( null != value ) return value.booleanValue();
        return true;
    }

   /**
    * Return server execution policy.  If TRUE the kernel will 
    * continue until explicitly terminated.  If FALSE the kernel
    * will initiate decommissioning on completion of deployment.
    *
    * @return the server execution mode
    */
    public boolean isServerEnabled()
    {
        Boolean value = (Boolean) get( MERLIN_SERVER );
        if( null != value ) return value.booleanValue();
        return false;
    }

    //--------------------------------------------------------------
    // internal
    //--------------------------------------------------------------

    private File resolveWorkingFile( File file )
    {
        if( null == file ) return getWorkingDirectory();
        if( file.isAbsolute() ) return getCanonicalForm( file );
        
        File relative = new File( getWorkingDirectory(), file.toString() );
        return getCanonicalForm( relative );
    }

    private void printProperties( Properties properties, String label )
    {
        System.out.print( "\n------------ " 
          + label.toUpperCase()
          + "--------------------------------".substring( label.length() ) 
          + "\n\n" );
        if( null == properties ) return;
        Enumeration names = properties.propertyNames();
        while( names.hasMoreElements() )
        {
            String name = (String) names.nextElement();
            System.out.println( "   ${" + name + "} == " 
            + properties.getProperty( name ) );
        }
    }

    private Properties getEnvinronment()
    {
        try
        {
            Properties properties = new Properties();
            setProperty( 
              properties, "avalon.home", 
              Env.getEnvVariable( "AVALON_HOME" ) );
            setProperty( 
              properties, "merlin.home", 
              Env.getEnvVariable( "MERLIN_HOME" ) );
            setProperty( 
              properties, "maven.home.local", 
              Env.getEnvVariable( "MAVEN_HOME_LOCAL" ) );
            return properties;
        }
        catch( Throwable e )
        {
            final String error =
              "Internal error occured while attempting to access system environment.";
            throw new KernelRuntimeException( error, e );
        }
    }

    private void setProperty( 
      Properties properties, String key, String value )
    {
        if( null != value ) properties.setProperty( key, value );
    }

    private Properties getLocalProperties( 
      File dir, String filename ) 
    {
        Properties properties = new Properties();
        if( null == dir ) return properties;
        File file = new File( dir, filename );
        if( !file.exists() ) return properties;
        try
        {
            properties.load( new FileInputStream( file ) );
            return properties;
        }
        catch( Throwable e )
        {
            final String error = 
              "Unexpected exception while attempting to read properties from: " 
              + file;
            throw new KernelRuntimeException( error, e );
        }
    }

   /**
    * Read in the static defined properties that contribute to 
    * the default context value establishment.
    *
    * @return the static default properties
    * @exception KernelRuntimeException if a error occurs while attempt to 
    *    load the property resource
    */
    private Properties getStaticProperties( String path ) 
      throws KernelRuntimeException
    {
        try
        {
            final String resource = "/" + path;
            return Defaults.getStaticProperties( 
              DefaultCriteria.class, resource );
        }
        catch ( IOException e )
        {
            throw new KernelRuntimeException( 
             "Failed to load implementation defaults resource: /"
             + path, e );
        }
    }

    private URL resolveURL( File base, String value ) throws Exception
    {
        if( value.startsWith( "block:" ) )
        {
            return new URL( null, value, new BlockHandler() );
        }
        else if( value.startsWith( "artifact:" ) )
        {
            return new URL( null, value, new ArtifactHandler() );
        }

        try
        {
            return new URL( value );
        }
        catch( Exception e )
        {
            File target = new File( value );
            if( target.exists() )
            {
                return toURL( target );
            }
            else
            {
                target = new File( base, value );
                if( target.exists() )
                {
                    return toURL( target );
                }
                else
                {
                    final String error = 
                      "Unable to resolve the block path [" + value + "].";
                    throw new KernelRuntimeException( error, e );
                }
            }
        }
    }

    private URL toURL( File file )
    {
        if( null == file ) throw new NullPointerException( "file" );
        try
        {
            return file.getCanonicalFile().toURL();
        }
        catch( Throwable e )
        {
            final String error = 
              "Unable to transform the file [" 
                  + file.toString()
                  + "] to a URL.";
            throw new KernelRuntimeException( error, e );
        }
    }

    private static File getBaseDirectory()
    {
        String base = System.getProperty( "basedir" );
        if( null != base )
        {
            return getCanonicalForm( new File( base ) );
        }
        return getCanonicalForm( 
          new File( System.getProperty( "user.dir" ) ) );
    }

    private static File getCanonicalForm( File file )
    {
        try
        {
            return file.getCanonicalFile();
        }
        catch( Throwable e )
        {
            final String error =
              "Unable to resolve cononical representation of: "
              + file; 
            throw new KernelRuntimeException( error, e );
        }
    }
}
