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

package org.apache.avalon.merlin.tools;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Enumeration;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;
import java.util.ArrayList;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.provider.Builder;
import org.apache.avalon.repository.provider.InitialContext;
import org.apache.avalon.repository.provider.Factory;
import org.apache.avalon.repository.RepositoryException;
import org.apache.avalon.repository.main.DefaultInitialContext;
import org.apache.avalon.repository.main.DefaultBuilder;

import org.apache.avalon.util.env.Env;
import org.apache.avalon.util.exception.ExceptionHelper;

/**
 * Test case that usages the repository builder to deploy the 
 * Merlin default application factory.
 * 
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.12 $
 */
public class MerlinBean
{
    //----------------------------------------------------------
    // static
    //----------------------------------------------------------

    private static final String MERLIN_PROPERTIES = "merlin.properties";

    private static final String IMPLEMENTATION_KEY = "merlin.implementation";

    //----------------------------------------------------------
    // immutable state
    //----------------------------------------------------------

    private String m_deployment;

    private String[] m_hosts;

    private String m_debug;

    private String m_info;

    //----------------------------------------------------------
    // setters
    //----------------------------------------------------------

   /**
    * Set the deployment path.
    */
    public void setDeployment( String path )
    {
        m_deployment = path;
    }

   /**
    * Set the deployment path.
    */
    public void setHosts( String path )
    {
        if( null != path )
        {
            m_hosts = expandHosts( path );
        }
    }

   /**
    * Set the debug policy.
    */
    public void setDebug( String policy )
    {
        m_debug = policy;
    }

   /**
    * Set the info policy.
    */
    public void setInfo( String policy )
    {
        m_info = policy;
    }

   /**
    * Establish the merlin kernel.
    */
    public void doExecute() throws Exception
    {
        ClassLoader classloader = MerlinBean.class.getClassLoader();

        try
        {
            InitialContext context = 
               new DefaultInitialContext( 
                 getMavenRepositoryDirectory(),
                 m_hosts );

            Artifact artifact = 
              DefaultBuilder.createImplementationArtifact( 
                classloader, 
                getMerlinHome(),
                getBaseDirectory(), 
                MERLIN_PROPERTIES, 
                IMPLEMENTATION_KEY );

            Builder builder = 
              new DefaultBuilder( context, artifact );
            Factory factory = builder.getFactory();
            Map criteria = factory.createDefaultCriteria();

            applyLocalProperties( criteria );

            factory.create( criteria );
        }
        catch( Throwable e )
        {
            final String error = ExceptionHelper.packException( e, true );
            System.out.println( error );
            throw new Exception( error );
        }
    }

    //----------------------------------------------------------------------
    // utilities
    //----------------------------------------------------------------------

    private void applyLocalProperties( Map criteria )
    {
        File repository = getMavenRepositoryDirectory();
        criteria.put( "merlin.repository", repository );
        criteria.put( "merlin.server", "false" );
        if( m_deployment != null )
        {
            criteria.put( "merlin.deployment", m_deployment );
        }
        if( null != m_debug ) criteria.put( "merlin.debug", m_debug );
        if( null != m_info ) criteria.put( "merlin.info", m_info ); 
    }

    private static File getMavenRepositoryDirectory()
    {
        return new File( getMavenHome(), "repository" );
    }

    private static File getMavenHome()
    {
        try
        {
            String local = 
              System.getProperty( 
                "maven.home.local", 
                Env.getEnvVariable( "MAVEN_HOME_LOCAL" ) );
            if( null != local ) 
              return new File( local ).getCanonicalFile();

            return new File(
              System.getProperty( "user.home" ) 
              + File.separator 
              + ".maven" ).getCanonicalFile();
        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while attempting to access environment.";
            final String message = 
              ExceptionHelper.packException( error, e, true );
            throw new RuntimeException( message );
        }
    }

    private File getBaseDirectory()
    {
        final String base = System.getProperty( "basedir" );
        if( null != base )
        {
            return new File( base );
        }
        return new File( System.getProperty( "user.dir" ) );
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

   /**
    * Return the merlin home directory.
    * @return the merlin install directory
    */
    private static File getMerlinHome()
    {
        return new File( getMerlinHomePath() );
    }

   /**
    * Return the merlin home directory path.
    * @return the merlin install directory path
    */
    private static String getMerlinHomePath()
    {
        try
        {
            String merlin = 
              System.getProperty( 
                "merlin.home", 
                Env.getEnvVariable( "MERLIN_HOME" ) );
            if( null != merlin ) return merlin;
            return System.getProperty( "user.home" ) 
              + File.separator + ".merlin";
        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while attempting to access MERLIN_HOME environment.";
            final String message = 
              ExceptionHelper.packException( error, e, true );
            throw new RuntimeException( message );
        }
    }

}

