/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.cpbuilder.metadata;

import org.apache.avalon.excalibur.extension.Extension;

/**
 * This class defines a specific classloader, made up of
 * {@link EntryDef}, {@link Extension} and
 * {@link FilesetDef} objects.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/09/01 00:43:54 $
 */
public class ClassloaderDef
{
    /**
     * The name of the current classloader.
     * This may be used by other ClassLoader definitions to refer
     * to this ClassLoader.
     */
    private final String m_name;

    /**
     * The name of the parent classloader.
     */
    private final String m_parent;

    /**
     * The Entrys that are added to this ClassLoader.
     */
    private final EntryDef[] m_entrys;

    /**
     * The Entrys that are required by this ClassLoader.
     */
    private final Extension[] m_extensions;

    /**
     * The Filesets that are added to this ClassLoader.
     */
    private final FilesetDef[] m_filesets;

    public ClassloaderDef( final String name,
                                final String parent,
                                final EntryDef[] elements,
                                final Extension[] extensions,
                                final FilesetDef[] filesets )
    {
        m_name = name;
        m_parent = parent;
        m_entrys = elements;
        m_extensions = extensions;
        m_filesets = filesets;
    }

    /**
     * Return the name of Classloader.
     *
     * @return the name of Classloader.
     * @see #m_name
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Return the name of parent Classloader.
     *
     * @return the name of parent Classloader.
     * @see #m_parent
     */
    public String getParent()
    {
        return m_parent;
    }

    /**
     * Return the elements added to Classloader.
     *
     * @return the elements added to Classloader.
     * @see #m_entrys
     */
    public EntryDef[] getEntrys()
    {
        return m_entrys;
    }

    /**
     * Return the extensions added to Classloader.
     *
     * @return the extensions added to Classloader.
     * @see #m_extensions
     */
    public Extension[] getExtensions()
    {
        return m_extensions;
    }

    /**
     * Return the filesets added to Classloader.
     *
     * @return the filesets added to Classloader.
     * @see #m_filesets
     */
    public FilesetDef[] getFilesets()
    {
        return m_filesets;
    }
}
