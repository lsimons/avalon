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

package org.apache.avalon.phoenix.framework.tools.qdox;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.Type;
import org.apache.avalon.phoenix.framework.info.ContextDescriptor;

/**
 * This is an abstract base class that is used to build a ComponentInfo object
 * from QDoxs JavaClass object model. Subclasses interpret different dialects
 * of javadocs markup.
 *
 * @author Peter Donald
 * @version $Revision: 1.4 $ $Date: 2003/12/05 15:14:38 $
 */
class AbstractInfoBuilder
{
    protected static final String LOGGER_CLASS =
        "org.apache.avalon.framework.logger.Logger";
    protected static final String CONTEXT_CLASS = ContextDescriptor.DEFAULT_TYPE;
    protected static final String COMPONENT_MANAGER_CLASS =
        "org.apache.avalon.framework.component.ComponentManager";
    protected static final String SERVICE_MANAGER_CLASS =
        "org.apache.avalon.framework.service.ServiceManager";
    protected static final String CONFIGURATION_CLASS =
        "org.apache.avalon.framework.configuration.Configuration";
    protected static final String PARAMETERS_CLASS =
        "org.apache.avalon.framework.parameters.Parameters";

    /**
     * Resolve the specified type.
     * Resolving essentially means finding the fully qualified name of
     * a class from just it's short name.
     *
     * @param javaClass the java class relative to which the type must be resolved
     * @param type the unresolved type
     * @return the resolved type
     */
    protected String resolveType( final JavaClass javaClass,
                                  final String type )
    {
        return javaClass.getParentSource().resolveType( type );
    }

    /**
     * Retrieve a method with specified name and one parameter of specified
     * type. The method must also return void.
     *
     * @param javaClass the java class to retrieve method for
     * @param methodName the name of the method
     * @param parameterType the class name of parameter
     * @return the method if such a method exists
     */
    protected JavaMethod getLifecycleMethod( final JavaClass javaClass,
                                             final String methodName,
                                             final String parameterType )
    {
        final JavaMethod[] methods = javaClass.getMethods();
        for( int i = 0; i < methods.length; i++ )
        {
            final JavaMethod method = methods[ i ];
            if( methodName.equals( method.getName() ) &&
                method.getReturns().equals( new Type( "void", 0 ) ) &&
                method.getParameters().length == 1 &&
                method.getParameters()[ 0 ].getType().getValue().equals( parameterType ) )
            {
                return method;
            }
        }
        return null;
    }

    /**
     * Retrieve specified named parameter from tag. If the parameter
     * does not exist then return specified default value.
     *
     * @param tag the tag
     * @param name the name of parameter
     * @return the value of named parameter
     */
    protected String getNamedParameter( final DocletTag tag,
                                        final String name,
                                        final String defaultValue )
    {
        String value = tag.getNamedParameter( name );
        if( null == value )
        {
            return defaultValue;
        }
        value = value.trim();
        if( value.startsWith( "\"" ) || value.startsWith( "'" ) )
        {
            value = value.substring( 1 );
        }
        if( value.endsWith( "\"" ) || value.endsWith( "'" ) )
        {
            value = value.substring( 0, value.length() - 1 );
        }
        return value;
    }

    /**
     * Retrieve specified named parameter from tag. If the parameter
     * does not exist then throw an exception.
     *
     * @param tag the tag
     * @param name the name of parameter
     * @return the value of named parameter
     */
    protected String getNamedParameter( final DocletTag tag, final String name )
    {
        final String value = getNamedParameter( tag, name, null );
        if( null == value )
        {
            final String message =
                "Malformed tag '" + tag.getName() + "'. " +
                "Missing required parameter '" + name + "'";
            throw new IllegalArgumentException( message );
        }
        return value;
    }
}
