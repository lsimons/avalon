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
    private static class Link
    {
        final String m_href;

        public Link( String href )
        {
            m_href = href;
        }

        public String getHref()
        {
            return m_href;
        }
    }

    private static class LocalLink extends Link
    {
        final File m_dir;

        public LocalLink( String href, File dir )
        {
            super( href );
            m_dir = dir;
        }

        public File getDir()
        {
            return m_dir;
        }
    }

    public static final String JAVADOC = "javadoc";
    public static final String API = "api";
    public static final String SPI = "spi";
    public static final String IMPL = "impl";

    private String m_root = "";

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

        File api = new File( root, "api" );
        Link j2se = new Link( "http://java.sun.com/j2se/1.4/docs/api/" );
        Link[] links = new Link[]{ j2se };
        setup( def, classpath, visited, ResourceRef.API, api, links, "API", false );

        File spi = new File( root, "spi" );
        //LocalLink apiLink = new LocalLink( "/../api/", api );
        //links = new Link[]{ j2se, apiLink };
        setup( def, classpath, visited, ResourceRef.SPI, spi, links, "SPI", false );

        File imp = new File( root, "impl" );
        //LocalLink spiLink = new LocalLink( "/../spi/", spi );
        //links = new Link[]{ j2se, apiLink, spiLink };
        setup( def, classpath, visited, ResourceRef.IMPL, imp, links, "IMP", true );

    }

    private void setup( 
      Definition def, Path classpath, List visited, int category, File root, 
      Link[] links, String message, boolean flag )
    {
        ResourceRef[] refs = 
          def.getResourceRefs( Policy.RUNTIME, category, true );
        //ResourceRef[] refs = def.getQualifiedRefs( visited, category );
        if( refs.length > 0 )
        {
            log( "Javadoc " + message + " generation." );
            generate( def, classpath, refs, root, links, flag );
        }
    }

    private void generate( 
       Definition definition, Path classpath, ResourceRef[] refs, 
       File root, Link[] links, boolean flag )
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

        for( int i=0; i<links.length; i++ )
        {
            Link link = links[i];
            Javadoc.LinkArgument arg = javadoc.createLink();
            arg.setHref( link.getHref() );
            if( link instanceof LocalLink )
            {
                LocalLink local = (LocalLink) link;
                arg.setOffline( true );
                arg.setPackagelistLoc( local.getDir() );
            }
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
