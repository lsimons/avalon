/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.avalon.composition.data.builder;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.ServiceDescriptor;
import org.apache.avalon.composition.data.BlockCompositionDirective;
import org.apache.avalon.composition.data.BlockIncludeDirective;
import org.apache.avalon.composition.data.CategoriesDirective;
import org.apache.avalon.composition.data.ContainmentProfile;
import org.apache.avalon.composition.data.DeploymentProfile;
import org.apache.avalon.composition.data.FilesetDirective;
import org.apache.avalon.composition.data.LibraryDirective;
import org.apache.avalon.composition.data.ClasspathDirective;
import org.apache.avalon.composition.data.IncludeDirective;
import org.apache.avalon.composition.data.ClassLoaderDirective;
import org.apache.avalon.composition.data.RepositoryDirective;
import org.apache.avalon.composition.data.ResourceDirective;
import org.apache.avalon.composition.data.Mode;
import org.apache.avalon.composition.data.Profile;
import org.apache.avalon.composition.data.ServiceDirective;
import org.apache.avalon.composition.data.MetaDataException;
import org.apache.avalon.composition.data.MetaDataRuntimeException;
import org.apache.avalon.composition.data.Targets;
import org.apache.avalon.composition.data.TargetDirective;
import org.apache.avalon.meta.info.builder.XMLTypeCreator;
import org.apache.excalibur.configuration.ConfigurationUtil;

/**
 * Handles internalization of an XML based description of a {@link ContainmentProfile}
 * from a Configuration object.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 09:31:37 $
 */
public class XMLContainmentProfileCreator extends XMLProfileCreator
{
    private static final XMLTypeCreator TYPE_CREATOR = 
      new XMLTypeCreator();

    private static final XMLDeploymentProfileCreator DEPLOYMENT_CREATOR = 
      new XMLDeploymentProfileCreator();

    private static final XMLTargetsCreator TARGETS_CREATOR = 
      new XMLTargetsCreator();

    /**
     * Create a {@link ContainmentProfile} from a configuration
     *
     * @param config the partitition configuration
     * @return the partition
     * @exception Exception if a error occurs during profile creation
     */
    public ContainmentProfile createContainmentProfile( Configuration config )
      throws Exception
    {
        //
        // build the containment description 
        //

        String name = null;
        if( config.getName().equals( "block" ) )
        {
            // Merlin 2.1 legacy
            name = config.getChild( "info" ).getChild( "name" ).getValue( "untitled" );
        }
        else
        {
            name = getName( null, config, "untitled" );
        }

        ServiceDirective[] exports = 
          createServiceDirectives( config.getChild( "services", false ) );

        //
        // check for any legacy "implementation" tags and if it exists
        // then run with it, otherwise continue with the container defintion
        //

        Configuration implementation = config;
        if( config.getChild( "implementation", false ) != null )
        {
            implementation = config.getChild( "implementation" );
        }

        ClassLoaderDirective classloader = null;
        if( implementation.getChild( "engine", false ) != null )
        {
            // Merlin 2.1 legacy

            Configuration engine = implementation.getChild( "engine" );
            String msg = ConfigurationUtil.list( engine );
            System.out.println("please replace 'engine' with 'classloader'\n" + msg );
            classloader = createClassLoaderDirective( engine );
        }
        else
        {
            classloader = 
              createClassLoaderDirective( 
                implementation.getChild( "classloader", false ) );
        }

        //
        // build any logging category directives
        // 

        final CategoriesDirective categories = 
          getCategoriesDirective( implementation.getChild( "categories", false ), name );

        //
        // build nested profiles
        // 

        final Profile[] profiles = createProfiles( implementation );

        //
        // return the containment profile
        // 

        return new ContainmentProfile( name, classloader, exports, categories, profiles );
    }

    private ClassLoaderDirective createClassLoaderDirective( Configuration config )
       throws ConfigurationException
    {
        if( config == null )
        {
            return null;
        }

        LibraryDirective library = 
          createLibraryDirective( config.getChild( "library", false ) );
        ClasspathDirective classpath = 
          createClasspathDirective( config.getChild( "classpath", false ) );
        return new ClassLoaderDirective( library, classpath );
    }

    private ClasspathDirective createClasspathDirective( Configuration config )
       throws ConfigurationException
    {
        if( config == null )
        {
            return null;
        }

        FilesetDirective[] filesets = createFilesetDirectives( config );
        RepositoryDirective[] repositories = createRepositoryDirectives( config );
        return new ClasspathDirective( filesets, repositories );
    }

    private LibraryDirective createLibraryDirective( Configuration config )
       throws ConfigurationException
    {
        if( config == null )
        {
            return null;
        }

        Configuration[] includes = config.getChildren( "include" );
        String[] inc = new String[ includes.length ];
        for( int i = 0; i < includes.length; i++ )
        {
            inc[i] = getIncludeValue( includes[i] );
        }

        Configuration[] groups = config.getChildren( "group" );
        String[] grp = new String[ groups.length ];
        for( int i = 0; i < groups.length; i++ )
        {
            grp[i] = groups[i].getValue();
        }

        return new LibraryDirective( inc, grp );
    }

    private RepositoryDirective[] createRepositoryDirectives( Configuration config )
       throws ConfigurationException
    {
        if( config == null )
        {
            throw new NullPointerException( "config" );
        }

        Configuration[] children = config.getChildren( "repository" );
        RepositoryDirective[] repositories = new RepositoryDirective[ children.length ];
        for( int i = 0; i < children.length; i++ )
        {
            ResourceDirective[] resources = createResourceDirectives( children[i] );
            repositories[i] = new RepositoryDirective( resources );
        }
        return repositories;
    }

    private ResourceDirective[] createResourceDirectives( Configuration config )
       throws ConfigurationException
    {
        if( config == null )
        {
            throw new NullPointerException( "config" );
        }

        ArrayList res = new ArrayList();
        Configuration[] resources = config.getChildren( "resource" );
        for( int i = 0; i < resources.length; i++ )
        {
            Configuration resource = resources[i];
            res.add( createResourceDirective( resource ) );
        }

        return (ResourceDirective[]) res.toArray( new ResourceDirective[0] );
    }

    private ResourceDirective createResourceDirective( Configuration config )
       throws ConfigurationException
    {
        String id = config.getAttribute( "id" );
        String version = config.getAttribute( "version", null );
        String type = config.getAttribute( "type", null );
        if( type == null )
        {
            return ResourceDirective.createResourceDirective( id, version );
        }
        else
        {
            return ResourceDirective.createResourceDirective( id, version, type );
        }
    }

    private FilesetDirective[] createFilesetDirectives( Configuration config )
       throws ConfigurationException
    {
        ArrayList list = new ArrayList();
        Configuration[] children = config.getChildren( "fileset" );
        for( int i = 0; i < children.length; i++ )
        {
            Configuration child = children[i];
            list.add( createFilesetDirective( child ) );
        }
        return (FilesetDirective[]) list.toArray( new FilesetDirective[0] );
    }

    /**
     * Utility method to create a new fileset descriptor from a
     * configuration instance.
     * @param config a configuration defining the fileset
     * @return the fileset descriptor
     * @exception ConfigurationException if the configuration is
     *   incomplete
     */
    public FilesetDirective createFilesetDirective( Configuration config )
       throws ConfigurationException
    {
        String base = config.getAttribute( "dir", "." );
        IncludeDirective[] includes = createIncludeDirectives( config );
        return new FilesetDirective( base, includes );
    }

    /**
     * Utility method to create a set in include directives.
     * @param config a configuration defining the fileset
     * @return the includes
     * @exception ConfigurationException if the configuration is
     *   incomplete
     */
    protected IncludeDirective[] createIncludeDirectives( Configuration config )
       throws ConfigurationException
    {
        if( config == null )
        {
            return new IncludeDirective[0];
        }

        ArrayList list = new ArrayList();
        Configuration[] children = config.getChildren( "include" );
        for( int i = 0; i < children.length; i++ )
        {
            Configuration child = children[i];
            list.add( createIncludeDirective( child ) );
        }

        return (IncludeDirective[]) list.toArray( new IncludeDirective[0] );
    }

    /**
     * Utility method to create a new include directive from a
     * configuration instance.
     * @param config a configuration defining the include directive
     * @return the include directive
     * @exception ConfigurationException if the configuration does not
     *   declare the name attribute
     */
    protected IncludeDirective createIncludeDirective( Configuration config )
       throws ConfigurationException
    {
        return new IncludeDirective( getIncludeValue( config ) );
    }

    private String getIncludeValue( Configuration config ) 
      throws ConfigurationException
    {
        if( config.getAttribute( "path", null ) != null )
        {
            return config.getAttribute( "path" );
        }
        else if( config.getAttribute( "name", null ) != null )
        {
            return config.getAttribute( "name" );
        }
        else
        {
            return config.getValue();
        }
    }

   /**
    * Return the set of profiles embedded in the supplied 
    * configuration.
    *
    * @param config a container or implementation configutation
    * @return the set of profile 
    */ 
    protected Profile[] createProfiles( Configuration config )
      throws Exception
    {
        ArrayList list = new ArrayList();
        Configuration[] children = config.getChildren();
        for( int i=0; i<children.length; i++ )
        {
            Configuration child = children[i];
            if( !child.getName().equals( "classloader" ) )
            {
                if( child.getName().equals( "container" ) )
                {
                    list.add( createContainmentProfile( child ) );
                }
                else if( child.getName().equals( "component" ) )
                {
                    DeploymentProfile profile = 
                      DEPLOYMENT_CREATOR.createDeploymentProfile( child );
                    list.add( profile );
                }
                else if( child.getName().equals( "include" ) )
                {
                    list.add( createFromInclude( child ) );
                }
            }
        }
        return (Profile[]) list.toArray( new Profile[0] );
    }

   /**
    * Resolve the logical services declared by a block directive.
    * @param config the services configuration fragment
    * @return the set of declared service descriptors
    */
    public ServiceDirective[] createServiceDirectives( Configuration config )
      throws MetaDataException
    {
        if( config == null )
        {
            return new ServiceDirective[0];
        }

        Configuration[] children = config.getChildren( "service" );
        ArrayList list = new ArrayList();
        for( int i=0; i<children.length; i++ )
        {
            list.add( createServiceDirective( children[i] ) );
        }
        return (ServiceDirective[]) list.toArray( new ServiceDirective[0] );
    }

   /**
    * Resolve a service directive declared by a block directive.
    * @param config the service configuration fragment
    * @return the set of declared services directives
    */
    private ServiceDirective createServiceDirective( Configuration config )
      throws MetaDataException
    {
        try
        {
            ServiceDescriptor service = TYPE_CREATOR.buildService( config );
            Configuration source = config.getChild( "source", false );
            if( source == null ) 
            {
                final String error = 
                  "Service configuration must contain a source directive.";
                throw new MetaDataException( error );
            }
            String path = source.getValue();
            return new ServiceDirective( service, path );
        }
        catch( Throwable ce )
        {
            final String error =
              "Invalid service declaration in block specification:\n"
               + ConfigurationUtil.list( config );
            throw new MetaDataException( error, ce );
        }
    }

   /**
    * Create a containment defintion for an include statement. Two variant
    * of include are supported - include by resource reference, and include
    * of a source container defintion.
    *
    * @param the include description
    * @return the containment directive
    */
    private Profile createFromInclude( Configuration config )
      throws MetaDataException, ConfigurationException
    {
        final String name = getBlockIncludeName( config );
        if( config.getAttribute( "id", null ) != null )
        {
            ResourceDirective resource = createResourceDirective( config );
            TargetDirective[] targets = createTargetDirectives( config );
            return new BlockCompositionDirective( name, resource, targets );
        }
        else
        {
            final String path = getBlockIncludePath( config );
            return new BlockIncludeDirective( name, path );
        }
    }

    private TargetDirective[] createTargetDirectives( Configuration config )
      throws MetaDataException
    {
        try
        {
            Targets targets = TARGETS_CREATOR.createTargets( config );
            return targets.getTargets();
        }
        catch( Throwable e )
        {
            final String error = 
              "Unexpected error while attempting to build target directives.";
            throw new MetaDataException( error, e );
        }
    }

    private String getBlockIncludeName( Configuration config ) throws MetaDataException
    {
        try
        {
            return config.getAttribute( "name" );
        }
        catch( ConfigurationException e )
        {
            final String error =
              "Missing 'name' attribute in the block include statement:\n"
               + ConfigurationUtil.list( config );
            throw new MetaDataException( error, e );
        }
    }

    private String getBlockIncludePath( Configuration config ) throws MetaDataException
    {
        try
        {
            Configuration source = config.getChild( "source", false );
            if( null == source )
            {
                final String error =
                  "Missing 'source' element in the block include statement:\n"
                 + ConfigurationUtil.list( config );
                throw new MetaDataException( error );
            }
            return source.getValue();
        }
        catch( ConfigurationException e )
        {
            final String error =
              "Missing source value in the block include statement:\n"
               + ConfigurationUtil.list( config );
            throw new MetaDataException( error, e );
        }
    }
}
