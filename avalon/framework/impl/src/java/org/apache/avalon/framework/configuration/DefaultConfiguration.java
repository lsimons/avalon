/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the default <code>Configuration</code> implementation.
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:fumagalli@exoffice.com">Pierpaolo Fumagalli</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class DefaultConfiguration
    extends AbstractConfiguration
    implements Serializable
{
    protected static final Configuration[]   EMPTY_ARRAY = new Configuration[ 0 ];

    private final String                     m_name;
    private final String                     m_location;
    private final String                     m_namespace;
    private final String                     m_prefix;
    private HashMap                          m_attributes;
    private ArrayList                        m_children;
    private String                           m_value;
    private boolean                          m_readOnly;

    /**
     * Create a new <code>DefaultConfiguration</code> instance.
     */
    public DefaultConfiguration( final String name, final String location )
    {
        this (name, location, "", "");
    }

    /**
     * Create a new <code>DefaultConfiguration</code> instance.
     */
    public DefaultConfiguration( final String name, 
                                 final String location, 
                                 final String ns, 
                                 final String prefix )
    {
        m_name = name;
        m_location = location;
        m_namespace = ns;
        m_prefix = prefix;  // only used as a serialization hint
    }

    /**
     * Returns the name of this configuration element.
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Returns the namespace of this configuration element
     */
    public String getNamespace()
    {
        return m_namespace;
    }

    /**
     * Returns the prefix of the namespace
     */
    protected String getPrefix()
    {
        return m_prefix;
    }

    /**
     * Returns a description of location of element.
     */
    public String getLocation()
    {
        return m_location;
    }

    /**
     * Returns the value of the configuration element as a <code>String</code>.
     *
     * @exception ConfigurationException If the value is not present.
     */
    public String getValue() throws ConfigurationException
    {
        if( null != m_value )
        {
            return m_value;
        }
        else
        {
            throw new ConfigurationException( "No value is associated with the "+
                                              "configuration element \"" + getName() +
                                              "\" at " + getLocation() );
        }
    }

    /**
     * Return an array of all attribute names.
     */
    public String[] getAttributeNames()
    {
        if( null == m_attributes )
        {
            return new String[ 0 ];
        }
        else
        {
            return (String[])m_attributes.keySet().toArray( new String[ 0 ] );
        }
    }

    /**
     * Return an <code>Iterator</code> of <code>Configuration<code>
     * elements containing all node children.
     *
     * @return The child nodes with name
     */
    public Configuration[] getChildren()
    {
        if( null == m_children )
        {
            return new Configuration[ 0 ];
        }
        else
        {
            return (Configuration[])m_children.toArray( new Configuration[ 0 ] );
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>String</code>.
     *
     * @exception ConfigurationException If the attribute is not present.
     */
    public String getAttribute( final String name )
        throws ConfigurationException
    {
        final String value =
            (null != m_attributes) ? (String)m_attributes.get( name ) : null;

        if( null != value )
        {
            return value;
        }
        else
        {
            throw new ConfigurationException( "No attribute named \"" + name + "\" is " +
                                              "associated with the configuration element \"" +
                                              getName() + "\" at " + getLocation() );
        }
    }

    /**
     * Return the first <code>Configuration</code> object child of this
     * associated with the given name.
     */
    public Configuration getChild( final String name, final boolean createNew )
    {
        if( null != m_children )
        {
            final int size = m_children.size();
            for( int i = 0; i < size; i++ )
            {
                final Configuration configuration = (Configuration)m_children.get( i );
                if( name.equals( configuration.getName() ) )
                {
                    return configuration;
                }
            }
        }

        if( createNew )
        {
            return new DefaultConfiguration( name, "-" );
        }
        else
        {
            return null;
        }
    }

    /**
     * Return an <code>Enumeration</code> of <code>Configuration</code> objects
     * children of this associated with the given name.
     * <br>
     * The returned <code>Enumeration</code> may be empty.
     *
     * @param name The name of the required children <code>Configuration</code>.
     */
    public Configuration[] getChildren( final String name )
    {
        if( null == m_children )
        {
            return new Configuration[ 0 ];
        }
        else
        {
            final ArrayList children = new ArrayList();
            final int size = m_children.size();

            for( int i = 0; i < size; i++ )
            {
                final Configuration configuration = (Configuration)m_children.get( i );
                if( name.equals( configuration.getName() ) )
                {
                    children.add( configuration );
                }
            }

            return (Configuration[])children.toArray( new Configuration[ 0 ] );
        }
    }

    /**
     * Append data to the value of this configuration element.
     *
     * @deprecated Use setValue() instead
     */
    public void appendValueData( final String value )
    {
        checkWriteable();

        if( null == m_value )
        {
            m_value = value;
        }
        else
        {
            m_value += value;
        }
    }

    public void setValue( final String value )
    {
        checkWriteable();

        m_value = value;
    }

    public void setAttribute( final String name, final String value )
    {
        checkWriteable();

        if( null == m_attributes )
        {
            m_attributes = new HashMap();
        }
        m_attributes.put( name, value );
    }

    /**
     * Add an attribute to this configuration element, returning its old
     * value or <b>null</b>.
     *
     * @deprecated Use setAttribute() instead
     */
    public String addAttribute( final String name, String value )
    {
        checkWriteable();

        if( null == m_attributes )
        {
            m_attributes = new HashMap();
        }

        return (String) m_attributes.put( name, value );
    }

    /**
     * Add a child <code>Configuration</code> to this configuration element.
     */
    public void addChild( final Configuration configuration )
    {
        checkWriteable();

        if( null == m_children )
        {
            m_children = new ArrayList();
        }

        m_children.add( configuration );
    }

    /**
     * Remove a child <code>Configuration</code> to this configuration element.
     */
    public void removeChild( final Configuration configuration )
    {
        checkWriteable();

        if( null == m_children )
        {
            return;
        }
        m_children.remove( configuration );
    }

    /**
     * Return count of children.
     */
    public int getChildCount()
    {
        if( null == m_children )
        {
            return 0;
        }

        return m_children.size();
    }

    public void makeReadOnly()
    {
        m_readOnly = true;
    }

    protected final void checkWriteable()
        throws IllegalStateException
    {
        if( m_readOnly )
        {
            throw new IllegalStateException( "Configuration is read only and can not be modified" );
        }
    }
}
