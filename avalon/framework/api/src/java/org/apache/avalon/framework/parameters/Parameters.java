/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.parameters;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 *
 * @author <a href="mailto:fumagalli@exoffice.com">Pierpaolo Fumagalli</a>
 */
public class Parameters
{
    private HashMap            m_parameters = new HashMap();

    /**
     * Set the <code>String</code> value of a specified parameter.
     * <p />
     * If the specified value is <b>null</b> the parameter is removed.
     *
     * @return The previous value of the parameter or <b>null</b>.
     */
    public String setParameter( final String name, final String value )
    {
        if( null == name )
        {
            return null;
        }

        if( null == value )
        {
            return (String)m_parameters.remove( name );
        }

        return (String)m_parameters.put( name, value );
    }

    /**
     * Return an <code>Enumeration</code> view of all parameter names.
     */
    public Iterator getParameterNames()
    {
        return m_parameters.keySet().iterator();
    }

    /**
     * Check if the specified parameter can be retrieved.
     */
    public boolean isParameter( final String name )
    {
        return m_parameters.containsKey( name );
    }

    /**
     * Retrieve the <code>String</code> value of the specified parameter.
     * <p />
     * If the specified parameter cannot be found, <b>null</b> is returned.
     */
    protected String getParameter( final String name )
    {
        if( null == name )
        {
            return null;
        }

        return (String)m_parameters.get( name );
    }

    /**
     * Retrieve the <code>String</code> value of the specified parameter.
     * <p />
     * If the specified parameter cannot be found, <code>defaultValue</code>
     * is returned.
     */
    public String getParameter( final String name, final String defaultValue )
    {
        final String value = getParameter( name );

        if( null == value )
        {
            return defaultValue;
        }
        else
        {
            return value;
        }
    }

    /**
     * Retrieve the <code>int</code> value of the specified parameter.
     * <p />
     * If the specified parameter cannot be found, <code>defaultValue</code>
     * is returned.
     */
    public int getParameterAsInteger( final String name, final int defaultValue )
    {
        final String value = getParameter( name );

        if( null == value )
        {
            return defaultValue;
        }

        try
        {
            if( value.startsWith("0x") )
            {
                return Integer.parseInt( value.substring(2), 16 );
            }
            else if( value.startsWith("0o") )
            {
                return Integer.parseInt( value.substring(2), 8 );
            }
            else if( value.startsWith("0b") )
            {
                return Integer.parseInt( value.substring(2), 2 );
            }
            else
            {
                return Integer.parseInt( value );
            }
        }
        catch( final NumberFormatException nfe )
        {
            return defaultValue;
        }
    }

    /**
     * Retrieve the <code>long</code> value of the specified parameter.
     * <p />
     * If the specified parameter cannot be found, <code>defaultValue</code>
     * is returned.
     */
    public long getParameterAsLong( final String name, final long defaultValue )
    {
        final String value = getParameter( name );

        if( null == value )
        {
            return defaultValue;
        }

        try
        {
            if( value.startsWith("0x") )
            {
                return Long.parseLong( value.substring(2), 16 );
            }
            else if( value.startsWith("0o") )
            {
                return Long.parseLong( value.substring(2), 8 );
            }
            else if( value.startsWith("0b") )
            {
                return Long.parseLong( value.substring(2), 2 );
            }
            else
            {
                return Long.parseLong(value);
            }
        }
        catch( final NumberFormatException nfe )
        {
            return defaultValue;
        }
    }

    /**
     * Retrieve the <code>float</code> value of the specified parameter.
     * <p />
     * If the specified parameter cannot be found, <code>defaultValue</code>
     * is returned.
     */
    public float getParameterAsFloat( final String name, final float defaultValue )
    {
        final String value = getParameter( name );

        if( null == value )
        {
            return defaultValue;
        }

        try
        {
            return Float.parseFloat(value);
        }
        catch( final NumberFormatException nfe )
        {
            return defaultValue;
        }
    }

    /**
     * Retrieve the <code>boolean</code> value of the specified parameter.
     * <p />
     * If the specified parameter cannot be found, <code>defaultValue</code>
     * is returned.
     */
    public boolean getParameterAsBoolean( final String name, final boolean defaultValue )
    {
        final String value = getParameter( name );

        if( null == value )
        {
            return defaultValue;
        }

        if( value.equalsIgnoreCase("true") )
        {
            return true;
        }

        if( value.equalsIgnoreCase("false") )
        {
            return(false);
        }

        return defaultValue;
    }

    /**
     * Merge parameters from another <code>Parameters</code> instance
     * into this.
     *
     * @return This <code>Parameters</code> instance.
     */
    public Parameters merge( final Parameters other )
    {
        final Iterator names = other.getParameterNames();

        while( names.hasNext() )
        {
            final String name = (String) names.next();
            final String value = other.getParameter( name );

            setParameter( name, value );
        }

        return this;
    }

    /**
     * Create a <code>Parameters</code> object from a <code>Configuration</code>
     * object.
     */
    public static Parameters fromConfiguration( final Configuration configuration )
        throws ConfigurationException
    {
        if( null == configuration )
        {
            throw new ConfigurationException( "You cannot convert to parameters with " +
                                              "a null Configuration" );
        }

        final Configuration[] parameters = configuration.getChildren( "parameter" );
        final Parameters param = new Parameters();

        for( int i = 0; i <  parameters.length; i++ )
        {
            try
            {
                final String name = parameters[ i ].getAttribute( "name" );
                final String value = parameters[ i ].getAttribute( "value" );
                param.setParameter( name, value );
            }
            catch( final Exception e )
            {
                throw new ConfigurationException( "Cannot process Configurable", e );
            }
        }

        return param;
    }

    /**
     * Create a <code>Parameters</code> object from a <code>Properties</code>
     * object.
     */
    public static Parameters fromProperties( final Properties properties )
    {
        final Parameters parameters = new Parameters();

        final Enumeration enum = properties.propertyNames();

        while( enum.hasMoreElements() )
        {
            final String name = (String)enum.nextElement();
            final String value = properties.getProperty( name );
            parameters.setParameter( name, value );
        }

        return parameters;
    }
}
