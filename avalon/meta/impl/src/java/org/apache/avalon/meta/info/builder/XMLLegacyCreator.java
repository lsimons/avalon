/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

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

package org.apache.avalon.meta.info.builder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.Version;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.meta.ConfigurationBuilder;
import org.apache.avalon.meta.info.InfoDescriptor;
import org.apache.avalon.meta.info.ContextDescriptor;
import org.apache.avalon.meta.info.EntryDescriptor;
import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.ExtensionDescriptor;
import org.apache.avalon.meta.info.CategoryDescriptor;
import org.apache.avalon.meta.info.ReferenceDescriptor;
import org.apache.avalon.meta.info.ServiceDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;
import org.apache.avalon.meta.info.Type;
import org.apache.excalibur.configuration.ConfigurationUtil;
import org.xml.sax.InputSource;

/**
 * Handles internalization of a legacy Phoenix XML based description of a {@link Type}
 * from a Configuration object.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2003/10/19 14:05:54 $
 */
public class XMLLegacyCreator
    extends XMLTypeCreator
    implements TypeCreator
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( XMLLegacyCreator.class );

    /**
     * Create a {@link Type} object for specified
     * classname, loaded from specified {@link InputStream}.
     *
     * @param implementationKey The classname of Component
     * @param inputStream the InputStream to load Type from
     * @return the created Type
     * @throws Exception if an error occurs
     */
    public Type createType( String implementationKey,
                            InputStream inputStream )
        throws Exception
    {
        if( inputStream == null )
        {
            throw new NullPointerException( "input" );
        }
        final InputSource input = new InputSource( inputStream );
        final String classname = implementationKey;
        final Configuration xinfo = ConfigurationBuilder.build( input );
        return build( classname, xinfo );
    }

    /**
     * Create a {@link Type} object for specified
     * classname and configuration.
     *
     * @param classname The classname of the component
     * @param config the meta info configuration fragment
     * @return the created Type
     * @throws ConfigurationException if an error occurs
     */
    public Type createType( String classname, Configuration config )
        throws BuildException
    {
        return build( classname, config );
    }

    /**
     * Create a {@link Type} object for specified classname from
     * specified configuration data.
     *
     * @param classname The classname of Component
     * @param info the Type configuration
     * @return the created Type
     * @throws BuildException if an error occurs
     */
    private Type build( final String classname, final Configuration info )
        throws BuildException
    {
        final String topLevelName = info.getName();
        if( !topLevelName.equals( "blockinfo" ) )
        {
            final String message =
              REZ.getString( "builder.bad-toplevel-block-element.error",
                   classname,
                   topLevelName );
            throw new BuildException( message );
        }

        Configuration configuration = null;

        configuration = info.getChild( "block" );

        final InfoDescriptor descriptor =
          buildInfoDescriptor( classname, configuration );

        configuration = info.getChild( "loggers" );
        final CategoryDescriptor[] loggers = new CategoryDescriptor[0];
        final ContextDescriptor context = 
          buildPhoenixContext();

        configuration = info.getChild( "services" );
        final ServiceDescriptor[] services = 
          buildBlockServices( configuration );

        configuration = info.getChild( "dependencies" );
        final DependencyDescriptor[] dependencies = 
          buildBlockDependencies( configuration );

        configuration = info.getChild( "stages" );
        final StageDescriptor[] phases = 
          buildStages( configuration );

        configuration = info.getChild( "extensions" );
        final ExtensionDescriptor[] extensions = 
          buildExtensions( configuration );

        return new Type(
            descriptor, loggers, context, services, dependencies, 
            phases, extensions );
    }

    /**
     * A utility method to build a {@link InfoDescriptor}
     * object from specified configuration data and classname.
     *
     * @param classname The classname of Component (used to create descriptor)
     * @param info the component info configuration fragment
     * @return the created InfoDescriptor
     * @throws ConfigurationException if an error occurs
     */
    public InfoDescriptor buildInfoDescriptor(
      final String classname, final Configuration info )
      throws BuildException
    {
        final String name = 
          info.getChild( "name" ).getValue( null );
        final String schema = 
          info.getChild( "schema-type" ).getValue( null );
        final Version version = 
          buildVersion( info.getChild( "version" ).getValue( "1.0" ) );
        final Properties attributes =
            buildAttributes( info.getChild( "attributes" ) );
        final String lifestyle = InfoDescriptor.SINGLETON;
        return new InfoDescriptor( 
          name, classname, version, lifestyle, null, schema, attributes );
    }

    private ContextDescriptor buildPhoenixContext()
    {
        EntryDescriptor name = 
          new EntryDescriptor( 
            ContextDescriptor.NAME_KEY, 
            "java.lang.String", false, false, "block.name" );
        EntryDescriptor partition = 
          new EntryDescriptor( 
            ContextDescriptor.PARTITION_KEY, 
            "java.lang.String", false, false, "app.name" );
        EntryDescriptor home = 
          new EntryDescriptor( 
            ContextDescriptor.HOME_KEY, 
            "java.io.File", false, false, "app.home" );

        return new ContextDescriptor( new EntryDescriptor[]{ name, partition, home } );
    }

    /**
     * A utility method to build an array of {@link ServiceDescriptor}
     * objects from specified configuration.
     *
     * @param depSet the set of dependency configurations
     * @return the created dependency descriptor
     * @throws ConfigurationException if an error occurs
     */
    protected DependencyDescriptor[] buildBlockDependencies( final Configuration depSet )
        throws BuildException
    {
        final Configuration[] deps = depSet.getChildren( "dependency" );
        final ArrayList dependencies = new ArrayList();
        for( int i = 0; i < deps.length; i++ )
        {
            final DependencyDescriptor dependency = 
              buildBlockDependency( deps[ i ] );
            dependencies.add( dependency );
        }
        return (DependencyDescriptor[])dependencies.toArray( 
          new DependencyDescriptor[ 0 ] );
    }


    /**
     * A utility method to build an array of {@link ServiceDescriptor}
     * objects from specified configuraiton.
     *
     * @param servicesSet the services configuration
     * @return the created ServiceDescriptor
     * @throws BuildException if an error occurs
     */
    protected ServiceDescriptor[] buildBlockServices( final Configuration servicesSet )
        throws BuildException
    {
        final Configuration[] elements = servicesSet.getChildren( "service" );
        final ArrayList services = new ArrayList();

        for( int i = 0; i < elements.length; i++ )
        {
            final ServiceDescriptor service = buildBlockService( elements[ i ] );
            services.add( service );
        }

        return (ServiceDescriptor[])services.toArray( new ServiceDescriptor[ 0 ] );
    }

    /**
     * A utility method to build a <code>ServiceDescriptor</code>
     * object from specified configuraiton data.
     *
     * @param service the service Configuration
     * @return the created ServiceDescriptor
     * @throws BuildException if an error occurs
     */
    protected ServiceDescriptor buildBlockService( final Configuration service )
        throws BuildException
    {
        final ReferenceDescriptor designator = buildReferenceDescriptor( service );
        final Properties attributes =
            buildAttributes( service.getChild( "attributes" ) );
        return new ServiceDescriptor( designator, attributes );
    }

    /**
     * A utility method to build a {@link ReferenceDescriptor}
     * object from specified configuraiton data.
     *
     * @param service the service Configuration
     * @return the created ReferenceDescriptor
     * @throws ConfigurationException if an error occurs
     */
    protected ReferenceDescriptor buildReferenceDescriptor( final Configuration service )
        throws BuildException
    {
        try
        {
            final String type = service.getAttribute( "name" );
            final String versionString = service.getAttribute( "version", "1.0" );
            final Version version = buildVersion( versionString );
            return new ReferenceDescriptor( type, version );
        }
        catch( Throwable e )
        {
            final String error =
              "Error occured while attempting to build reference descriptor from legacy blockinfo configuration: "
              + ConfigurationUtil.list( service );
            throw new BuildException( error, e );
        }
    }

    /**
     * A utility method to build a {@link DependencyDescriptor}
     * object from specified configuraiton.
     *
     * @param dependency the dependency configuration
     * @return the created DependencyDescriptor
     * @throws ConfigurationException if an error occurs
     */
    protected DependencyDescriptor buildBlockDependency( final Configuration dependency )
        throws BuildException
    {
        try
        {
            Configuration serviceRef = dependency.getChild( "service" );
            final ReferenceDescriptor service =
              buildReferenceDescriptor( serviceRef );
            final boolean optional =
              dependency.getAttributeAsBoolean( "optional", false );
            final Properties attributes =
              buildAttributes( dependency.getChild( "attributes" ) );
            String role = dependency.getChild( "role" ).getValue( null );

            // default to name of service if role unspecified
            if( null == role ) role = service.getClassname();
            return new DependencyDescriptor( role, service, optional, attributes );
        }
        catch( Throwable e )
        {
            final String error =
              "Error occured while attempting to build dependency descriptor from legacy blockinfo descriptor: "
              + ConfigurationUtil.list( dependency );
            throw new BuildException( error, e );
        }
    }

}
