/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.util;

import org.apache.avalon.framework.configuration.ConfigurationException;
import java.io.File;
import java.net.MalformedURLException;

/**
 * A utility class for working with resources in default sar layout.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/09/06 09:42:35 $
 */
public class ResourceUtil
{
    private static final String SAR_PROTOCOL = "sar:";
    private static final String SAR_INF = "SAR-INF/";
    private static final String CLASSES = SAR_INF + "classes";
    private static final String LIB = SAR_INF + "lib";

    /**
     * Expand any URLs with sar: protocol so that
     * they accurately match the actual location
     *
     * @param codeBase the input url
     * @return the result url, modified to file url if it
     *         is protocol "sar:"
     * @throws ConfigurationException if invalidly specified URL
     */
    public static String expandSarURL( final String codeBase,
                                       final File baseDirectory,
                                       final File workDirectory )
        throws ConfigurationException
    {
        if( codeBase.startsWith( SAR_PROTOCOL ) )
        {
            final File file =
                getFileForResource( codeBase.substring( 4 ),
                                    baseDirectory,
                                    workDirectory );
            try
            {
                return file.toURL().toString();
            }
            catch( MalformedURLException e )
            {
                throw new ConfigurationException( e.getMessage(), e );
            }
        }
        else
        {
            return codeBase;
        }
    }

    public static File getFileForResource( final String location,
                                           final File baseDirectory,
                                           final File workDirectory )
    {
        String filename =
            location.replace( '/', File.separatorChar );
        if( filename.startsWith( "/" ) )
        {
            filename = filename.substring( 1 );
        }

        final File baseDir =
            getBaseDirectoryFor( location, baseDirectory, workDirectory );
        return new File( baseDir, filename );
    }

    private static File getBaseDirectoryFor( final String location,
                                             final File baseDirectory,
                                             final File workDirectory )
    {
        if( location.startsWith( CLASSES ) ||
            location.startsWith( LIB ) )
        {
            return workDirectory;
        }
        else
        {
            return baseDirectory;
        }
    }
}
