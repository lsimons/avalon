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

package org.apache.avalon.composition.model;

import org.apache.avalon.composition.data.ComponentProfile;
import org.apache.avalon.composition.model.ProfileUnknownException;
import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;
import org.apache.avalon.meta.info.Type;

/**
 * A type manager implemetation provides support for the creation,
 * storage and retrival of component types.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.3 $ $Date: 2004/01/13 11:41:24 $
 */
public interface TypeRepository
{
    /**
     * Return all availble types.
     * @return the array of types
     */
    Type[] getTypes();

    /**
     * Return all the types available within the repository.
     * @param policy if TRUE, return all available types, if FALSE
     *   return only the locally established types.
     * @return the array of types
     */
    Type[] getTypes( boolean policy );

    /**
     * Locate a {@link Type} instances associated with the
     * supplied implementation classname.
     * @param clazz the component type implementation class.
     * @return the type matching the supplied implementation classname.
     * @exception UnknownTypeException if a matching type cannot be found
     */
    Type getType( Class clazz ) throws TypeUnknownException;

    /**
     * Locate a {@link Type} instances associated with the
     * supplied implementation classname.
     * @param classname the component type implementation class name.
     * @return the type matching the supplied implementation classname.
     * @exception UnknownTypeException if a matching type cannot be found
     */
    Type getType( String classname ) throws TypeUnknownException;

    /**
     * Locate the set of component types capable of services the supplied
     * dependency.
     * @param dependency a service dependency descriptor
     * @return a set of types capable of servicing the supplied dependency
     */
    Type[] getTypes( DependencyDescriptor dependency );

    /**
     * Locate the set of component types that provide the supplied extension.
     * @param stage a stage descriptor
     * @return a set of types that support the supplied stage
     */
    Type[] getTypes( StageDescriptor stage );

   /**
    * Return the set of deployment profiles for the supplied type. An 
    * implementation is required to return a array of types > 0 in length
    * or throw a TypeUnknownException.
    * @param type the type
    * @return a profile array containing at least one deployment profile
    * @exception TypeUnknownException if the supplied type is unknown
    */
    ComponentProfile[] getProfiles( Type type ) throws TypeUnknownException;

   /**
    * Return a deployment profile for the supplied type and key.
    * @param type the type
    * @param key the profile name
    * @return a profile matching the supplied key
    * @exception TypeUnknownException if the supplied type is unknown
    * @exception ProfileUnknownException if the supplied key is unknown
    */
    ComponentProfile getProfile( Type type, String key ) 
      throws TypeUnknownException, ProfileUnknownException;


}
