/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.extensions;

import java.io.File;
import org.apache.avalon.excalibur.packagemanager.ExtensionManager;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.phoenix.interfaces.ExtensionManagerMBean;

/**
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2002/07/26 09:49:21 $
 */
public class DefaultExtensionManager
    extends org.apache.avalon.excalibur.packagemanager.impl.DefaultExtensionManager
    implements LogEnabled, Parameterizable, Initializable, Disposable,
    ExtensionManager, ExtensionManagerMBean
{
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
    }

    protected void debug( final String message )
    {
        m_logger.debug( message );
    }
}
