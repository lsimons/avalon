/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.avalon.composition.data;

import java.io.Serializable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * <p>An library directive.</p>
 * <p><b>XML</b></p>
 * <p>An library element is normally contained within a scoping structure such as a
 * classloader directive. The library element may contain any number of "include" 
 * or "group" elements.</p>
 * <pre>
 *    <font color="gray">&lt;library&gt;</font>
 *       &lt;include&gt;lib&lt;/include&gt;
 *       &lt;group&gt;avalon-framework&lt;/group&gt;
 *    <font color="gray">&lt;/library&gt;</font>
 * </pre>
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 09:31:07 $
 */
public class LibraryDirective implements Serializable
{
     private static final String[] EMPTY_SET = new String[0];

    /**
     * The include paths
     */
    private final String[] m_includes;

    /**
     * The groups
     */
    private final String[] m_groups;

    /**
     * Create a new LibraryDirective instance.
     */
    public LibraryDirective( )
    {
        this( null, null );
    }

    /**
     * Create a new LibraryDirective instance.
     *
     * @param includes the set of include paths
     * @param groups the set of group identifiers
     */
    public LibraryDirective( final String[] includes, final String[] groups )
    {
        if( includes == null )
        {
            m_includes = EMPTY_SET;
        }
        else
        {
            m_includes = includes;
        }

        if( groups == null )
        {
            m_groups = EMPTY_SET;
        }
        else
        {
            m_groups = groups;
        }
    }

   /**
    * Return the empty status of this directive.
    */
    public boolean isEmpty()
    {
        final int n = m_includes.length + m_groups.length;
        return n == 0;
    }

    /**
     * Return the set of include path entries.
     *
     * @return the include paths
     */
    public String[] getIncludes()
    {
        return m_includes;
    }

    /**
     * Return the set of group identifiers.
     *
     * @return the group identifiers
     */
    public String[] getGroups()
    {
        return m_groups;
    }

    /**
     * Return the set of optional extension locations as a File[] 
     * relative to a supplied base directory.
     *
     * @param base a base directory against which relatve references shall be resolved 
     * @return an array of extension library locations
     * @exception IOException if a path cannot be resolved to a directory
     */
    public File[] getOptionalExtensionDirectories( File base ) throws IOException
    {
        if( base == null )
        {
            throw new NullPointerException( "base" );
        }
        String[] includes = getIncludes();
        ArrayList list = new ArrayList();
        for( int i=0; i<includes.length; i++ )
        {
            final String path = includes[i];
            list.add( getDirectory( base, path ) );
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
}
