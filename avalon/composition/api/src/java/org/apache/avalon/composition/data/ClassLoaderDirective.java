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

package org.apache.avalon.composition.data;

import java.io.Serializable;

/**
 * Description of classloader.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.4 $ $Date: 2004/01/19 14:14:56 $
 */
public final class ClassLoaderDirective implements Serializable
{
    private static final LibraryDirective EMPTY_LIBRARY = new LibraryDirective();
    private static final ClasspathDirective EMPTY_CLASSPATH = new ClasspathDirective();
    private static final GrantDirective EMPTY_GRANT = new GrantDirective();

    /**
     * The library directive.
     */
    private LibraryDirective m_library;

    /**
     * The root category hierachy.
     */
    private ClasspathDirective m_classpath;

    private GrantDirective m_grantDirective;
    
    /**
     * Create an empty ClassloaderDirective.
     */
    public ClassLoaderDirective()
    {
        this( null, null, null );
    }

    /**
     * Create a ClassloaderDirective instance.
     *
     * @param library the library descriptor
     * @param classpath the classpath descriptor
     * @param grant the security policy declared for the classloader
     */
    public ClassLoaderDirective( 
       final LibraryDirective library,
       final ClasspathDirective classpath,
       final GrantDirective grant )
    {
        if( library == null )
        {
            m_library = EMPTY_LIBRARY;
        }
        else
        {
            m_library = library;
        }

        if( classpath == null )
        {
            m_classpath = EMPTY_CLASSPATH;
        }
        else
        {
            m_classpath = classpath;
        }

        if( grant == null )
        {
            m_grantDirective = EMPTY_GRANT;
        }
        else
        {
            m_grantDirective = grant;
        }
    }

   /**
    * Return true if the library and classpath declarations are empty.
    * If the function returns true, this directive is in an effective 
    * default state and need not be externalized.
    *
    * @return the empty status of this directive
    */
    public boolean isEmpty()
    {
        return ( m_library.isEmpty() && m_classpath.isEmpty() );
    }

    /**
     * Return the library directive.
     *
     * @return the library directive.
     */
    public LibraryDirective getLibrary()
    {
        return m_library;
    }

    /**
     * Return the classpath directive.
     *
     * @return the classpath directive.
     */
    public ClasspathDirective getClasspathDirective()
    {
        return m_classpath;
    }
    
    public GrantDirective getGrantDirective()
    {
        return m_grantDirective;
    }
}
