/* 
 * Copyright 2002-2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.excalibur.source;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;

/**
 * This class holds parameters for a <code>Source</code> object.
 * It differs from the usual Parameters object because it can hold
 * more than one value for a parameter, as is the case for HTTP
 * request parameters.
 * <p>
 * Only particular kinds of <code>Source</code> implementations, such as
 * {@link org.apache.excalibur.source.impl.URLSource} support this kind of
 * parameters, passed as the {@link SourceResolver#URI_PARAMETERS} entry
 * in the <code>parameters</code> argument of
 * {@link SourceResolver#resolveURI(String, String, Map)}.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version $Id: SourceParameters.java,v 1.2 2004/02/19 08:36:15 cziegeler Exp $
 */
public final class SourceParameters
    implements Serializable, Cloneable
{
    /** The parameter names are the keys and the value is a List object */
    private Map names = new HashMap( 5 );

    /**
     * Decode the string
     */
    private String parseName( String s )
    {
        StringBuffer sb = new StringBuffer();
        for( int i = 0; i < s.length(); i++ )
        {
            char c = s.charAt( i );
            switch( c )
            {
                case '+':
                    sb.append( ' ' );
                    break;
                case '%':
                    try
                    {
                        sb.append( (char)Integer.parseInt( s.substring( i + 1, i + 3 ),
                                                           16 ) );
                        i += 2;
                    }
                    catch( NumberFormatException e )
                    {
                        throw new IllegalArgumentException();
                    }
                    catch( StringIndexOutOfBoundsException e )
                    {
                        String rest = s.substring( i );
                        sb.append( rest );
                        if( rest.length() == 2 )
                            i++;
                    }

                    break;
                default:
                    sb.append( c );
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * Create a new parameters object from the
     * children of the configuration.
     * If no children are available <code>null</code>
     * is returned.
     */
    public static SourceParameters create( Configuration conf )
    {
        Configuration[] children = conf.getChildren();
        if( children != null && children.length > 0 )
        {
            SourceParameters pars = new SourceParameters();
            String name;
            String value;
            for( int i = 0; i < children.length; i++ )
            {
                name = children[ i ].getName();
                try
                {
                    value = children[ i ].getValue();
                }
                catch( ConfigurationException local )
                {
                    value = ""; // ignore exception
                }
                pars.setParameter( name, value );
            }
            return pars;
        }
        return null;
    }

    /**
     * Standard Constructor creating an empty parameters object
     */
    public SourceParameters()
    {
    }

    /**
     * Construct a new object from a queryString
     */
    public SourceParameters( String queryString )
    {
        if( queryString != null )
        {
            StringTokenizer st = new StringTokenizer( queryString, "&" );
            while( st.hasMoreTokens() )
            {
                String pair = st.nextToken();
                int pos = pair.indexOf( '=' );
                if( pos != -1 )
                {
                    setParameter( parseName( pair.substring( 0, pos ) ),
                                  parseName( pair.substring( pos + 1, pair.length() ) ) );
                }
            }
        }
    }

    /**
     * Add a parameter.
     * The parameter is added with the given value.
     * @param name   The name of the parameter.
     * @param value  The value of the parameter.
     */
    public void setParameter( String name, String value )
    {
        ArrayList list;
        if( names.containsKey( name ) == true )
        {
            list = (ArrayList)names.get( name );
        }
        else
        {
            list = new ArrayList( 3 );
            names.put( name, list );
        }
        list.add( value );
    }

    /**
     * Get the value of a parameter.
     * @param name   The name of the parameter.
     * @return       The value of the first parameter with the name
     *               or <CODE>null</CODE>
     */
    public String getParameter( String name )
    {
        if( names.containsKey( name ) == true )
        {
            return (String)( (ArrayList)names.get( name ) ).get( 0 );
        }
        return null;
    }

    /**
     * Get the value of a parameter.
     * @param name   The name of the parameter.
     * @param defaultValue The default value if the parameter does not exist.
     * @return       The value of the first parameter with the name
     *               or <CODE>defaultValue</CODE>
     */
    public String getParameter( String name, String defaultValue )
    {
        if( names.containsKey( name ) == true )
        {
            return (String)( (ArrayList)names.get( name ) ).get( 0 );
        }
        return defaultValue;
    }

    /**
     * Get the integer value of a parameter.
     * @param name   The name of the parameter.
     * @param defaultValue The default value if the parameter does not exist.
     * @return       The value of the first parameter with the name
     *               or <CODE>defaultValue</CODE>
     */
    public int getParameterAsInteger( String name, int defaultValue )
    {
        if( names.containsKey( name ) == true )
        {
            return new Integer( (String)( (ArrayList)names.get( name ) ).get( 0 ) ).intValue();
        }
        return defaultValue;
    }

    /**
     * Get the boolean value of a parameter.
     * @param name   The name of the parameter.
     * @param defaultValue The default value if the parameter does not exist.
     * @return       The value of the first parameter with the name
     *               or <CODE>defaultValue</CODE>
     */
    public boolean getParameterAsBoolean( String name, boolean defaultValue )
    {
        if( names.containsKey( name ) == true )
        {
            return new Boolean( (String)( (ArrayList)names.get( name ) ).get( 0 ) ).booleanValue();
        }
        return defaultValue;
    }

    /**
     * Test if a value for this parameter exists.
     * @param name   The name of the parameter.
     * @return       <CODE>true</CODE> if a value exists, otherwise <CODE>false</CODE>
     */
    public boolean containsParameter( String name )
    {
        return names.containsKey( name );
    }

    /**
     * Get all values of a parameter.
     * @param name   The name of the parameter.
     * @return       Iterator for the (String) values or null if the parameter
     *               is not defined.
     */
    public Iterator getParameterValues( String name )
    {
        if( names.containsKey( name ) == true )
        {
            ArrayList list = (ArrayList)names.get( name );
            return list.iterator();
        }
        return null;
    }

    /**
     * Get all parameter names.
     * @return  Iterator for the (String) parameter names.
     */
    public Iterator getParameterNames()
    {
        return names.keySet().iterator();
    }

    /**
     * Create a Parameters object.
     * The first value of each parameter is added to the Parameters object.
     * @return An Parameters object - if no parameters are defined this is an
     *         empty object.
     */
    public Parameters getFirstParameters()
    {
        Parameters result = new Parameters();
        Iterator iter = this.getParameterNames();
        String parName;
        while( iter.hasNext() )
        {
            parName = (String)iter.next();
            result.setParameter( parName, this.getParameter( parName ) );
        }
        return result;
    }

    /**
     * Build a query string.
     * The query string can e.g. be used for http connections.
     * @return A query string which contains for each parameter/value pair
     *         a part, like "parameter=value" separated by "&".
     *         If no parameter is defined <CODE>null</CODE> is returned.
     */
    public String getQueryString()
    {
        StringBuffer result = new StringBuffer();
        Iterator iter = this.names.keySet().iterator();
        Iterator listIterator;
        String key;
        String value;
        boolean first = true;
        while( iter.hasNext() == true )
        {
            key = (String)iter.next();
            listIterator = ( (ArrayList)names.get( key ) ).iterator();
            while( listIterator.hasNext() == true )
            {
                if( first == false ) result.append( '&' );
                value = (String)listIterator.next();
                result.append( key ).append( '=' ).append( value );
                first = false;
            }
        }
        return ( result.length() == 0 ? null : result.toString() );
    }

    /**
     * Build a query string and encode each parameter value.
     * The query string can e.g. be used for http connections.
     * @return A query string which contains for each parameter/value pair
     *         a part, like "parameter=value" separated by "&".
     *         If no parameter is defined <CODE>null</CODE> is returned.
     */
    public String getEncodedQueryString()
    {
        StringBuffer result = new StringBuffer();
        Iterator iter = this.names.keySet().iterator();
        Iterator listIterator;
        String key;
        String value;
        boolean first = true;
        while( iter.hasNext() == true )
        {
            key = (String)iter.next();
            listIterator = ( (ArrayList)names.get( key ) ).iterator();
            while( listIterator.hasNext() == true )
            {
                if( first == false ) result.append( '&' );
                value = (String)listIterator.next();
                result.append( key ).append( '=' ).append( SourceUtil.encode( value ) );
                first = false;
            }
        }
        return ( result.length() == 0 ? null : result.toString() );
    }

    /**
     * Add all parameters from the incoming parameters object.
     */
    public void add( SourceParameters parameters )
    {
        if( null != parameters )
        {
            Iterator names = parameters.getParameterNames();
            Iterator values;
            String name;
            String value;
            while( names.hasNext() == true )
            {
                name = (String)names.next();
                values = parameters.getParameterValues( name );
                while( values.hasNext() == true )
                {
                    value = (String)values.next();
                    this.setParameter( name, value );
                }
            }
        }
    }

    /**
     * Overriding toString
     */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer( "SourceParameters: {" );
        Iterator names = this.getParameterNames();
        String name;
        boolean firstName = true;
        Iterator values;
        String value;
        boolean firstValue;
        while( names.hasNext() == true )
        {
            name = (String)names.next();
            if( firstName == false )
            {
                buffer.append( ", " );
            }
            else
            {
                firstName = false;
            }
            buffer.append( name ).append( " = (" );
            values = this.getParameterValues( name );
            firstValue = true;
            while( values.hasNext() == true )
            {
                value = (String)values.next();
                if( firstValue == false )
                {
                    buffer.append( ", " );
                }
                else
                {
                    firstValue = false;
                }
                buffer.append( value );
            }
            buffer.append( ')' );
        }
        buffer.append( '}' );
        return buffer.toString();
    }

    /**
     * Returns a copy of the parameters object.
     */
    public Object clone()
    {
        SourceParameters newObject = new SourceParameters();
        Iterator names = this.getParameterNames();
        Iterator values;
        String name, value;
        while( names.hasNext() )
        {
            name = (String)names.next();
            values = this.getParameterValues( name );
            while( values.hasNext() )
            {
                value = (String)values.next();
                newObject.setParameter( name, value );
            }
        }
        return newObject;
    }

    /**
     * Test if there are any parameters.
     */
    public boolean hasParameters()
    {
        return ( this.names.size() > 0 );
    }

    /**
     * Set the value of this parameter to the given value.
     * Remove all other values for this parameter.
     */
    public void setSingleParameterValue( String name, String value )
    {
        this.removeParameter( name );
        this.setParameter( name, value );
    }

    /**
     * Remove all values for this parameter
     */
    public void removeParameter( String name )
    {
        if( this.names.containsKey( name ) )
        {
            this.names.remove( name );
        }
    }
}
