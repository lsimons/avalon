/* 
 * Copyright (C) The Apache Software Foundation. All rights reserved. 
 * 
 * This software is published under the terms of the Apache Software License 
 * version 1.1, a copy of which has been included with this distribution in 
 * the LICENSE file. 
 */
package org.apache.avalon.framework.camelot;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.excalibur.io.DirectoryFileFilter;
import org.apache.avalon.excalibur.io.ExtensionFileFilter;

/**
 * Utility methods for Camelot related facilities.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class CamelotUtil
{
    /**
     * Private constructor so impossible to instantiate.
     */
    private CamelotUtil()
    {
    }

    public static void deployFromDirectory( final Deployer deployer, 
                                            final File directory,
                                            final String extention )
        throws DeploymentException
    {
        deployFromDirectory( deployer, directory, new String[] { extention } );
    }

    public static void deployFromDirectory( final Deployer deployer, 
                                            final File directory,
                                            final String[] extentions )
        throws DeploymentException
                                          
    {
        final ExtensionFileFilter filter = new ExtensionFileFilter( extentions );
        deployFromDirectory( deployer, directory, filter );
    }

    public static void deployFromDirectory( final Deployer deployer, 
                                            final File directory,
                                            final FilenameFilter filter )
        throws DeploymentException
                                          
    {
        final File[] files = directory.listFiles( filter );

        if( null != files )
        {
            deployFiles( deployer, files );
        }
    }

    public static void deployFiles( final Deployer deployer, final File[] files )
        throws DeploymentException
    {
        for( int i = 0; i < files.length; i++ )
        {
            final String filename = files[ i ].getName();

            int index = filename.lastIndexOf( '.' );
            if( -1 == index ) index = filename.length();

            final String name = filename.substring( 0, index );

            try
            {
                final File file = files[ i ].getCanonicalFile();
                deployer.deploy( name, file.toURL() );
            }
            catch( final MalformedURLException mue )
            {
                throw new DeploymentException( "Malformed URL for " + files[ i ], mue );
            }
            catch( final IOException ioe )
            {
                throw new DeploymentException( "Unable to get canonical representation " +
                                               "for file " + files[ i ], ioe );
            }
        }
    }
}
