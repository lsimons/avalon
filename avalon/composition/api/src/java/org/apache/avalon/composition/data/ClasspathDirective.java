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

package org.apache.avalon.composition.data;

import java.io.Serializable;

/**
 * Description of classpath.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.3 $ $Date: 2004/01/24 23:25:23 $
 */
public final class ClasspathDirective implements Serializable
{
     private static final FilesetDirective[] EMPTY_FILESETS = new FilesetDirective[0]; 
     private static final RepositoryDirective[] EMPTY_REPOSITORIES = new RepositoryDirective[0]; 

    /**
     * The fileset directives
     */
    private FilesetDirective[] m_filesets;

    /**
     * The resource references
     */
    private RepositoryDirective[] m_repositories;

    /**
     * Create a empty ClasspathDirective.
     */
    public ClasspathDirective()
    {
        this( null, null );
    }

    /**
     * Create a ClasspathDirective instance.
     *
     * @param filesets the filesets to be included in a classloader
     * @param repositories the repositories directives to be included in a classloader
     */
    public ClasspathDirective( 
       final FilesetDirective[] filesets, 
       final RepositoryDirective[] repositories )
    {
        if( filesets == null )
        {
            m_filesets = EMPTY_FILESETS;
        }
        else
        {
            m_filesets = filesets;
        }
        if( repositories == null )
        {
            m_repositories = EMPTY_REPOSITORIES;
        }
        else
        {
            m_repositories = repositories;
        }
    }

   /**
    * Return the default status of this directive.  If TRUE
    * the enclosed repository and fileset directives are empty.
    */
    public boolean isEmpty()
    {
        final int n = m_repositories.length + m_filesets.length;
        return n == 0;
    }

    /**
     * Return the set of resource directives.
     *
     * @return the resource directive set
     */
    public RepositoryDirective[] getRepositoryDirectives()
    {
        return m_repositories;
    }

    /**
     * Return the set of fileset directives.
     *
     * @return the fileset directives
     */
    public FilesetDirective[] getFilesets()
    {
        return m_filesets;
    }

   /**
    * Return an array of files corresponding to the expansion 
    * of the filesets declared within the directive.
    *
    * @param base the base directory against which relative 
    *   file references will be resolved
    * @return the classpath
    */
    /*
    public File[] expandFileSetDirectives( File base ) throws IOException
    {
        ArrayList list = new ArrayList();

        //
        // expand relative to fileset
        //

        FilesetDirective[] filesets = getFilesets();

        for( int i=0; i<filesets.length; i++ )
        {
            FilesetDirective fileset = filesets[i];
            File anchor = getDirectory( base, fileset.getBaseDirectory() );
            IncludeDirective[] includes = fileset.getIncludes();
            if( includes.length > 0 )
            {
                for( int j=0; j<includes.length; j++ )
                {
                    File file = new File( anchor, includes[j].getPath() );
                    list.add( file );
                }
            }
            else
            {
                list.add( anchor );
            }
        }

        return (File[]) list.toArray( new File[0] );
    }

    private File getDirectory( File base, String path ) throws IOException
    {
        File file = new File( path );
        if( file.isAbsolute() )
        {
            return verifyDirectory( file );
        }
        return verifyDirectory( new File( base, path ) );
    }

    private File verifyDirectory( File dir ) throws IOException
    {
        if( dir.isDirectory() )
        {
            return dir.getCanonicalFile();
        }

        final String error = 
          "Path does not correspond to a directory: " + dir;
        throw new IOException( error );
    }
    */
}
