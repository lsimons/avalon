/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

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
package org.apache.avalon.phoenix.components.extensions;

import java.io.File;
import java.util.ArrayList;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.phoenix.components.extensions.pkgmgr.ExtensionManager;
import org.apache.avalon.phoenix.components.extensions.pkgmgr.OptionalPackage;
import org.apache.avalon.phoenix.interfaces.ExtensionManagerMBean;

/**
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.11 $ $Date: 2003/04/30 10:16:49 $
 */
public class DefaultExtensionManager
    extends org.apache.avalon.phoenix.components.extensions.pkgmgr.impl.DefaultExtensionManager
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
