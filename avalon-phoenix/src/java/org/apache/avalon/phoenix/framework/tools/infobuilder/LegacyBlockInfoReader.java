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

package org.apache.avalon.phoenix.framework.tools.infobuilder;

import java.io.InputStream;
import java.util.ArrayList;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.phoenix.framework.info.Attribute;
import org.apache.avalon.phoenix.framework.info.ComponentDescriptor;
import org.apache.avalon.phoenix.framework.info.ComponentInfo;
import org.apache.avalon.phoenix.framework.info.DependencyDescriptor;
import org.apache.avalon.phoenix.framework.info.LoggerDescriptor;
import org.apache.avalon.phoenix.framework.info.SchemaDescriptor;
import org.apache.avalon.phoenix.framework.info.ServiceDescriptor;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.xml.sax.InputSource;

/**
 * A LegacyBlockInfoReader is responsible for building {@link ComponentInfo}
 * objects from <a href="http://jakarta.apache.org/avalon/phoenix">Phoenixs</a>
 * BlockInfo descriptors. The format for descriptor is specified in the
 * <a href="package-summary.html#external">package summary</a>.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2003/04/04 10:56:25 $
 */
public final class LegacyBlockInfoReader
    extends AbstractLogEnabled
    implements InfoReader
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( LegacyBlockInfoReader.class );

    /**
     * Create a {@link ComponentInfo} object for specified
     * classname, loaded from specified {@link InputStream}.
     *
     * @param implementationKey The classname of Component
     * @param inputStream the InputStream to load ComponentInfo from
     * @return the created ComponentInfo
     * @throws ConfigurationException if an error occurs
     */
    public ComponentInfo createComponentInfo( final String implementationKey,
                                              final InputStream inputStream )
        throws Exception
    {
        final InputSource input = new InputSource( inputStream );
        final Configuration configuration = ConfigurationBuilder.build( input );
        return build( implementationKey, configuration );
    }

    /**
     * Create a {@link ComponentInfo} object for specified classname from
     * specified configuration data.
     *
     * @param classname The classname of Component
     * @param info the ComponentInfo configuration
     * @return the created ComponentInfo
     * @throws ConfigurationException if an error occurs
     */
    private ComponentInfo build( final String classname,
                                 final Configuration info )
        throws Exception
    {
        if( getLogger().isDebugEnabled() )
        {
            final String message =
                REZ.getString( "builder.creating-info.notice",
                               classname );
            getLogger().debug( message );
        }

        final String topLevelName = info.getName();
        if( !topLevelName.equals( "blockinfo" ) )
        {
            final String message =
                REZ.getString( "legacy.bad-toplevel-element.error",
                               classname,
                               topLevelName );
            throw new ConfigurationException( message );
        }

        Configuration configuration = null;

        configuration = info.getChild( "block" );
        final ComponentDescriptor descriptor =
            buildComponentDescriptor( classname, configuration );
        final String implementationKey = descriptor.getImplementationKey();

        final ServiceDescriptor[] services = buildServices( info );

        configuration = info.getChild( "dependencies" );
        final DependencyDescriptor[] dependencies =
            buildDependencies( implementationKey, configuration );

        if( getLogger().isDebugEnabled() )
        {
            final String message =
                REZ.getString( "legacy.created-info.notice",
                               classname,
                               new Integer( services.length ),
                               new Integer( dependencies.length ) );
            getLogger().debug( message );
        }

        configuration = info.getChild( "block" );
        final SchemaDescriptor schema = buildConfigurationSchema( configuration );

        return new ComponentInfo( descriptor,
                                  services,
                                  LoggerDescriptor.EMPTY_SET,
                                  LegacyUtil.CONTEXT_DESCRIPTOR,
                                  dependencies, schema, null );
    }

    /**
     * A utility method to build a descriptor for SchemaDescriptor,
     *
     * @return the a descriptor for the SchemaDescriptor,
     */
    private SchemaDescriptor buildConfigurationSchema( Configuration configuration )
    {
        final String schemaType =
            configuration.getChild( "schema-type" ).getValue( "" );
        if( "".equals( schemaType ) )
        {
            return null;
        }
        else
        {
            final String schemaUri =
                LegacyUtil.translateToSchemaUri( schemaType );
            return new SchemaDescriptor( "", schemaUri, Attribute.EMPTY_SET );
        }

    }

    /**
     * A utility method to build an array of {@link DependencyDescriptor}
     * objects from specified configuration and classname.
     *
     * @param classname The classname of Component (used for logging purposes)
     * @param configuration the dependencies configuration
     * @return the created DependencyDescriptor
     * @throws ConfigurationException if an error occurs
     */
    private DependencyDescriptor[] buildDependencies( final String classname,
                                                      final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration[] elements = configuration.getChildren( "dependency" );
        final ArrayList dependencies = new ArrayList();

        for( int i = 0; i < elements.length; i++ )
        {
            final DependencyDescriptor dependency =
                buildDependency( classname, elements[ i ] );
            dependencies.add( dependency );
        }

        return (DependencyDescriptor[])dependencies.toArray( DependencyDescriptor.EMPTY_SET );
    }

    /**
     * A utility method to build a {@link DependencyDescriptor}
     * object from specified configuraiton.
     *
     * @param classname The classname of Component (used for logging purposes)
     * @param dependency the dependency configuration
     * @return the created DependencyDescriptor
     * @throws ConfigurationException if an error occurs
     */
    private DependencyDescriptor buildDependency( final String classname,
                                                  final Configuration dependency )
        throws ConfigurationException
    {
        final String implementationKey =
            dependency.getChild( "service" ).getAttribute( "name" );
        String key = dependency.getChild( "role" ).getValue( null );

        //default to name of service if key unspecified
        if( null == key )
        {
            key = implementationKey;
        }
        else
        {
            //If key is specified and it is the same as
            //service name then warn that it is redundent.
            if( key.equals( implementationKey ) )
            {
                final String message =
                    REZ.getString( "builder.redundent-key.notice",
                                   classname,
                                   key );
                getLogger().warn( message );
            }
        }

        return new DependencyDescriptor( key,
                                         implementationKey,
                                         false,
                                         Attribute.EMPTY_SET );
    }

    /**
     * A utility method to build an array of {@link ServiceDescriptor}
     * objects from specified configuraiton.
     *
     * @param info the services configuration
     * @return the created ServiceDescriptor
     * @throws ConfigurationException if an error occurs
     */
    private ServiceDescriptor[] buildServices( final Configuration info )
        throws ConfigurationException
    {
        final ArrayList services = new ArrayList();

        Configuration[] elements = info.getChild( "services" ).getChildren( "service" );
        for( int i = 0; i < elements.length; i++ )
        {
            final ServiceDescriptor service = buildService( elements[ i ], false );
            services.add( service );
        }
        elements = info.getChild( "management-access-points" ).getChildren( "service" );
        for( int i = 0; i < elements.length; i++ )
        {
            final ServiceDescriptor service = buildService( elements[ i ], true );
            services.add( service );
        }

        return (ServiceDescriptor[])services.toArray( ServiceDescriptor.EMPTY_SET );
    }

    /**
     * A utility method to build a {@link ServiceDescriptor}
     * object from specified configuraiton data.
     *
     * @param service the service Configuration
     * @return the created ServiceDescriptor
     * @throws ConfigurationException if an error occurs
     */
    private ServiceDescriptor buildService( final Configuration service,
                                            final boolean isManagement )
        throws ConfigurationException
    {
        final String implementationKey = service.getAttribute( "name" );
        final String version = service.getAttribute( "version", null );

        final ArrayList attributeSet = new ArrayList();
        if( null != version )
        {
            attributeSet.add( LegacyUtil.createVersionAttribute( version ) );
        }

        if( isManagement )
        {
            attributeSet.add( LegacyUtil.MX_ATTRIBUTE );
        }

        final Attribute[] attributes = (Attribute[])attributeSet.toArray( new Attribute[ attributeSet.size() ] );
        return new ServiceDescriptor( implementationKey, attributes );
    }

    /**
     * A utility method to build a {@link ComponentDescriptor}
     * object from specified configuraiton data and classname.
     *
     * @param config the Component Configuration
     * @return the created ComponentDescriptor
     */
    private ComponentDescriptor buildComponentDescriptor( final String classname,
                                                          final Configuration config )
    {
        final String version = config.getChild( "version" ).getValue( "1.0" );
        final ArrayList attributeSet = new ArrayList();
        attributeSet.add( LegacyUtil.createVersionAttribute( version ) );

        final Attribute[] attributes = (Attribute[])attributeSet.toArray( new Attribute[ attributeSet.size() ] );
        return new ComponentDescriptor( classname, attributes );
    }
}
