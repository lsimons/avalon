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


import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.Type;

import org.apache.avalon.framework.Version;

/**
 * A doclet tag representing the name of the Type.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 08:16:14 $
 */
public class AbstractTag
{
   /**
    * The dependency tag type parameter name.
    */
    public static final String TYPE_PARAM = "type";

   /**
    * The dependency tag version parameter name.
    */
    public static final String VERSION_PARAM = "version";


    private JavaClass m_class;

   /**
    * Class constructor.
    * @param clazz the javadoc class descriptor
    */
    public AbstractTag( final JavaClass clazz )
    {
        m_class = clazz;
    }

   /**
    * Return the javadoc class descriptor.
    * @return the javadoc class descriptor
    */
    protected JavaClass getJavaClass()
    {
        return m_class;
    }

   /**
    * Return the user defined namespace for avalon tags. The value returned is 
    * established by the javadoc tag 'avalon.namespace [namespace-value]' where
    * [namespace-value] is a string representing the namespace identifier.
    * @return the namespace tag used to represent the avalon.meta tag space
    * @exception IllegalArgumentException if the namespace tag is declared by does not
    *   contain a value
    */
    public String getNS() throws IllegalArgumentException
    {
        return Tags.NAMESPACE;
    }

   /**
    * Return the user defined namespace for avalon tags including the
    * standard namespace delimiter.  The value returned is established
    * by the javadoc tag 'avalon.namespace [namespace-value]' where
    * [namespace-value] is a string representing the namespace identifier
    * with the namespace delimiter appended.
    * @return the namespace tag with delimeter
    * @exception IllegalArgumentException if the namespace tag is declared by does not
    *   contain a value
    */
    public String getNSD() throws IllegalArgumentException
    {
        return getNS() + Tags.DELIMITER;
    }

    /**
     * Retrieve specified named parameter from tag. If the parameter
     * does not exist then return specified default value.
     *
     * @param tag the tag
     * @param name the name of parameter
     * @param defaultValue the default value
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
                "Malformed tag '" + tag.getName() + "'. "
                + "Missing required parameter '" + name + "'";
            throw new IllegalArgumentException( message );
        }
        return value;
    }

    /**
     * Resolve a version form the supplied string.
     *
     * @param version the explicit version
     * @param type the unresolved type in the classname:version format
     * @return the version or null if no version specified
     */
    protected Version resolveVersion( final String version, final String type )
    {
        if( version != null )
        {
            return Version.getVersion( version );
        }
        if( type.indexOf(":") > -1 )
        {
            return Version.getVersion( type.substring( type.indexOf(":") + 1 ) );
        }
        return null;
    }

    /**
     * Resolve a version form the supplied string.
     *
     * @param type the unresolved type
     * @return the version or null if no version specified
     */
    protected Version resolveVersion( final String type )
    {
        if( type.indexOf(":") > -1 )
        {
            return Version.getVersion( type.substring( type.indexOf(":") + 1 ) );
        }
        return null;
    }

    /**
     * Resolve the specified type.
     * Resolving essentially means finding the fully qualified name of
     * a class from just it's short name.
     *
     * @param type the unresolved type classname
     * @return the resolved type classname
     */
    protected String resolveType( final String type )
    {
        final String resolvedType;
        if( type.indexOf(":") > -1 )
        {
            resolvedType = 
              this.doResolveType( getJavaClass(), 
              type.substring( 0, type.indexOf(":") ) );
        }    
        else
        {
            resolvedType = doResolveType( getJavaClass(), type );
        }
        if( resolvedType == null )
        {
            final String message = 
              "Unable to find type " + type
              + " in class " + getJavaClass().getFullyQualifiedName();
            throw new RuntimeException( message );   
        }
        else 
        {
            return resolvedType;
        }
    }

    private String doResolveType( final JavaClass clazz, final String type )
    {
        final String resolvedType = clazz.getParentSource().resolveType( type );
        if( resolvedType != null )
        {
            return resolvedType;
        }
        else if( clazz.getSuperJavaClass() == null 
          || JavaClass.OBJECT.equals( clazz.getSuperClass() ) )
        {
            return null;
        }
        else 
        {
            return doResolveType( clazz.getSuperJavaClass(), type );
        }
    }

    /**
     * Retrieves all methods in the inheritance graph with specified name and 
     * one parameter of specified type. The methods must also return void.
     *
     * @param methodName the name of the methods
     * @param parameterType the class name of parameter
     * @return an array of such methods
     */
    protected JavaMethod[] getLifecycleMethods( final String methodName,
						    final String parameterType )
    {
	List result = new ArrayList();
	findLifecycleMethod( result, getJavaClass(), methodName, parameterType );
	return (JavaMethod[]) result.toArray( new JavaMethod[ result.size() ] );
    }

    private void findLifecycleMethod( 
            final List result,
            final JavaClass clazz,
            final String methodName,
            final String parameterType )
    {
        final JavaMethod[] methods = clazz.getMethods();
        for( int i = 0; i < methods.length; i++ )
        {
            final JavaMethod method = methods[ i ];
            if( methodName.equals( method.getName() )
                && method.getReturns().equals( new Type( "void", 0 ) )
                && method.getParameters().length == 1
                && method.getParameters()[ 0 ].getType().getValue().equals( parameterType ) )
            {
                result.add( method );
		    break;		
            }
        }

        if( 
            clazz.getSuperJavaClass() != null 
            && !JavaClass.OBJECT.equals( clazz.getSuperClass() ) )
        {
            this.findLifecycleMethod( 
              result, clazz.getSuperJavaClass(), methodName, parameterType );
        }
    }

    JavaMethod[] findTaggedMethods( final JavaClass clazz, String key )
    {
        ArrayList list = new ArrayList();
        return findTaggedMethods( clazz, key, list );
    }

    private JavaMethod[] findTaggedMethods( final JavaClass clazz, String key, List list )
    {
        final JavaMethod[] methods = clazz.getMethods();
        for( int i=0; i<methods.length; i++ )
        {
            JavaMethod method = methods[i];
            final DocletTag tag = 
              method.getTagByName( key );
            if( tag != null ) list.add( method );
        }

        if( 
            clazz.getSuperJavaClass() != null 
            && !JavaClass.OBJECT.equals( clazz.getSuperClass() ) )
        {
            return this.findTaggedMethods( clazz.getSuperJavaClass(), key, list );
        }

        return (JavaMethod[]) list.toArray( new JavaMethod[0] );

    }
}
