/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.extensions;

import java.io.File;
import java.io.IOException;
import org.apache.avalon.excalibur.extension.DefaultPackageRepository;
import org.apache.avalon.excalibur.util.StringUtil;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.phoenix.interfaces.PackageRepository;
import org.apache.avalon.phoenix.interfaces.ExtensionManagerMBean;

/**
 * PhoenixPackageRepository
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2001/12/11 10:13:34 $
 */
public class PhoenixPackageRepository
    extends DefaultPackageRepository
    implements LogEnabled, Parameterizable, Initializable, Disposable, 
               PackageRepository, ExtensionManagerMBean
{
    private Logger m_logger;

    /**
     * An array of path elements. Each element designates a directory
     * in which the ExtensionManager should scan for Extensions.
     */
    private String[] m_path;

    public PhoenixPackageRepository()
    {
        super( new File[ 0 ] );
    }

    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
    }

    public void parameterize( final Parameters parameters )
        throws ParameterException
    {
        final String phoenixHome = parameters.getParameter( "phoenix.home" );
        final String defaultExtPath = phoenixHome + File.separator + "ext";
        final String rawPath = 
            parameters.getParameter( "phoenix.ext.path", defaultExtPath );
        m_path = StringUtil.split( rawPath, "|" );

        final File[] dirs = new File[ m_path.length ];
        for( int i = 0; i < dirs.length; i++ )
        {
            try
            {
                dirs[ i ] = (new File( m_path[ i ] )).getCanonicalFile();
            }
            catch( final IOException ioe )
            {
                throw new ParameterException( "Malformed entry in path '" + m_path[ i ] + 
                                              ". Unable to determine file for entry", ioe );
            }
        }

        for( int i = 0; i < dirs.length; i++ )
        {
            m_path[ i ] = dirs[ i ].toString();
        }       
    }

    public void initialize()
        throws Exception
    {
        final File[] dirs = new File[ m_path.length ];
        for( int i = 0; i < dirs.length; i++ )
        {
            dirs[ i ] = new File( m_path[ i ] );
        }

        setPath( dirs );

        scanPath();
    }

    /**
     * Retrieve an array of paths where each
     * element in array represents a directory
     * in which the ExtensionManager will look 
     * for Extensions.
     *
     * @return the list of paths to search in
     */
    public String[] getPaths()
    {
        return m_path;
    }

    /**
     * Force the ExtensionManager to rescan the paths
     * to discover new Extensions that have been added
     * or remove old Extensions that have been removed.
     *
     */
    public void rescanPath()
    {
        clearCache();
        scanPath();
    }

    public void dispose()
    {
        clearCache();
    }

    protected void debug( final String message )
    {
        m_logger.debug( message );
    }
}
