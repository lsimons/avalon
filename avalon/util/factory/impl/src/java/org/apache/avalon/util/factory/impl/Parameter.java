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

package org.apache.avalon.util.factory.impl;

import java.lang.reflect.Constructor;
import org.apache.avalon.util.factory.FactoryException;

/**
 * A parameter is an immutable class that contains a description 
 * of an allowable parameter within a crieria instance.
 * 
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.2 $
 */
public class Parameter
{
    //--------------------------------------------------------------
    // static
    //--------------------------------------------------------------

   /**
    * Return the set of keys corresponding to the supplied set of 
    * parameters.
    * @param params the parameter sequence
    * @return the corresponding keys
    */
    public static String[] getKeys( Parameter[] params )
    {
        String[] keys = new String[ params.length ];
        for( int i=0; i<params.length; i++ )
        {
            keys[i] = params[i].getKey();
        }
        return keys;
    }

    //--------------------------------------------------------------
    // immutable state
    //--------------------------------------------------------------

    private final String m_key;
    private final Class m_type;
    private final boolean m_required;
    private final Object m_default;

    //--------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------

    /**
     * Creation of a new required parameter constraint.
     * @param key the parameter key
     * @param type the name of a class constraining assigned values
     */
    public Parameter( 
      final String key, final Class type ) 
    {
        m_key = key;
        m_type = type;
        m_required = true;
        m_default = null;
    }

    /**
     * Creation of a new optional parameter constraint.
     * @param key the parameter key
     * @param type the name of a class constraining assigned values
     * @param value the default value
     */
    public Parameter( 
      final String key, final Class type, Object value ) 
    {
        m_key = key;
        m_type = type;
        m_required = false;
        m_default = value;
    }

    //--------------------------------------------------------------
    // implementation
    //--------------------------------------------------------------

   /**
    * Return the key for the parameter.
    * @return the key
    */
    public String getKey()
    {
        return m_key;
    }

   /**
    * Return the classname for the parameter.
    * @return the classname
    */
    public Class getParameterClass()
    {
        return m_type;
    }

   /**
    * Return TRUE is the parameter is required.
    * @return the required status of the parameter
    */
    public boolean isRequired()
    {
        return m_required;
    }

   /**
    * Return TRUE is the parameter is optional.
    * @return the optional status of the parameter
    */
    public boolean isOptional()
    {
        return !isRequired();
    }

   /**
    * Return the default value for this parameter.
    * @return the default value
    */
    public Object getDefault()
    {
        return m_default;
    }

   /**
    * Resolve a supplied argument to a value.
    * @param value the supplied argument
    * @return the resolved object
    * @exception Exception if an error occurs
    */
    public Object resolve( Object value ) throws FactoryException
    {
        if( value == null ) return null;
        if( m_type.isInstance( value ) )
        {
            return value;
        }
        else
        { 
            Constructor constructor = null;
            try
            {
                constructor = 
                  m_type.getConstructor( 
                    new Class[]{ value.getClass() } );
            }
            catch( NoSuchMethodException nsme )
            {
                final String error =
                  "Value of class: [" 
                  + value.getClass().getName() 
                  + "] supplied for key [" 
                  + getKey() 
                  + "] is not an instance of type: [" 
                  + m_type.getName()
                  + "].";
                throw new IllegalArgumentException( error );
            }

            try
            {
                return constructor.newInstance( 
                  new Object[]{ value } );
            }
            catch( Throwable e )
            {
                final String error =
                  "Value of class: [" 
                  + value.getClass().getName() 
                  + "] supplied for key [" 
                  + getKey() 
                  + "] is not an instance of or was not resolvable to the type: [" 
                  + m_type.getName()
                  + "].";
                throw new FactoryException( error, e );
            }
        }
    }
}
