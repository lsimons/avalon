/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
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
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
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
package org.apache.avalon.excalibur.monitor;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This is a Resource that monitors a directory. If any files
 * are added, removed or modified in directory then it will
 * send an event indicating the change.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003/11/09 14:36:57 $
 */
public class DirectoryResource
    extends Resource
{
    public static final String ADDED = "AddedFiles";
    public static final String REMOVED = "DeletedFiles";
    public static final String MODIFIED = "ModifiedFiles";

    private final File m_dir;
    private Set m_files;
    private Map m_times;

    public DirectoryResource( final String resourceKey )
        throws Exception
    {
        super( resourceKey );
        m_dir = new File( resourceKey );
        if( !m_dir.isDirectory() )
        {
            final String message = m_dir + " is not a directory.";
            throw new IllegalArgumentException( message );
        }

        m_files = new HashSet();
        m_times = new HashMap();

        final File[] files = m_dir.listFiles();
        for( int i = 0; i < files.length; i++ )
        {
            final File file = files[ i ];
            m_files.add( file );
            m_times.put( file, new Long( file.lastModified() ) );
        }
        setPreviousModified( System.currentTimeMillis() );
    }

    /**
     * Test whether this has been modified since time X
     */
    public void testModifiedAfter( final long time )
    {
        if( getPreviousModified() > time )
        {
            return;
        }

        final HashSet existingFiles = new HashSet();
        final HashSet modifiedFiles = new HashSet();
        final HashSet addedFiles = new HashSet();

        final File[] files = m_dir.listFiles();

        int fileCount = 0;
        if( null != files )
        {
            fileCount = files.length;
            for( int i = 0; i < files.length; i++ )
            {
                final File file = files[ i ];
                final long newModTime = file.lastModified();
                if( m_files.contains( file ) )
                {
                    existingFiles.add( file );

                    final Long oldModTime = (Long)m_times.get( file );
                    if( oldModTime.longValue() != newModTime )
                    {
                        modifiedFiles.add( file );
                    }
                }
                else
                {
                    addedFiles.add( file );
                }
                m_times.put( file, new Long( newModTime ) );
            }
        }

        final int lastCount = m_files.size();
        final int addedCount = addedFiles.size();
        final int modifiedCount = modifiedFiles.size();

        //If no new files have been added and
        //none deleted then nothing to do
        if( fileCount == lastCount &&
            0 == addedCount &&
            0 == modifiedCount )
        {
            return;
        }

        final HashSet deletedFiles = new HashSet();

        //If only new files were added and none were changed then
        //we don't have to scan for deleted files
        if( fileCount != lastCount + addedCount )
        {
            //Looks like we do have to scan for deleted files
            final Iterator iterator = m_files.iterator();
            while( iterator.hasNext() )
            {
                final File file = (File)iterator.next();
                if( !existingFiles.contains( file ) )
                {
                    deletedFiles.add( file );
                    m_times.remove( file );
                }
            }
        }

        final int deletedCount = deletedFiles.size();
        if( 0 != deletedCount )
        {
            getEventSupport().firePropertyChange( REMOVED,
                                                  Collections.EMPTY_SET,
                                                  deletedFiles );
        }
        if( 0 != addedCount )
        {
            getEventSupport().firePropertyChange( ADDED,
                                                  Collections.EMPTY_SET,
                                                  addedFiles );
        }

        if( 0 != modifiedCount )
        {
            getEventSupport().firePropertyChange( MODIFIED,
                                                  Collections.EMPTY_SET,
                                                  modifiedFiles );
        }

        existingFiles.addAll( addedFiles );
        m_files = existingFiles;
    }

    public long lastModified()
    {
        return getPreviousModified();
    }
}
