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
    private static final Link J2SE = 
      new Link( "http://java.sun.com/j2se/1.4/docs/api/" );

    public static class Link
    {
        private String m_href;
        private File m_dir;
        private int m_tag = ResourceRef.ANY;
        private String m_key;

        public Link()
        {
            m_href = null;
        }

        public Link( String href )
        {
            this( href, null );
        }

        public Link( String href, File dir )
        {
            m_href = href;
            m_dir = dir;
        }

        public void setHref( String href )
        {
            m_href = href;
        }

        public void setTag( String tag )
        {
            m_tag = ResourceRef.getCategory( tag );
        }

        public String getHref()
        {
            return m_href;
        }

        public void setKey( String key )
        {
            m_key = key;
        }

        public void setDir( File dir )
        {
            m_dir = dir;
        }

        public File getDir( Home home )
        {
            if( null == m_key )
            {
                return m_dir;
            }
            else
            {
                Resource resource = home.getResource( m_key );
                File cache = home.getDocsRepository().getCacheDirectory();
                File group = new File( cache, resource.getInfo().getGroup() );
                File docs = new File( group, resource.getInfo().getName() );
                String category = ResourceRef.getCategoryName( m_tag );
                String version = resource.getInfo().getVersion();
                if(( null == version ) || "".equals( version ))
                {
                    return new File( docs, category );
                }
                else
                {
                    File vDir = new File( docs, version );
                    return new File( vDir, category );
                }
            }
        }

        public boolean matches( int category )
        {
            if( ResourceRef.ANY == category ) return true;
            if( ResourceRef.ANY == m_tag ) return true;
            return ( m_tag == category );
        }

        public String toString()
        {
            if( null == m_dir )
            {
                if( null == m_key )
                {
                    return "link: " + m_href;
                }
                else
                {
                    return "link: " + m_href + " from [" + m_key + "]";
                }
            }
            else
            {
                return "link: " + m_href + " at " + m_dir;
            }
        }
    }

    public static final String JAVADOC = "javadoc";
    public static final String API = "api";
    public static final String SPI = "spi";
    public static final String IMPL = "impl";

    private String m_root;
    private String m_id;
    private String m_title;
    private List m_links = new ArrayList();

    public void setRoot( String root )
    {
        m_root = root;
    }

    public void setId( String id )
    {
        m_id = id;
    }

    public void setTitle( String title )
    {
        m_title = title;
    }

    public Link createLink()
    {
        Link link = new Link();
        m_links.add( link );
        return link;
    }

    public void execute() throws BuildException
    {
        Definition def = getReferenceDefinition();
        File root = getJavadocRootDirectory( def );
        Path classpath = def.getPath( getProject(), Policy.RUNTIME );

        File api = new File( root, "api" );
        File spi = new File( root, "spi" );
        File imp = new File( root, "impl" );

        setup( def, classpath, ResourceRef.API, api, false );
        setup( def, classpath, ResourceRef.SPI, spi, false );
        setup( def, classpath, ResourceRef.IMPL, imp,  true );
    }

    private void setup( 
      Definition def, Path classpath, int category, File root, boolean flag )
    {
        ResourceRef[] refs = 
          def.getResourceRefs( Policy.RUNTIME, category, true );
        if( refs.length > 0 )
        {
            String message = ResourceRef.getCategoryName( category );
            log( "Javadoc " + message + " generation." );
            generate( def, classpath, refs, category, root, flag );
        }
    }

    private void generate( 
       Definition definition, Path classpath, ResourceRef[] refs, 
       int category, File root, boolean flag )
    {
        Javadoc javadoc = (Javadoc) getProject().createTask( "javadoc" );

        javadoc.init();
        javadoc.setDestdir( root );
        Path source = javadoc.createSourcepath();
        javadoc.createClasspath().add( classpath );
        javadoc.setDoctitle( getTitle( definition, category ) );

        for( int i=0; i<m_links.size(); i++ )
        {
            Link link = (Link) m_links.get( i );
            if( link.matches( category ) )
            {
                Javadoc.LinkArgument arg = javadoc.createLink();
                arg.setHref( link.getHref() );
                File dir = link.getDir( getHome() );
                if( null != dir )
                {
                    if( dir.exists() )
                    {
                        log( link.toString() );
                        arg.setOffline( true );
                        arg.setPackagelistLoc( dir );
                    }
                    else
                    {
                        final String warning = 
                          link + ": warning - unresolved directory";
                        log( warning, Project.MSG_WARN );
                    }
                }
                else
                {
                    log( link.toString() );
                }
            }
        }

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
                    log( "Ignoring src path: " + src, Project.MSG_WARN );
                }
            }
        }

        if( flag )
        {
            File basedir = definition.getBasedir();
            File local = new File( basedir, "target/build/main" );
            if( local.exists() )
            {
                source.createPathElement().setLocation( local );
                DirSet packages = new DirSet();
                packages.setDir( local );
                packages.setIncludes( "**/**" );
                javadoc.addPackageset( packages );
            }
        }

        javadoc.execute();
    }

    private File getJavadocRootDirectory( Definition def )
    {
        File docs = getProductRoot( def );
        
        String version = def.getInfo().getVersion();
        if( null == version )
        {
            return docs;
        }
        else
        {
            return new File( docs, version );
        }
    }

    private File getProductRoot( Definition def )
    {
        File docs = getContext().getDocsDirectory();
        if( m_root != null )
        {
            return Context.getFile( docs, m_root );
        }
        else
        {
            return docs;
        }
    }

    private String getTitle( Definition def, int category )
    {
        String extra = getTitleSuppliment( def, category );
        if( null == m_title )
        {
            return def.getInfo().getName() + extra;
        }
        return m_title + extra;
    }

    private String getTitleSuppliment( Definition def, int cat )
    {
        String category = ResourceRef.getCategoryName( cat ).toUpperCase();
        String version = def.getInfo().getVersion();
        if( null == version )
        {
            return " : " + category;
        }
        else
        {
            return ", Version " + version + " : " + category;
        }
    }

    private Definition getReferenceDefinition()
    {
        if( null != m_id )
        {
            return getHome().getDefinition( m_id );
        }
        else
        {
            return getHome().getDefinition( getKey() );
        }
    }
}
