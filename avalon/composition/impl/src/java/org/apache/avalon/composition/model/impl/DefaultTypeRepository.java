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

package org.apache.avalon.composition.model.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.avalon.composition.model.ProfileSelector;
import org.apache.avalon.composition.model.TypeRepository;
import org.apache.avalon.composition.model.TypeUnknownException;
import org.apache.avalon.composition.model.ProfileUnknownException;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.composition.data.ProfilePackage;
import org.apache.avalon.composition.data.DeploymentProfile;
import org.apache.avalon.composition.data.ComponentProfile;
import org.apache.avalon.composition.data.builder.ProfilePackageBuilder;
import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.ReferenceDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;
import org.apache.avalon.meta.info.Type;

/**
 * A type manager implemetation provides support for the creation,
 * storage and retrival of component types.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.5 $ $Date: 2004/01/21 00:10:27 $
 */
class DefaultTypeRepository implements TypeRepository
{
    //==============================================================
    // static
    //==============================================================

    private static final Resources REZ =
      ResourceManager.getPackageResources( DefaultClassLoaderModel.class );

    /**
     * The profile builder.
     */
    private static final ProfilePackageBuilder PACKAGE_BUILDER = 
      new ProfilePackageBuilder();

    //==============================================================
    // immutable state
    //==============================================================

    private final Logger m_logger;

    private final ClassLoader m_classloader;

    /**
     * The parent type manager (may be null)
     */
    private final TypeRepository m_parent;

    /**
     * Table of component types keyed by implementation classname.
     */
    private final Hashtable m_types = new Hashtable();

    /**
     * Table of packaged profiles keyed by implementation classname.
     */
    private final Hashtable m_profiles = new Hashtable();

    //==============================================================
    // constructor
    //==============================================================

    /**
     * Creation of a new root type manager.
     * @param types the list of types local to the repository
     * @exception NullPointerException if the type list is null
     */
    public DefaultTypeRepository( Logger logger, ClassLoader classloader, List types )
      throws Exception
    {
        this( logger, classloader, null, types );
    }

    /**
     * Creation of a new type manager.
     * @param logger the assigned logging channel
     * @param classloader the classloader
     * @param parent the parent type repository
     * @param types the list of types local to the repository
     * @exception NullPointerException if the type list is null
     */
    public DefaultTypeRepository( 
      final Logger logger, final ClassLoader classloader, final TypeRepository parent, 
      final List types ) throws Exception
    {
        if( types == null )
        {
            throw new NullPointerException( "types" );
        }

        m_parent = parent;
        m_classloader = classloader;
        m_logger = logger;

        //
        // build up a map of types in the repository keyed by classname
        //

        if( getLogger().isDebugEnabled() )
        {
            final String message = 
              REZ.getString( "type.repository.count", new Integer( types.size() ) );
            getLogger().debug( message );
        }

        Iterator iterator = types.iterator();
        while( iterator.hasNext() )
        {
            Type type = (Type) iterator.next();
            final String name = type.getInfo().getName();
            final String classname = type.getInfo().getClassname();
            Class clazz = m_classloader.loadClass( classname );

            ProfilePackage pack = 
              PACKAGE_BUILDER.createProfilePackage( name, clazz );
            m_profiles.put( classname, pack );
            int n = pack.getComponentProfiles().length;

            m_types.put( classname, type );
            if( getLogger().isDebugEnabled() )
            {
                final String message = 
                  REZ.getString( 
                    "type.repository.addition", classname, 
                    new Integer( n ).toString() );
                getLogger().debug( message );
            }
        }
    }

    //==============================================================
    // DefaultTypeRepository
    //==============================================================

    /**
     * Return all available types.
     * @return the array of types
     */
    public Type[] getTypes()
    {
        return getTypes( true );
    }

    /**
     * Return all the types available within the repository.
     *
     * @param policy if TRUE, return all available types, if FALSE
     *   return only the locally established types.
     * @return the array of types
     */
    public Type[] getTypes( boolean policy )
    {
        if( policy && ( m_parent != null ))
        {
            ArrayList list = new ArrayList( m_types.values() );
            Type[] types = m_parent.getTypes();
            for( int i = 0; i < types.length; i++ )
            {
                list.add( types[i] );
            }
            return (Type[]) list.toArray( new Type[0] );
        }
        else
        {
            return (Type[]) m_types.values().toArray( new Type[0] );
        }
    }

    /**
     * Locate a {@link Type} instances associated with the
     * supplied implementation classname.
     *
     * @param clazz the component type implementation class.
     * @return the type matching the supplied implementation classname.
     * @exception TypeUnknownException if a matching type cannot be found
     */
    public Type getType( Class clazz ) throws TypeUnknownException
    {
        if( clazz == null )
        {
            throw new NullPointerException( "clazz" );
        }
        return getType( clazz.getName() );
    }

    /**
     * Locate a {@link Type} instances associated with the
     * supplied implementation classname.
     *
     * @param classname the component type implementation class name.
     * @return the type matching the supplied implementation classname.
     * @exception TypeUnknownException if a matching type cannot be found
     */
    public Type getType( String classname ) throws TypeUnknownException
    {
        if( classname == null )
        {
            throw new NullPointerException( "classname" );
        }

        if( m_parent != null )
        {
            try
            {
                return m_parent.getType( classname );
            }
            catch( TypeUnknownException tue )
            {
                // continue
            }
        }

        Type type = (Type) m_types.get( classname );
        if( type == null )
        {
            final String error = 
              REZ.getString( "type.repository.unknown-type", classname );
            throw new TypeUnknownException( error );
        }

        return type;
    }

    /**
     * Locate the set of component types in the local repository 
     * capable of servicing the supplied dependency.
     *
     * @param dependency a service dependency descriptor
     * @return a set of types capable of servicing the supplied dependency
     */
    public Type[] getTypes( DependencyDescriptor dependency )
    {
        return getTypes( dependency, true );
    }

    /**
     * Locate the set of component types in the local repository 
     * capable of servicing the supplied dependency.
     *
     * @param dependency a service dependency descriptor
     * @return a set of types capable of servicing the supplied dependency
     */
    public Type[] getTypes( DependencyDescriptor dependency, boolean search )
    {
        if( dependency == null )
        {
            throw new NullPointerException( "dependency" );
        }

        ArrayList list = new ArrayList();
        ReferenceDescriptor reference = dependency.getReference();
        Type[] types = getTypes( false );
        for( int i=0; i<types.length; i++ )
        {
            Type type = types[i];
            if( type.getService( reference ) != null )
            {
                list.add( type );
            }
        }

        if( search && m_parent != null )
        {
            Type[] suppliment = m_parent.getTypes( dependency );
            for( int i=0; i<suppliment.length; i++ )
            {
                list.add( suppliment[i] );
            }
        }

        return (Type[]) list.toArray( new Type[0] );
    }

    /**
     * Locate the set of local component types that provide the 
     * supplied extension.
     * @param stage a stage descriptor
     * @return a set of types that support the supplied stage
     */
    public Type[] getTypes( StageDescriptor stage )
    {
        if( stage == null )
        {
            throw new NullPointerException( "stage" );
        }

        ArrayList list = new ArrayList();
        Type[] types = getTypes( false );
        for( int i=0; i<types.length; i++ )
        {
            Type type = types[i];
            if( type.getExtension( stage ) != null )
            {
                list.add( type );
            }
        }

        if( m_parent != null )
        {
            Type[] suppliment = m_parent.getTypes( stage );
            for( int i=0; i<suppliment.length; i++ )
            {
                list.add( suppliment[i] );
            }
        }

        return (Type[]) list.toArray( new Type[0] );
    }

   /**
    * Return the set of deployment profiles for the supplied type. An 
    * implementation is required to return a array of types > 0 in length
    * or throw a TypeUnknownException.
    * @param type the type
    * @return a profile array containing at least one deployment profile
    * @exception TypeUnknownException if the supplied type is unknown
    */
    public ComponentProfile[] getProfiles( Type type ) 
      throws TypeUnknownException
    {
        return getProfiles( type, true );
    }

   /**
    * Return the set of deployment profiles for the supplied type. An 
    * implementation is required to return a array of types > 0 in length
    * or throw a TypeUnknownException.
    * @param type the type
    * @param search if the local search failes and search is TRUE then 
    *    delegate to a parent repository if available
    * @return a profile array containing at least one deployment profile
    * @exception TypeUnknownException if the supplied type is unknown
    */
    private ComponentProfile[] getProfiles( Type type, boolean search ) 
      throws TypeUnknownException
    {
        final String classname = type.getInfo().getClassname();
        ProfilePackage profiles = (ProfilePackage) m_profiles.get( classname );
        if( profiles != null )
        {
            return profiles.getComponentProfiles();
        }
        else
        {
            if( search && m_parent != null )
            {
                return m_parent.getProfiles( type );
            }
            else
            {
                final String error = 
                  REZ.getString( "type.repository.unknown-type", classname );
                throw new TypeUnknownException( error );
            }
        }
    }

   /**
    * Return a deployment profile for the supplied type and key.
    * @param type the type
    * @param key the profile name
    * @return a profile matching the supplied key
    * @exception TypeUnknownException if the supplied type is unknown
    * @exception ProfileUnknownException if the supplied key is unknown
    */
    public ComponentProfile getProfile( Type type, String key ) 
      throws TypeUnknownException, ProfileUnknownException
    {
        ComponentProfile[] profiles = getProfiles( type );
        for( int i=0; i<profiles.length; i++ )
        {
            ComponentProfile profile = profiles[i];
            final String name = getProfileName( type, key );
            if( profile.getName().equals( name ) ) return profile;
        }
        throw new ProfileUnknownException( key );
    }

   /**
    * Attempt to locate a packaged deployment profile meeting the 
    * supplied dependency description.
    *
    * @param dependency the dependency description 
    * @param search the search policy  
    * @return the deployment profile (possibly null) 
    */
    public DeploymentProfile getProfile( 
      DependencyDescriptor dependency, boolean search )
    {
        DeploymentProfile[] profiles = getProfiles( dependency, search );
        ProfileSelector profileSelector = new DefaultProfileSelector();
        return profileSelector.select( profiles, dependency );
    }

   /**
    * Return a set of local deployment profile for the supplied dependency.
    * @param dependency the dependency descriptor
    * @return a set of profiles matching the supplied dependency
    */
    public DeploymentProfile[] getProfiles( DependencyDescriptor dependency, boolean search ) 
    {
        Type[] types = getTypes( dependency, search );
        ArrayList list = new ArrayList();
        for( int i=0; i<types.length; i++ )
        {
            Type type = types[i];
            try
            {
                DeploymentProfile[] profiles = getProfiles( type, false );
                for( int j=0; j<profiles.length; j++ )
                {
                    list.add( profiles[j] );
                }
            }
            catch( TypeUnknownException e )
            {
                final String error = 
                  "Unexpected condition: " + e.toString();
                throw new IllegalStateException( error );
            }
        }
        return (DeploymentProfile[]) list.toArray( new DeploymentProfile[0] );
    }

   /**
    * Return the set of local profiles.
    * @return a profile or null if a profile connot be resolve
    */
    private DeploymentProfile[] getProfiles()
    {
        return (DeploymentProfile[]) 
          m_profiles.values().toArray( new DeploymentProfile[0] );
    }

   /**
    * Return the logging channel assigned to the component.
    * @return the assigned logging channel
    */
    protected Logger getLogger()
    {
        return m_logger;
    }

   /**
    * Return the name of a packaged profile given the type and the 
    * packaged profile key.  The key corresponds to the name attribute
    * declared under the profile definition.
    *
    * @param type the component type
    * @param key the profile name
    * @return the composite name
    */
    private String getProfileName( Type type, String key )
    {
        return type.getInfo().getName() + "-" + key;
    }

}
