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

package org.apache.avalon.util.criteria;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import java.net.URL;
import java.net.MalformedURLException;
import java.lang.reflect.Constructor;

/**
 * A abstract utility class that can be used to simplify the 
 * creation of domain specific criteria.
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.1 $
 */
public class Criteria extends HashMap
{
    //--------------------------------------------------------------
    // state
    //--------------------------------------------------------------

     private final Parameter[] m_params;

    //--------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------


   /**
    * Creation of a new criteria instance.
    * @param params the set of parameters managed by the criteria
    */
    public Criteria( final Parameter[] params )
    {
        if( null == params ) 
          throw new NullPointerException( "params" );
        m_params = params;
    }

    //--------------------------------------------------------------
    // Criteria
    //--------------------------------------------------------------

   /**
    * Set a named parameter of the criteria to a value.
    * @param key the parameter key
    * @param value the value to assign to the key
    * @return the original value
    * @exception CriteriaRuntimeException if the supplied value fails
    *    the validation test for its associated parameter
    */
    public Object put( final Object key, final Object value ) 
    {
        if( !(key instanceof String ))
        {
            final String error = 
              "Invalid key: " + key;
            throw new IllegalArgumentException( error );
        }

        Object current = super.get( key );

        if( null == value )
        {
            super.put( key, null );
            return current;
        }

        final Parameter p = getParameter( (String) key );

        try
        {
            final Object v = p.resolve( value );
            if( p.getParameterClass().isInstance( v ) )
            {
                super.put( key, v );
                return current;
            }
            else
            {
                final String error = 
                  "Resolved value: " + v 
                  + " does not implement the parameter type: "
                  + p.getParameterClass();
                throw new IllegalArgumentException( error );
            }
        }
        catch( Throwable e )
        {
            final String error = 
              "Unable to assign a value to the key: " + key;
            throw new CriteriaRuntimeException( error, e );
        }
    }

   /**
    * Return the currently assigned value for a key.
    * @return the assigned value
    */
    public Object get( final Object key )
    {
        Parameter param = getParameterFromObject( key );
        Object value = super.get( param.getKey() );
        if( null != value )
        {
            return value;
        }
        else
        {
            return param.getDefault();
        }
    }

    //--------------------------------------------------------------
    // private and protected
    //--------------------------------------------------------------

   /**
    * Return the currently assigned value for a key.
    * @return the assigned value
    */
    protected Object getValue( final Parameter param )
    {
        return get( param.getKey() );
    }

   /**
    * Return the parameter associated to the criteria.
    * @return the parameters
    */
    protected Parameter[] getParameters()
    {
        return m_params;
    }

    protected Parameter getParameter( final String key )
    {
        Parameter[] params = getParameters();
        for( int i=0; i<params.length; i++ )
        {
            Parameter parameter = params[i];
            if( parameter.getKey().equals( key ) ) return parameter;
        }

        final String error = 
          "Unknown key: [" + key + "].";
        throw new IllegalArgumentException( error );
    }

    private Parameter getParameterFromObject( Object key )
    {
        if( key instanceof Parameter )
        {
            return (Parameter) key;
        }
        else
        {
            return getParameter( key.toString() );
        }
    }
}
