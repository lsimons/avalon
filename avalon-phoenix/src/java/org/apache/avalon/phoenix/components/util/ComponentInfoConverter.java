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

package org.apache.avalon.phoenix.components.util;

import java.util.ArrayList;
import org.apache.avalon.framework.Version;
import org.apache.avalon.phoenix.framework.info.ComponentDescriptor;
import org.apache.avalon.phoenix.framework.info.ComponentInfo;
import org.apache.avalon.phoenix.framework.info.SchemaDescriptor;
import org.apache.avalon.phoenix.framework.tools.infobuilder.LegacyUtil;
import org.apache.avalon.phoenix.metainfo.BlockDescriptor;
import org.apache.avalon.phoenix.metainfo.BlockInfo;
import org.apache.avalon.phoenix.metainfo.DependencyDescriptor;
import org.apache.avalon.phoenix.metainfo.ServiceDescriptor;

/**
 * Convert a {@link ComponentInfo} into a {@link BlockInfo}.
 *
 * @author Peter Donald
 * @version $Revision: 1.13 $ $Date: 2003/12/05 15:14:36 $
 */
public class ComponentInfoConverter
{
    private ComponentInfoConverter()
    {
    }

    /**
     * Convert a ComponentInfo object into a BlockInfo object.
     *
     * @param component the ComponentInfo object
     * @return the BlockInfo object
     */
    public static BlockInfo toBlockInfo( final ComponentInfo component )
    {
        final BlockDescriptor descriptor = toBlockDescriptor( component );
        final ServiceDescriptor[] services = toPhoenixServices( component.getServices() );
        final ServiceDescriptor[] mxServices = getMXServices( component.getServices() );
        final DependencyDescriptor[] dependencys =
            toPhoenixDependencys( component.getDependencies() );

        return new BlockInfo( descriptor,
                              services,
                              mxServices,
                              dependencys );
    }

    /**
     * Return Phoenix Management services from Info Service array.
     *
     * @param services the services
     * @return the management services
     */
    private static ServiceDescriptor[] getMXServices(
        final org.apache.avalon.phoenix.framework.info.ServiceDescriptor[] services )
    {
        final ArrayList serviceSet = new ArrayList();
        for( int i = 0; i < services.length; i++ )
        {
            if( LegacyUtil.isMxService( services[ i ] ) )
            {
                serviceSet.add( toPhoenixService( services[ i ] ) );
            }
        }
        return (ServiceDescriptor[])serviceSet.toArray( new ServiceDescriptor[ serviceSet.size() ] );
    }

    /**
     * Return Phoenix services from Info Service array.
     *
     * @param services the services
     * @return the Phoenix services
     */
    private static ServiceDescriptor[] toPhoenixServices(
        final org.apache.avalon.phoenix.framework.info.ServiceDescriptor[] services )
    {
        final ArrayList serviceSet = new ArrayList();
        for( int i = 0; i < services.length; i++ )
        {
            if( !LegacyUtil.isMxService( services[ i ] ) )
            {
                serviceSet.add( toPhoenixService( services[ i ] ) );
            }
        }
        return (ServiceDescriptor[])serviceSet.toArray( new ServiceDescriptor[ serviceSet.size() ] );
    }

    /**
     * Convert Info service to Phoenix Service descriptor.
     *
     * @param service the Info Service
     * @return the Phoenix service
     */
    private static ServiceDescriptor toPhoenixService(
        final org.apache.avalon.phoenix.framework.info.ServiceDescriptor service )
    {
        final Version version = LegacyUtil.toVersion( service );
        return new ServiceDescriptor( service.getType(), version );
    }

    /**
     * Convert Info dependencys to Phoenix dependencys.
     *
     * @param dependencies the Info dependencys
     * @return the Phoenix dependencys
     */
    private static DependencyDescriptor[] toPhoenixDependencys(
        final org.apache.avalon.phoenix.framework.info.DependencyDescriptor[] dependencies )
    {
        final ArrayList depends = new ArrayList();
        for( int i = 0; i < dependencies.length; i++ )
        {
            depends.add( toPhoenixDependency( dependencies[ i ] ) );
        }
        return (DependencyDescriptor[])depends.toArray( new DependencyDescriptor[ depends.size() ] );
    }

    /**
     * Convert Info dependency to Phoenix dependency descriptor.
     *
     * @param dependency the Info dependency
     * @return the Phoenix dependency
     */
    private static DependencyDescriptor toPhoenixDependency(
        final org.apache.avalon.phoenix.framework.info.DependencyDescriptor dependency )
    {
        final Version version = LegacyUtil.toVersion( dependency );
        final ServiceDescriptor service =
            new ServiceDescriptor( dependency.getType(), version );
        return new DependencyDescriptor( dependency.getKey(), service );
    }

    /**
     * Create a BlockDescriptor object from ComponentInfo.
     *
     * @param component the info
     * @return the BlockDescriptor
     */
    private static BlockDescriptor toBlockDescriptor( final ComponentInfo component )
    {
        final ComponentDescriptor descriptor = component.getDescriptor();
        final Version version = LegacyUtil.toVersion( descriptor );

        final SchemaDescriptor schema = component.getConfigurationSchema();
        String schemaType = null;
        if( null != schema )
        {
            schemaType = schema.getType();
        }

        return new BlockDescriptor( null,
                                    descriptor.getImplementationKey(),
                                    schemaType,
                                    version );
    }
}
