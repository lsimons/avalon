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

package org.apache.avalon.repository.main ;

import java.io.File;
import java.net.URL;
import java.util.Map;

import junit.framework.TestCase ;

import org.apache.avalon.repository.Artifact ;
import org.apache.avalon.repository.Repository ;
import org.apache.avalon.repository.RepositoryException ;
import org.apache.avalon.repository.provider.CacheManager ;
import org.apache.avalon.repository.provider.InitialContext ;

import org.apache.avalon.util.env.Env;
import org.apache.avalon.util.exception.ExceptionHelper;
import org.apache.avalon.util.factory.Factory;

/**
 * 
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: mcconnell $
 * @version $Revision: 1.1 $
 */
public class DefaultInitialContextTest extends TestCase
{

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(DefaultInitialContextTest.class);
    }

    /**
     * Constructor for DefaultInitialContextTest.
     * @param name the test name
     */
    public DefaultInitialContextTest( String name )
    {
        super( name );
    }

    public void testRepositoryBootstrap() throws Exception
    {
        InitialContext context = 
          new DefaultInitialContext( 
            getMavenRepositoryDirectory(),
            getDefaultHosts() );

        Factory factory = context.getInitialFactory();
        assertNotNull( factory );

        Repository repository = (Repository ) factory.create() ;
        assertNotNull( repository ) ;
        
        Artifact artifact = Artifact.createArtifact( 
          "avalon-framework", "avalon-framework-api", "4.1.5" );
        URL url = repository.getResource( artifact );
        assertNotNull( "url", url );
    }

    private static File getMavenRepositoryDirectory()
    {
        return new File( getMavenHomeDirectory(), "repository" );
    }

    private static File getMavenHomeDirectory()
    {
        return new File( getMavenHome() );
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
              "Internal error while attempting to access environment.";
            final String message = 
              ExceptionHelper.packException( error, e, true );
            throw new RuntimeException( message );
        }
    }

    private File getBaseDirectory()
    {
        String base = System.getProperty( "basedir" );
        if( null != base )
        {
            return new File( base );
        }
        return new File( System.getProperty( "user.dir" ) );
    }

    private static String[] getDefaultHosts()
    {
        return new String[]{ 
          "http://dpml.net/",
          "http://www.ibiblio.org/maven/"
        };
    }
}
