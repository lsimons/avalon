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

package org.apache.avalon.repository;

import java.net.URL;
import javax.naming.directory.Attributes ;

/**
 * A service that provides access to versioned resources.
 * @author <a href="mailto:mcconnell@osm.net">Stephen McConnell</a>
 * @version $Revision: 1.1 $ $Date: 2003/12/04 19:34:29 $
 */
public interface Repository
{
    /**
     * The context resolution key.
     */
    String KEY = "urn:assembly:repository" ;

    /**
     * The seperator character between the group and resource name values
     * in an artifact name.
     */
    String SEPERATOR = ":" ;

   /**
    * Return the metadata of an artifact as attributes.
    * @param artifact the artifact
    * @return the attributes resolved relative to the artifact address
    * @exception RepositoryException if an error occurs while resolving
    *   artifact metadata attributes
    */
    Attributes getAttributes( Artifact artifact ) 
        throws RepositoryException ;
    
    /**
     * Get a resource url relative to the supplied artifact.
     * 
     * @param artifact the artifact describing the resource
     * @return the resource url
     */
    URL getResource( Artifact artifact ) throws RepositoryException;

    /**
     * Creates a ClassLoader chain returning the lowest ClassLoader containing 
     * the jar artifact in the loader's path.  The dependencies of the argument 
     * artifact jar and an api, spi and implementation attribute on the jar and 
     * its dependencies are used to construct the ClassLoaders.
     * 
     * @param artifact the implementation artifact
     * @return the lowest ClassLoader in a chain
     * @throws RepositoryException if there is a problem caching and accessing
     * repository artifacts and reading their attributes.
     */
    ClassLoader getClassLoader( Artifact artifact )
        throws RepositoryException ;

    /**
     * Creates a ClassLoader chain returning the lowest ClassLoader containing 
     * the jar artifact in the loader's path.  The dependencies of the argument 
     * artifact jar and an api, spi and implementation attribute on the jar and 
     * its dependencies are used to construct the ClassLoaders.
     * 
     * @param parent the parent classloader
     * @param artifact the implementation artifact
     * @return the lowest ClassLoader in a chain
     * @throws RepositoryException if there is a problem caching and accessing
     * repository artifacts and reading their attributes.
     */
    ClassLoader getClassLoader( ClassLoader parent, Artifact artifact )
        throws RepositoryException ;
}
