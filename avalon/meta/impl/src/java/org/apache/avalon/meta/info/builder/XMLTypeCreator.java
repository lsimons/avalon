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
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.meta.ConfigurationBuilder;
import org.apache.avalon.meta.info.InfoDescriptor;
import org.apache.avalon.meta.info.ContextDescriptor;
import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.EntryDescriptor;
import org.apache.avalon.meta.info.ExtensionDescriptor;
import org.apache.avalon.meta.info.CategoryDescriptor;
import org.apache.avalon.meta.info.ReferenceDescriptor;
import org.apache.avalon.meta.info.ServiceDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;
import org.apache.avalon.meta.info.Type;
import org.apache.excalibur.configuration.ConfigurationUtil;
import org.xml.sax.InputSource;

/**
 * Handles internalization of an XML based description of a {@link Type}
 * from a Configuration object. The format for Configuration object
 * is specified in the <a href="package-summary.html#external">package summary</a>.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 08:15:38 $
 */
public class XMLTypeCreator
    extends XMLServiceCreator implements TypeFactory
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( XMLTypeCreator.class );

    /**
     * Create a {@link Type} object for specified
     * classname, loaded from specified {@link InputStream}.
     *
     * @param classname The classname of Component
     * @param inputStream the InputStream to load Type from
     * @return the created Type
     * @throws Exception if an error occurs
     */
    public Type createType( String classname,
                            InputStream inputStream )
        throws Exception
    {
        if( inputStream == null )
        {
            throw new NullPointerException( "input" );
        }

        final InputSource input = new InputSource( inputStream );
        final Configuration xinfo = ConfigurationBuilder.build( input );
        return createType( classname, xinfo, (Configuration) null );
    }

    /**
     * Create an {@link Type} object for a specified classname from
     * specified configuration data.
     *
     * @param classname The classname of Component
     * @param info the Type configuration
     * @param defaults the default configuration
     * @return the created Type
     * @throws Exception if an error occurs
     */
    public Type createType( 
        final String classname, final Configuration info, final Configuration defaults )
        throws BuildException
    {
        final String topLevelName = info.getName();
         
        if( topLevelName.equals( "blockinfo" ) )
        {
            return new XMLLegacyCreator().createType( classname, info );
        }

        if( !topLevelName.equals( "type" ) )
        {
            final String message =
                REZ.getString( "builder.bad-toplevel-element.error",
                               classname,
                               topLevelName );
            throw new BuildException( message );
        }

        Configuration configuration = null;

        //
        // changed the information block from "component" to "info" to
        // avoid confusion with the "component" element definition in the
        // meta-data level - change is backward compatible
        //

        configuration = info.getChild( "info", false );

        final InfoDescriptor descriptor =
            buildInfoDescriptor( classname, configuration );

        configuration = info.getChild( "loggers" );
        final CategoryDescriptor[] loggers = buildLoggers( configuration );

        configuration = info.getChild( "context" );
        final ContextDescriptor context = buildContext( configuration );

        configuration = info.getChild( "services" );
        final ServiceDescriptor[] services = buildServices( configuration );

        configuration = info.getChild( "dependencies" );
        final DependencyDescriptor[] dependencies = 
           buildDependencies( configuration );

        configuration = info.getChild( "stages" );
        final StageDescriptor[] phases = buildStages( configuration );

        configuration = info.getChild( "extensions" );
        final ExtensionDescriptor[] extensions = buildExtensions( configuration );

        return new Type(
          descriptor, loggers, context, services, dependencies, phases, 
          extensions, defaults );
    }

    /**
     * Utility function to create a set of phase descriptor from a configuration.
     * @param config a configuration containing 0..n phase elements
     * @return an array of phase descriptors
     * @exception Exception if a build error occurs
     */
    protected StageDescriptor[] buildStages( Configuration config ) 
      throws BuildException
    {
        ArrayList list = new ArrayList();
        Configuration[] stages = config.getChildren( "stage" );
        for( int i = 0; i < stages.length; i++ )
        {
            StageDescriptor stage = buildPhase( stages[i] );
            list.add( stage );
        }
        return (StageDescriptor[])list.toArray( new StageDescriptor[ 0 ] );
    }

    /**
     * Utility function to create a set of phase descriptor from a configuration.
     * @param config a configuration containing 0..n phase elements
     * @return an array of phase descriptors
     * @exception Exception if a build error occurs
     */
    protected StageDescriptor buildPhase( Configuration config ) 
      throws BuildException
    {

        try
        {
            String id = null;
            if( config.getAttribute( "type", null ) != null ) // legacy
            {
                id = config.getAttribute( "type" );
            }
            else if( config.getAttribute( "key", null ) != null ) // legacy
            {
               id = config.getAttribute( "key" );
            }

            if( id == null ) id = config.getAttribute( "id" ); // standard

            final Properties attributes =
              buildAttributes( config.getChild( "attributes" ) );
            return new StageDescriptor( id, attributes );
        }
        catch( Throwable e )
        {
            final String error = 
              "Unable to construct a stage descriptor from source fragment:"
              + ConfigurationUtil.list( config );
            throw new BuildException( error, e );
        }
    }

    /**
     * A utility method to build a {@link ReferenceDescriptor}
     * object from specified configuration data.
     *
     * @param service the service Configuration
     * @return the created ReferenceDescriptor
     * @throws ConfigurationException if an error occurs
     */
    protected ReferenceDescriptor buildReferenceDescriptor( final Configuration service )
        throws BuildException
    {
        return buildReferenceDescriptor( service, null );
    }

    /**
     * A utility method to build a {@link ReferenceDescriptor}
     * object from specified configuration data.
     *
     * @param service the service Configuration
     * @param classname the default type classname
     * @return the created ReferenceDescriptor
     * @throws ConfigurationException if an error occurs
     */
    protected ReferenceDescriptor buildReferenceDescriptor( final Configuration service, String classname )
        throws BuildException
    {
        final String type = service.getAttribute("type", classname );
        if( type == null )
        {
            final String error = 
              "Missing 'type' attribute in configuration: " 
              + ConfigurationUtil.list( service );
            throw new BuildException( error );
        }
        if( type.indexOf( ":" ) > -1 )
        {
            return createReference( type );
        }
        final String versionString = service.getAttribute( "version", "1.0" );
        final Version version = buildVersion( versionString );
        return new ReferenceDescriptor( type, version );
    }

    /**
     * A utility method to build an array of {@link CategoryDescriptor} objects
     * from specified configuraiton.
     *
     * @param configuration the loggers configuration
     * @return the created CategoryDescriptor
     * @throws ConfigurationException if an error occurs
     */
    protected CategoryDescriptor[] buildLoggers( final Configuration configuration )
        throws BuildException
    {
        final Configuration[] elements = configuration.getChildren( "logger" );
        final ArrayList loggers = new ArrayList();

        for( int i = 0; i < elements.length; i++ )
        {
            final CategoryDescriptor logger = buildLogger( elements[ i ] );
            loggers.add( logger );
        }

        return (CategoryDescriptor[])loggers.toArray( new CategoryDescriptor[ loggers.size() ] );
    }

    /**
     * A utility method to build a {@link CategoryDescriptor}
     * object from specified configuraiton.
     *
     * @param logger the Logger configuration
     * @return the created CategoryDescriptor
     * @throws ConfigurationException if an error occurs
     */
    private CategoryDescriptor buildLogger( Configuration logger )
        throws BuildException
    {
        final Properties attributes = buildAttributes( logger.getChild( "attributes" ) );
        final String name = logger.getAttribute( "name", "" );
        return new CategoryDescriptor( name, attributes );
    }

    /**
     * A utility method to build an array of {@link DependencyDescriptor}
     * objects from specified configuration.
     *
     * @param configuration the dependencies configuration
     * @return the created DependencyDescriptor
     * @throws ConfigurationException if an error occurs
     */
    public DependencyDescriptor[] buildDependencies( final Configuration configuration )
        throws BuildException
    {
        final Configuration[] elements = configuration.getChildren( "dependency" );
        final ArrayList dependencies = new ArrayList();

        for( int i = 0; i < elements.length; i++ )
        {
            final DependencyDescriptor dependency =
                buildDependency( elements[ i ] );
            dependencies.add( dependency );
        }

        return (DependencyDescriptor[])dependencies.toArray( new DependencyDescriptor[ 0 ] );
    }

    /**
     * A utility method to build a {@link DependencyDescriptor}
     * object from specified configuraiton.
     *
     * @param dependency the dependency configuration
     * @return the created DependencyDescriptor
     * @throws BuildException if an error occurs
     */
    protected DependencyDescriptor buildDependency( final Configuration dependency )
        throws BuildException
    {
        String role = dependency.getAttribute( "role", null ); // legacy
        if( role == null )
        {
            role = dependency.getAttribute( "key", null );
        }
        ReferenceDescriptor reference = 
          buildReferenceDescriptor( dependency );
        
        final boolean optional =
           dependency.getAttributeAsBoolean( "optional", false );
        final Properties attributes =
            buildAttributes( dependency.getChild( "attributes" ) );

        //
        // default to name of service if role unspecified
        //

        if( null == role )
        {
            role = reference.getClassname();
        }

        return new DependencyDescriptor( role, reference, optional, attributes );
    }

    /**
     * A utility method to build a {@link ContextDescriptor}
     * object from specified configuraiton.
     *
     * @param context the dependency configuration
     * @return the created ContextDescriptor
     * @throws ConfigurationException if an error occurs
     */
    protected ContextDescriptor buildContext( final Configuration context )
        throws BuildException
    {
        final EntryDescriptor[] entrys =
          buildEntries( context.getChildren( "entry" ) );

        final Properties attributes =
          buildAttributes( context.getChild( "attributes" ) );

        String classname = context.getAttribute( "type", null );

        return new ContextDescriptor( classname, entrys, attributes );
    }


    /**
     * A utility method to build an array of {@link ServiceDescriptor}
     * objects from specified configuraiton.
     *
     * @param servicesSet the services configuration
     * @return the created ServiceDescriptor
     * @throws ConfigurationException if an error occurs
     */
    public ServiceDescriptor[] buildServices( final Configuration servicesSet )
        throws BuildException
    {
        final Configuration[] elements = servicesSet.getChildren( "service" );
        final ArrayList services = new ArrayList();

        for( int i = 0; i < elements.length; i++ )
        {
            final ServiceDescriptor service = buildService( elements[ i ] );
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
     * @throws ConfigurationException if an error occurs
     */
    public ServiceDescriptor buildService( final Configuration service )
        throws BuildException
    {
        ReferenceDescriptor reference = buildReferenceDescriptor( service );
        final Properties attributes =
            buildAttributes( service.getChild( "attributes" ) );
        return new ServiceDescriptor( reference, attributes );
    }

    /**
     * A utility method to build a {@link InfoDescriptor}
     * object from specified configuraiton data and classname.
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
        final Version version = 
          buildVersion( info.getChild( "version" ).getValue( "1.0" ) );
        final String schema = 
          info.getChild( "schema" ).getValue( null );
        final Properties attributes =
            buildAttributes( info.getChild( "attributes" ) );
        final String lifestyle = 
          buildLifestyle( info, attributes );
        return new InfoDescriptor( 
          name, classname, version, lifestyle, schema, attributes );
    }

   /**
    * Handle the resolution of the component lifestyle.  Normally this is 
    * resolved by retrieving the &lt;lifestyle&gt; element value, however, for 
    * backward compatability - if the lifecycle element is not present, we will
    * attempt to resolve the lifestyle using the attribute value relative to the
    * key urn:avalon:lifestyle.
    *
    * @param info the info configuration fragment
    * @param attributes the component attributes
    * @return the lifestyle policy value
    */
    private String buildLifestyle( Configuration info, Properties attributes )
    {
        String lifestyle = info.getChild( "lifestyle" ).getValue( null );
        if( lifestyle != null )
        {
            return lifestyle;
        }
        else
        {
            return attributes.getProperty( "urn:avalon:lifestyle" );
        }
    }

    /**
     * Utility function to create a set of phase descriptor from a configuration.
     * @param config a configuration containing 0..n phase elements
     * @return an array of phase descriptors
     * @exception Exception if a build error occurs
     */
    protected ExtensionDescriptor[] buildExtensions( Configuration config ) 
      throws BuildException
    {
        ArrayList list = new ArrayList();
        Configuration[] extensions = config.getChildren( "extension" );
        for( int i = 0; i < extensions.length; i++ )
        {
            list.add( buildExtension( extensions[i] ) );
        }
        return (ExtensionDescriptor[])list.toArray( new ExtensionDescriptor[ 0 ] );
    }

    /**
     * Utility function to create an extension descriptor from a configuration.
     * @param config a configuration containing the extension definition
     * @return the extension descriptor
     * @exception Exception if a build error occurs
     */
    protected ExtensionDescriptor buildExtension( Configuration config ) 
      throws BuildException
    {
        if( config.getAttribute( "type", null ) != null ) // legacy
        {
            String urn = config.getAttribute( "type", null );
            final Properties attributes =
              buildAttributes( config.getChild( "attributes" ) );
            return new ExtensionDescriptor( urn, attributes );
        }
        else
        {
            String id = config.getAttribute( "urn", null ); // legacy
            if( id == null )
            {
               try
               {
                   id = config.getAttribute( "id" );
               }
               catch( ConfigurationException e )
               {
                   final String error = 
                     "Missing extensions identifier 'id' attribute."
                     + ConfigurationUtil.list( config );
                   throw new BuildException( error, e );
               }
            }
            final Properties attributes =
              buildAttributes( config.getChild( "attributes" ) );
            return new ExtensionDescriptor( id, attributes );
        }
    }

   /**
    * Creation of a reference descriptor from the supplied path.
    * @param path the classname
    * @return the reference descriptor
    */
    public ReferenceDescriptor createReference( String path ) 
      throws BuildException
    {
        final String type;
        final Version version;
        int index = path.indexOf(":");
        if( index > -1 )
        {
            type = path.substring( 0, index );
            version = buildVersion( path.substring( index + 1 ) );
        }
        else
        {
            type = path;
            version = buildVersion( "1.0.0" );
        }

        return new ReferenceDescriptor( type, version );
    }
}
