/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.configuration;

import java.util.Iterator;

/**
 * This is an abstract <code>Configuration</code> implementation that deals
 * with methods that can be abstracted away from underlying implementations.
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mailto:fumagalli@exoffice.com">Pierpaolo Fumagalli</a>
 * @version CVS $Revision: 1.1 $ $Date: 2001/04/26 14:16:25 $
 */
public abstract class AbstractConfiguration
    implements Configuration
{
    /**
     * Returns the value of the configuration element as an <code>int</code>.
     */
    public int getValueAsInteger()
        throws ConfigurationException
    {
        final String value = getValue();
        try
        {
            if( value.startsWith( "0x" ) )
            {
                return Integer.parseInt( value.substring( 2 ), 16 );
            }
            else if( value.startsWith( "0o" ) )
            {
                return Integer.parseInt( value.substring( 2 ), 8 );
            }
            else if( value.startsWith( "0b" ) )
            {
                return Integer.parseInt( value.substring( 2 ), 2 );
            }
            else
            {
                return Integer.parseInt( value );
            }
        }
        catch( final Exception nfe )
        {
            throw
                new ConfigurationException( "Cannot parse the value of the configuration " +
                                            "element \"" + getName() + "\" as an integer" );
        }
    }

    /**
     * Returns the value of the configuration element as an <code>int</code>.
     */
    public int getValueAsInteger( final int defaultValue )
    {
        try
        {
            return getValueAsInteger();
        }
        catch( final ConfigurationException ce )
        {
            return defaultValue;
        }
    }

    /**
     * Returns the value of the configuration element as a <code>long</code>.
     */
    public long getValueAsLong()
        throws ConfigurationException
    {
        final String value = getValue();
        try
        {
            if( value.startsWith( "0x" ) )
            {
                return Long.parseLong( value.substring( 2 ), 16 );
            }
            else if( value.startsWith( "0o" ) )
            {
                return Long.parseLong( value.substring( 2 ), 8 );
            }
            else if( value.startsWith( "0b" ) )
            {
                return Long.parseLong( value.substring( 2 ), 2 );
            }
            else return Integer.parseInt(value);
        }
        catch( final Exception nfe )
        {
            throw new ConfigurationException( "Cannot parse the value of the " +
                                              "configuration element \"" + getName() +
                                              "\" as a long" );
        }
    }

    /**
     * Returns the value of the configuration element as a <code>long</code>.
     */
    public long getValueAsLong( final long defaultValue )
    {
        try
        {
            return getValueAsLong();
        }
        catch( final ConfigurationException ce )
        {
            return defaultValue;
        }
    }

    /**
     * Returns the value of the configuration element as a <code>float</code>.
     */
    public float getValueAsFloat()
        throws ConfigurationException
    {
        final String value = getValue();
        try
        {
            return Float.parseFloat( value );
        }
        catch( final Exception nfe )
        {
            throw new ConfigurationException( "Cannot parse the value of the " +
                                              "configuration element \"" + getName() +
                                              "\" as a float" );
        }
    }

    /**
     * Returns the value of the configuration element as a <code>float</code>.
     */
    public float getValueAsFloat( final float defaultValue )
    {
        try
        {
            return getValueAsFloat();
        }
        catch( final ConfigurationException ce )
        {
            return(defaultValue);
        }
    }

    /**
     * Returns the value of the configuration element as a <code>boolean</code>.
     */
    public boolean getValueAsBoolean()
        throws ConfigurationException
    {
        final String value = getValue();
        if( value.equals( "true" ) ) return true;
        else if( value.equals( "false" ) ) return false;
        else
        {
            throw new ConfigurationException( "Cannot parse the value of the " +
                                              "configuration element \"" +
                                              getName() + "\" as a boolean" );
        }
    }

    /**
     * Returns the value of the configuration element as a <code>boolean</code>.
     */
    public boolean getValueAsBoolean( final boolean defaultValue )
    {
        try
        {
            return getValueAsBoolean();
        }
        catch( final ConfigurationException ce )
        {
            return defaultValue;
        }
    }

    /**
     * Returns the value of the configuration element as a <code>String</code>.
     */
    public String getValue( final String defaultValue )
    {
        try
        {
            return getValue();
        }
        catch( final ConfigurationException ce )
        {
            return defaultValue;
        }
    }

    /**
     * Returns the value of the attribute specified by its name as an
     * <code>int</code>.
     */
    public int getAttributeAsInteger( final String name )
        throws ConfigurationException
    {
        final String value = getAttribute( name );
        try
        {
            if( value.startsWith( "0x" ) )
            {
                return Integer.parseInt( value.substring( 2 ), 16 );
            }
            else if( value.startsWith( "0o" ) )
            {
                return Integer.parseInt( value.substring( 2 ), 8);
            }
            else if( value.startsWith( "0b" ) )
            {
                return Integer.parseInt( value.substring( 2 ), 2 );
            }
            else
            {
                return Integer.parseInt(value);
            }
        }
        catch( final Exception nfe )
        {
            throw new ConfigurationException( "Cannot parse the value of the attribute \"" +
                                              name + "\" of the configuration element \"" +
                                              getName() + "\" as an integer" );
        }
    }

    /**
     * Returns the value of the attribute specified by its name as an
     * <code>int</code>.
     */
    public int getAttributeAsInteger( final String name, final int defaultValue )
    {
        try
        {
            return getAttributeAsInteger( name );
        }
        catch( final ConfigurationException ce )
        {
            return defaultValue;
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>long</code>.
     */
    public long getAttributeAsLong( final String name )
        throws ConfigurationException
    {
        final String value = getAttribute( name );

        try
        {
            if( value.startsWith( "0x" ) )
            {
                return Long.parseLong( value.substring( 2 ), 16 );
            }
            else if( value.startsWith( "0o" ) )
            {
                return Long.parseLong( value.substring( 2 ), 8 );
            }
            else if( value.startsWith( "0b" ) )
            {
                return Long.parseLong( value.substring( 2 ), 2);
            }
            else
            {
                return Integer.parseInt( value );
            }
        }
        catch( final Exception nfe )
        {
            throw new ConfigurationException( "Cannot parse the value of the attribute \"" +
                                              name + "\" of the configuration element \"" +
                                              getName() + "\" as a long" );
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>long</code>.
     */
    public long getAttributeAsLong( final String name, final long defaultValue )
    {
        try
        {
            return getAttributeAsLong( name );
        }
        catch( final ConfigurationException ce )
        {
            return defaultValue;
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>float</code>.
     */
    public float getAttributeAsFloat( final String name )
        throws ConfigurationException
    {
        final String value = getAttribute( name );
        try
        {
            return Float.parseFloat( value );
        }
        catch( final Exception e )
        {
            throw new ConfigurationException( "Cannot parse the value of the attribute \"" +
                                              name + "\" of the configuration element \"" +
                                              getName() + "\" as a float" );
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>float</code>.
     */
    public float getAttributeAsFloat( final String name, final float defaultValue )
    {
        try
        {
            return getAttributeAsFloat( name );
        }
        catch( final ConfigurationException ce )
        {
            return defaultValue;
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>boolean</code>.
     */
    public boolean getAttributeAsBoolean( final String name )
        throws ConfigurationException
    {
        final String value = getAttribute( name );

        if( value.equals( "true" ) ) return true;
        else if( value.equals( "false" ) ) return false;
        else
        {
            throw new ConfigurationException( "Cannot parse the value of the attribute \"" +
                                              name + "\" of the configuration element \"" +
                                              getName() + "\" as a boolean" );
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>boolean</code>.
     */
    public boolean getAttributeAsBoolean( final String name, final boolean defaultValue )
    {
        try
        {
            return getAttributeAsBoolean( name );
        }
        catch( final ConfigurationException ce )
        {
            return defaultValue;
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>String</code>.
     */
    public String getAttribute( final String name, final String defaultValue )
    {
        try
        {
            return getAttribute( name );
        }
        catch( final ConfigurationException ce )
        {
            return defaultValue;
        }
    }

    /**
     * Return the first <code>Configuration</code> object child of this
     * associated with the given name.
     */
    public Configuration getChild( final String name )
    {
        return getChild( name, true );
    }

    /**
     * Return the first <code>Configuration</code> object child of this
     * associated with the given name.
     */
    public Configuration getChild( final String name, final boolean createNew )
    {
        final Configuration[] children = getChildren( name );
        if( children.length > 0 )
        {
            return children[ 0 ];
        }
        else
        {
            if( createNew )
            {
                return new DefaultConfiguration( name, "-" );
            }
            else
            {
                return null;
            }
        }
    }
}
