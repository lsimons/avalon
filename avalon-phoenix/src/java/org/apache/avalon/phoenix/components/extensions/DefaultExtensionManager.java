/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.extensions;

import java.io.File;
import java.util.ArrayList;
import org.apache.avalon.excalibur.packagemanager.ExtensionManager;
import org.apache.avalon.excalibur.packagemanager.OptionalPackage;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.phoenix.interfaces.ExtensionManagerMBean;

/**
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.6 $ $Date: 2002/11/01 22:41:37 $
 */
public class DefaultExtensionManager
    extends org.apache.avalon.excalibur.packagemanager.impl.DefaultExtensionManager
    implements LogEnabled, Parameterizable, Initializable, Disposable,
    ExtensionManager, ExtensionManagerMBean
{
    private final static Resources REZ =
        ResourceManager.getPackageResources( DefaultExtensionManager.class );

    private Logger m_logger;
    private String m_rawPath;

    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
    }

    public void parameterize( final Parameters parameters )
        throws ParameterException
    {
        final String phoenixHome = parameters.getParameter( "phoenix.home" );
        final String defaultExtPath = phoenixHome + File.separator + "ext";
        m_rawPath = parameters.getParameter( "phoenix.ext.path", defaultExtPath );
    }

    public void initialize()
        throws Exception
    {
        setPath( m_rawPath );
        rescanPath();
    }

    public void dispose()
    {
        clearCache();
    }

    public void rescanPath()
    {
        super.scanPath();

        //Display a list of packages once they have been added.
        if( m_logger.isDebugEnabled() )
        {
            final ArrayList list = new ArrayList();
            final OptionalPackage[] optionalPackages = getAllOptionalPackages();
            for( int i = 0; i < optionalPackages.length; i++ )
            {
                list.add( optionalPackages[ i ].getFile() );
            }

            final String message =
                REZ.getString( "extension.packages.notice", list );
            m_logger.debug( message );
        }
    }

    protected void debug( final String message )
    {
        m_logger.debug( message );
    }
}
