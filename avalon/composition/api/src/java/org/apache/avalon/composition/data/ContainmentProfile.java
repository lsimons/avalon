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

package org.apache.avalon.composition.data;

import java.util.ArrayList;

import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.ReferenceDescriptor;
import org.apache.avalon.meta.info.ServiceDescriptor;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.configuration.Configuration;

/**
 * A containment profile describes a containment context including
 * a classloader and the set of profiles explicitly included within
 * the a container.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 09:31:04 $
 */
public class ContainmentProfile extends Profile
{
    //========================================================================
    // static
    //========================================================================

    /**
     * Container path delimiter.
     */
    public static final String DELIMITER = "/";

    private static final ServiceDirective[] EMPTY_SERVICES =
      new ServiceDirective[0];

    private static final Profile[] EMPTY_PROFILES = new Profile[0];

    private static final CategoriesDirective EMPTY_CATEGORIES = new CategoriesDirective();

    private static final ClassLoaderDirective EMPTY_CLASSLOADER =
      new ClassLoaderDirective( new LibraryDirective(), new ClasspathDirective() );

    //========================================================================
    // state
    //========================================================================

    /**
     * The classloader directive.
     */
    private ClassLoaderDirective m_classloader;

   /**
    * The published service directives.
    */
    private final ServiceDirective[] m_export;

    /**
     * The profiles described within the scope of the containment profile.
     */
    private final Profile[] m_profiles;

    /**
     * The assigned logging categories.
     */
    private CategoriesDirective m_categories;

    //--------------------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------------------

   /**
    * Creation of a new empty containment profile.
    */
    public ContainmentProfile()
    {
        this( "container", null, null, null, null );
    }

   /**
    * Creation of a new containment profile.
    *
    * @param name the profile name
    * @param classloader the description of the classloader to be 
    *    created for this containment profile
    * @param exports the set of servides that this component is 
    *    dependent on for normal execution
    * @param profiles the set of profiles contained within this 
    *    containment profile
    */
    public ContainmentProfile( 
      final String name, final ClassLoaderDirective classloader, 
      final ServiceDirective[] exports,
      final CategoriesDirective categories, 
      Profile[] profiles )
    {
        super( name, true, Mode.EXPLICIT );

        m_categories = categories;
        m_classloader = classloader;
        m_profiles = profiles;
        m_export = exports;
    }

    //--------------------------------------------------------------------------
    // implementation
    //--------------------------------------------------------------------------

    /**
     * Return the logging categories for the profile.
     *
     * @return the categories
     */
    public CategoriesDirective getCategories()
    {
        if( m_categories == null ) return EMPTY_CATEGORIES;
        return m_categories;
    }

    /**
     * Return the classloader directive that describes the creation
     * arguments for the classloader required by this container.
     *
     * @return the classloader directive
     */
    public ClassLoaderDirective getClassLoaderDirective()
    {
        if( m_classloader == null ) return EMPTY_CLASSLOADER;
        return m_classloader;
    }

    /**
     * Return the set of service directives that describe the mapping 
     * between services exposrted by the container and its implementation
     * model.
     *
     * @return the array of service directives
     */
    public ServiceDirective[] getExportDirectives()
    {
        if( m_export == null ) return EMPTY_SERVICES;
        return m_export;
    }

    /**
     * Retrieve a service directive matching a supplied class.
     *
     * @param clazz the class to match
     * @return the service directive or null if it does not exist
     */
    public ServiceDirective getExportDirective( final Class clazz )
    {
        final String classname = clazz.getName();
        ServiceDescriptor[] services = getExportDirectives();
        for( int i = 0; i <services.length; i++ )
        {
            final ServiceDirective virtual = (ServiceDirective) services[i];
            if( virtual.getReference().getClassname().equals( classname ) )
            {
                return virtual;
            }
        }
        return null;
    }

    /**
     * Return the set of nested profiles wihin this containment profile.
     *
     * @return the profiles nested in this containment profile
     */
    public Profile[] getProfiles()
    {
        if( m_profiles == null ) return EMPTY_PROFILES;
        return m_profiles;
    }

    /**
     * Return the set of nested profiles contained within this profile matching
     * the supplied mode.
     *
     * @param mode one of enumerated value {@link Mode#IMPLICIT}, 
     *    {@link Mode#PACKAGED}, or {@link Mode#EXPLICIT}
     * @return the profiles matching the supplied creation mode
     */
    public Profile[] getProfiles( Mode mode )
    {
        Profile[] profiles = getProfiles();
        return selectProfileByMode( profiles, mode );
    }

    /**
     * Returns a sub-set of the supplied containers matching the supplied creation mode.
     * @param profiles the profiles to select from
     * @param mode the creation mode to retrict the returned selection to
     * @return the subset of the supplied profiles with a creation mode matching
     *   the supplied mode value
     */
    private Profile[] selectProfileByMode( Profile[] profiles, Mode mode )
    {
        ArrayList list = new ArrayList();
        for( int i = 0; i < profiles.length; i++ )
        {
            Profile profile = profiles[ i ];
            if( profile.getMode().equals( mode ) )
            {
                list.add( profile );
            }
        }
        return (Profile[])list.toArray( new Profile[0] );
    }
}
