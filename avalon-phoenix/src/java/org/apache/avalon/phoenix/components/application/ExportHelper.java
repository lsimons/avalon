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

package org.apache.avalon.phoenix.components.application;

import java.util.ArrayList;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.CascadingException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.containerkit.profile.ComponentProfile;
import org.apache.avalon.phoenix.framework.info.ServiceDescriptor;
import org.apache.avalon.phoenix.framework.tools.infobuilder.LegacyUtil;
import org.apache.avalon.phoenix.interfaces.ApplicationContext;

/**
 * Utility class to help with exporting Blocks to management subsystem.
 *
 * @author Peter Donald
 * @version $Revision: 1.11 $ $Date: 2003/12/05 15:14:35 $
 */
class ExportHelper
    extends AbstractLogEnabled
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( ExportHelper.class );

    /**
     * Export the services of block, declared to be management
     * services, into management system.
     */
    void exportBlock( final ApplicationContext context,
                      final ComponentProfile profile,
                      final Object block )
        throws CascadingException
    {
        final ServiceDescriptor[] services = getMxServices( profile );
        final String name = profile.getMetaData().getName();
        final ClassLoader classLoader = block.getClass().getClassLoader();

        final Class[] serviceClasses = new Class[ services.length ];

        for( int i = 0; i < services.length; i++ )
        {
            final ServiceDescriptor service = services[ i ];
            try
            {
                serviceClasses[ i ] = classLoader.loadClass( service.getType() );
            }
            catch( final Exception e )
            {
                final String reason = e.toString();
                final String message =
                    REZ.getString( "bad-mx-service.error", name, service.getType(), reason );
                getLogger().error( message );
                throw new CascadingException( message, e );
            }
        }

        try
        {
            context.exportObject( name, serviceClasses, block );
        }
        catch( final Exception e )
        {
            final String message =
                REZ.getString( "export.error", name, e );
            getLogger().error( message );
            throw new CascadingException( message, e );
        }

    }

    /**
     * Return an array of all Management services for profile.
     *
     * @param profile the component profile
     * @return the management services.
     */
    private ServiceDescriptor[] getMxServices( final ComponentProfile profile )
    {
        final ArrayList mxServices = new ArrayList();
        final ServiceDescriptor[] services = profile.getInfo().getServices();
        for( int i = 0; i < services.length; i++ )
        {
            final ServiceDescriptor service = services[ i ];
            if( LegacyUtil.isMxService( service ) )
            {
                mxServices.add( service );
            }
        }

        return (ServiceDescriptor[])mxServices.toArray( new ServiceDescriptor[ mxServices.size() ] );
    }

    /**
     * Unxport the services of block, declared to be management
     * services, into management system.
     */
    void unexportBlock( final ApplicationContext context,
                        final ComponentProfile profile )
    {
        final String name = profile.getMetaData().getName();
        try
        {
            context.unexportObject( name );
        }
        catch( final Exception e )
        {
            final String message =
                REZ.getString( "unexport.error", name, e );
            getLogger().error( message );
        }
    }
}
