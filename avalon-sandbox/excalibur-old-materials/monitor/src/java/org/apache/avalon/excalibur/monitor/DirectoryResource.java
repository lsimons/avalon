/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
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
 * @version $Revision: 1.1 $ $Date: 2002/09/08 02:30:48 $
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

        final int lastCount = m_files.size();
        final int addedCount = addedFiles.size();
        final int modifiedCount = modifiedFiles.size();

        //If no new files have been added and
        //none deleted then nothing to do
        if( files.length == lastCount &&
            0 == addedCount &&
            0 == modifiedCount )
        {
            return;
        }

        final HashSet deletedFiles = new HashSet();

        //If only new files were added and none were changed then
        //we don't have to scan for deleted files
        if( files.length != lastCount + addedCount )
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

        m_files = existingFiles;
    }

    public long lastModified()
    {
        return getPreviousModified();
    }
}
