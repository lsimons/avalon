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

package org.apache.avalon.tools.tasks;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.taskdefs.Javadoc;

import org.apache.avalon.tools.home.Context;
import org.apache.avalon.tools.home.Home;
import org.apache.avalon.tools.project.Definition;
import org.apache.avalon.tools.project.ResourceRef;
import org.apache.avalon.tools.project.Resource;
import org.apache.avalon.tools.project.Policy;

/**
 * Build the javadoc for a project. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class JavadocTask extends SystemTask
{
    public static final String JAVADOC = "javadoc";
    public static final String API = "api";
    public static final String SPI = "spi";
    public static final String IMPL = "impl";

    private String m_root = "{docRoot}";

    public void setRoot( String root )
    {
        if( root.endsWith( "/" ) )
        { 
            m_root = root.substring( 0, root.length() - 1 );
        }
        else
        {
            m_root = root;
        }
    }

    public void execute() throws BuildException 
    {
        Definition def = getHome().getDefinition( getKey() );
        File root = getJavadocRootDirectory( def );
        Path classpath = def.getPath( getProject(), Policy.RUNTIME );

        ArrayList visited = new ArrayList();
        File api = setup( 
          def, classpath, visited, ResourceRef.API, 
          "api", root, null, null, false );
        File spi = setup( 
          def, classpath, visited, ResourceRef.SPI, 
          "spi", root, api, m_root + "/../api/", false );
        setup( 
          def, classpath, visited, ResourceRef.IMPL, 
          "impl", root, spi, m_root + "/../spi/", true );
    }

    private File setup( 
      Definition def, Path classpath, List visited, int category, String branch, 
      File root, File parent, String href, boolean flag )
    {
        File base = new File( root, branch );
        ResourceRef[] refs = def.getQualifiedRefs( visited, category );
        if( refs.length > 0 )
        {
            log( 
              "Javadoc preparation for category: " 
              + branch + ", " 
              + refs.length );
            generate( def, classpath, refs, base, parent, href, flag );
        }
        return base;
    }

    private void generate( 
       Definition definition, Path classpath, ResourceRef[] refs, 
       File root, File parent, String href, boolean flag )
    {
        Javadoc javadoc = (Javadoc) getProject().createTask( "javadoc" );

        javadoc.init();
        javadoc.setDestdir( root );
        Path source = javadoc.createSourcepath();
        javadoc.createClasspath().add( classpath );
        
        for( int i=0; i<refs.length; i++ )
        {
            ResourceRef ref = refs[i];
            Resource resource = getHome().getResource( ref );
            if( resource instanceof Definition )
            {
                Definition def = (Definition) resource;
                File base = def.getBasedir();
                File src = Context.getFile( base, "target/build/main" );
                if( src.exists() )
                {
                    log( "Adding src path: " + src );
                    source.createPathElement().setLocation( src );
                    DirSet packages = new DirSet();
                    packages.setDir( src );
                    packages.setIncludes( "**/**" );
                    javadoc.addPackageset( packages );
                }
                else
                {
                    log( "Ignoring src path: " + src );
                }
            }
        }

        if( null != href )
        {
            Javadoc.LinkArgument link = javadoc.createLink();
            link.setOffline( true );
            link.setPackagelistLoc( parent );
            link.setHref( href );
        }

        if( flag )
        {
            File basedir = definition.getBasedir();
            File local = new File( basedir, "target/build/main" );
            if( local.exists() )
            {
                source.createPathElement().setLocation( local );
            }
        }
        javadoc.execute();
    }

    private File getJavadocRootDirectory( Definition def )
    {
        File docs = getContext().getDocsDirectory();
        String version = def.getInfo().getVersion();
        if( null == version )
        {
            return new File( docs, JAVADOC );
        }
        else
        {
            return new File( docs, version );
        }
    }
}
