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

package org.apache.avalon.repository.main;

import java.io.File;
import java.text.ParseException;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.ArrayList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import javax.naming.directory.Attributes;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.Repository;
import org.apache.avalon.repository.RepositoryException;
import org.apache.avalon.repository.RepositoryRuntimeException;
import org.apache.avalon.repository.meta.FactoryDescriptor;
import org.apache.avalon.repository.provider.Builder;
import org.apache.avalon.repository.provider.InitialContext;
import org.apache.avalon.repository.provider.CacheManager;
import org.apache.avalon.repository.provider.Factory;
import org.apache.avalon.util.env.Env;
import org.apache.avalon.util.exception.ExceptionHelper;


/**
 * Application and component bootstrapper used to instantiate, and or invoke
 * Classes and their methods within newly constructed Repository ClassLoaders.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: mcconnell $
 * @version $Revision: 1.6 $
 */
public class DefaultBuilder extends AbstractBuilder implements Builder
{
    //-----------------------------------------------------------
    // state
    //-----------------------------------------------------------

   /**
    * The repository established by the loader that will be used 
    * to cache the resources needed to establish the application 
    * classloader.
    */
    private final Repository m_repository;
    
   /**
    * The classloader established by the loader used to load the 
    * the application factory.
    */
    private final ClassLoader m_classloader;
    
   /**
    * The initial repository factory that may be supplied to 
    * application factories as a constructor argument for applications
    * that need to create their own repositories.
    */
    private final InitialContext m_context;
    
   /**
    * The application factory established by the loader to which 
    * requests for default criteria and instance creation are 
    * delegated.
    */
    private final Factory m_delegate;
    
   /**
    * Creates a DefaultBuilder for a specific target application.
    * 
    * @param context the initial repository context
    * @param artifact the reference to the application
    * @exception RepositoryException if a app factory creation error occurs
    */
    public DefaultBuilder( InitialContext context, Artifact artifact )
        throws Exception
    {
        this( 
          context, 
          Thread.currentThread().getContextClassLoader(),
          artifact );
    }

   /**
    * Creates a DefaultBuilder for a specific target application.
    * 
    * @param context the initial repository context
    * @param classloader the parent classloader
    * @param artifact the reference to the application
    * @exception Exception if a app factory creation error occurs
    */
    public DefaultBuilder( 
      InitialContext context, ClassLoader classloader, Artifact artifact )
      throws Exception
    {
        if( null == context ) throw new NullPointerException( "context" );
        if( null == artifact ) throw new NullPointerException( "artifact" );

        m_context = context;

        ClassLoader parent = getClassLoader( classloader );

        try
        {
            Factory factory = m_context.getInitialFactory();
            CacheManager manager = (CacheManager)factory.create();
            m_repository = manager.createRepository();
        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while attempt to construct initial repository.";
            throw new RepositoryException( error, e );           
        }

        Attributes attributes = m_repository.getAttributes( artifact );
        FactoryDescriptor descriptor = new FactoryDescriptor( attributes );
        String classname = descriptor.getFactory();
        if( null == classname ) 
        {
            final String error = 
              "Required property 'avalon.artifact.factory' not present in artifact: ["
              + artifact + "] under the active repository: [" + m_repository + "].";
            throw new IllegalArgumentException( error );
        }

        m_classloader = m_repository.getClassLoader( parent, artifact );
        Class clazz = loadFactoryClass( m_classloader, classname );

        try
        {
            m_delegate = createDelegate( m_classloader, classname, m_context );
        }
        catch( Throwable e )
        {
            final String error = 
              "Unable to establish a factory for the supplied artifact:";
            StringBuffer buffer = new StringBuffer( error );
            buffer.append( "\n artifact: " + artifact );
            buffer.append( "\n build: " + descriptor.getBuild() );
            buffer.append( "\n factory: " + descriptor.getFactory() );
            buffer.append( "\n source: " + clazz.getProtectionDomain().getCodeSource().getLocation() );
            buffer.append( "\n repository: " + m_repository );
            throw new RepositoryException( buffer.toString(), e );
        }
    }

    private ClassLoader getClassLoader( ClassLoader classloader )
    {
        if( null != classloader ) return classloader;
        return DefaultBuilder.class.getClassLoader();
    }


   /**
    * Return the factory established by the loader.
    * @return the delegate factory
    */
    public Factory getFactory()
    {
        return m_delegate;
    }

    /**
     * Gets the ClassLoader used by this Bootstrapper.
     * 
     * @return the ClassLoader built by the Repository
     */
    public ClassLoader getClassLoader()
    {
        return m_classloader;
    }
    
    /**
     * Main wrapper.
     * 
     * TODO add more properties to allow full repo specification via 
     * system properties including a repository implementation replacement.
     * 
     * @param args the command line arguments
     */
    public static void main( String [] args )
    {
        String spec = getArtifactSpec( args );
        if( null == spec )
        {
            final String error = 
              "Cannot resolve an artifict target.";
              System.out.println( error );
            System.exit( -1 );
        }

        File cache = getCache( args );
        String[] hosts = getHosts( args );
        Artifact artifact = Artifact.createArtifact( spec );

        try
        {
            InitialContext context = new DefaultInitialContext( cache, hosts );
            System.out.println( "Building: " + artifact );
            Builder builder = new DefaultBuilder( context, artifact );
            Object object = builder.getFactory().create();
            System.out.println( "OBJECT: " + object );
        }
        catch ( Throwable e )
        {
            String message = ExceptionHelper.packException( e, true );
            System.out.println( message );
            System.exit( -1 );
        }
    }

    private static String getArtifactSpec( String[] args )
    {
        String artifact = getArgument( "-artifact", args );
        if( null != artifact )
        {
            return artifact;
        }
        else
        {
            return null;
        }
    }

    private static File getCache( String[] args )
    {
        String cache = getArgument( "-cache", args );
        if( null != cache )
        {
            return new File( cache );
        }
        else
        {
            return null;
        }
    }

    private static String[] getHosts( String[] args )
    {
        String hosts = getArgument( "-hosts", args );
        if( null != hosts )
        {
            return expandHosts( hosts );
        }
        else
        {
            return null;
        }
    }

    private static String getArgument( String key, String[] args )
    {
        for( int i=0; i<args.length; i++ )
        {
            if( args[i].equals( key ) )
            {
                if( args.length >= i+1 )
                {
                    return args[ i+1 ];
                }
                else
                {
                    final String error = 
                      "Missing CLI value for parameter: " + key;
                    throw new IllegalArgumentException( error );
                }
            }
        }
        return null;
    }

    private static String[] expandHosts( String arg )
    {
        ArrayList list = new ArrayList();
        StringTokenizer tokenizer = new StringTokenizer( arg, "," );
        while( tokenizer.hasMoreTokens() )
        {
            list.add( tokenizer.nextToken() );
        }
        return (String[]) list.toArray( new String[0] );
    }

    private static String getMavenRepositoryURI()
    {
        String home = getMavenHome();
        return "file:///" + getMavenHomeRepository();
    }

    private static String getMavenHomeRepository()
    {
        return getMavenHome() + File.separator + "repository";
    }

    private static String getMavenHome()
    {
        try
        {
            String local = 
              System.getProperty( 
                "maven.home.local", 
                Env.getEnvVariable( "MAVEN_HOME_LOCAL" ) );
            if( null != local ) return local;

            String maven = 
              System.getProperty( 
                "maven.home", 
                Env.getEnvVariable( "MAVEN_HOME" ) );
            if( null != maven ) return maven;

            return System.getProperty( "user.home" ) + File.separator + ".maven";

        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while attempting to access environment: " + e.toString();
            throw new Error( error );
        }
    }
}
