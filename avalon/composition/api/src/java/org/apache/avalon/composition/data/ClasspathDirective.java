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

package org.apache.avalon.composition.data;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Description of classpath.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 09:31:03 $
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
