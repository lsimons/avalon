/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 2002-2003 The Apache Software Foundation. All rights reserved.

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

package org.apache.avalon.meta.info.builder.tags;


import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import org.apache.avalon.framework.Version;
import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.ReferenceDescriptor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A doclet tag representing the lifestyle assigned to the Type.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 08:16:15 $
 */
public class DependencyTag extends AbstractTag
{
    /**
     * The dependency tag name.
     */
    public static final String KEY = "dependency";

    /**
     * The dependency tag key parameter name.
     */
    public static final String KEY_PARAM = "key";

    /**
     * The dependency tag optional parameter name.
     */
    public static final String OPTIONAL_PARAM = "optional";

    /**
     * The default component manager class.
     */
    protected static final String COMPONENT_MANAGER_CLASS =
            "org.apache.avalon.framework.component.ComponentManager";

    /**
     * The default service manager class.
     */
    protected static final String SERVICE_MANAGER_CLASS =
            "org.apache.avalon.framework.service.ServiceManager";

    private JavaMethod[] m_methods;

    /**
     * Class constructor.
     * @param clazz the javadoc class descriptor
     */
    public DependencyTag( final JavaClass clazz )
    {
        super( clazz );
        setMethods();
    }

    /**
     * Return the array of dependency descriptors based on the set of
     * 'dependency' tags associated with the components compose or service method.
     * @return the set of dependencies
     */
    public DependencyDescriptor[] getDependencies()
    {
        final ArrayList deps = new ArrayList();
        final Set marked = new HashSet( 10 );
        for ( int j = 0; j < m_methods.length; j++ )
        {
            final DocletTag[] tags =
                    m_methods[j].getTagsByName( getNS()
                    + Tags.DELIMITER + KEY );

            for ( int i = 0; i < tags.length; i++ )
            {
                DocletTag tag = tags[i];
                DependencyDescriptor dep = getDependency( tag );
                final String key = dep.getKey();
                if ( !marked.contains( key ) )
                {
                    deps.add( dep );
                    marked.add( key );
                }
            }
        }
        return (DependencyDescriptor[]) deps.toArray(
                new DependencyDescriptor[deps.size()] );
    }

    private DependencyDescriptor getDependency( DocletTag tag )
    {
        final String value = getNamedParameter( tag, TYPE_PARAM );
        final String type = resolveType( value );
        final String versionString = getNamedParameter( tag, VERSION_PARAM, null );
        final Version version = resolveVersion( versionString, value );
        final String key = getNamedParameter( tag, KEY_PARAM, type );
        final String optional = getNamedParameter( tag, OPTIONAL_PARAM, "false" );
        final boolean isOptional = "true".equals( optional.toLowerCase() );
        final ReferenceDescriptor ref = new ReferenceDescriptor( type, version );
        return new DependencyDescriptor( key, ref, isOptional, null );
    }

    /**
     * Set the value of the composition method.
     */
    private void setMethods()
    {
        m_methods = getLifecycleMethods( "compose", COMPONENT_MANAGER_CLASS );
        if ( m_methods.length == 0 )
        {
            m_methods = getLifecycleMethods( "service", SERVICE_MANAGER_CLASS );
        }
    }
}
