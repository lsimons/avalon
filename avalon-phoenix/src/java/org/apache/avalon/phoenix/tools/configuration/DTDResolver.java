/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.configuration;

import java.io.IOException;
import java.io.InputStream;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A Class to help to resolve Entitys for items such as DTDs or
 * Schemas.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.5 $ $Date: 2002/07/26 09:49:22 $
 */
public class DTDResolver
    implements EntityResolver
{
    /**
     * The list of DTDs that can be resolved by this class.
     */
    private final DTDInfo[] m_dtdInfos;

    /**
     * The ClassLoader to use when loading resources for DTDs.
     */
    private final ClassLoader m_classLoader;

    /**
     * Construct a resolver using specified DTDInfos where resources are loaded
     * from specified ClassLoader.
     */
    public DTDResolver( final DTDInfo[] dtdInfos, final ClassLoader classLoader )
    {
        m_dtdInfos = dtdInfos;
        m_classLoader = classLoader;
    }

    /**
     * Resolve an entity in the XML file.
     * Called by parser to resolve DTDs.
     */
    public InputSource resolveEntity( final String publicId, final String systemId )
        throws IOException, SAXException
    {
        for( int i = 0; i < m_dtdInfos.length; i++ )
        {
            final DTDInfo info = m_dtdInfos[ i ];

            if( (publicId != null && publicId.equals( info.getPublicId() ))
                || (systemId != null && systemId.equals( info.getSystemId() )) )
            {
                final ClassLoader classLoader = getClassLoader();
                final InputStream inputStream =
                    classLoader.getResourceAsStream( info.getResource() );
                return new InputSource( inputStream );
            }
        }

        return null;
    }

    /**
     * Return CLassLoader to load resource from.
     * If a ClassLoader is specified in the constructor use that,
     * else use ContextClassLoader unless that is null in which case
     * use the current classes ClassLoader.
     */
    private ClassLoader getClassLoader()
    {
        ClassLoader classLoader = m_classLoader;
        if( null == classLoader )
        {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        if( null == classLoader )
        {
            classLoader = getClass().getClassLoader();
        }
        return classLoader;
    }
}
