/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.TXT file.
 */
package org.apache.avalon.phoenix.components.configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

/**
 * The MergedConfiguration is a classic Configuration backed by parent
 * Configuration.  Operations such as getChild return a MergedConfiguration
 * encapsulating both a primary and parent configuration.  Requests for attribute
 * values are resolved against the base configuration initially.  If the result
 * of the resolution is unsucessful, the request is applied against the parent
 * configuration.  As a parent may also be a MergedConfiguration, the evaluation
 * will be applied until a value is resolved against a class parent Configuration.
 *
 * The MergedConfiguration will use special attributes on the base configuration's
 * children to control how children of the parent and base are combined. In order for
 * a child of the base to be merged with a child of the parent, the following must hold true:
 * <ol>
 *   <li>There is only a single child of both the parent and child with a specified name
 *       <i>(TODO: enable a "key attribute" to support merging when multiple items with
 *          the same name exist</i>
 *   </li>
 *   <li>The child in the <b>base</b> Configuration has an attribute named
 *       <code>phoenix-configuration:merge</code> and its value is equal to a boolean
 *       <code>TRUE</code>
 *   </li>
 * </ol>
 *
 * When viewing the list of attributes for a MergedConfiguration, the
 * <code>phoenix-configuration:merge</code> is not included.
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
class MergedConfiguration implements Configuration
{
    private static final String MERGE_ATTR = "phoenix-configuration:merge";
    //TOOD: implement support for key attribute
    private static final String KEY_ATTR = "phoenix-configuration:key-attribute";

    //=============================================================================
    // state
    //=============================================================================

    /**
     * The primary configuration.
     */
    private final Configuration m_base;

    /**
     * The fallback configuration.
     */
    private final Configuration m_parent;

    //=============================================================================
    // constructors
    //=============================================================================

    /**
     * Create a MergedConfiguration with specified parent.  The base
     * configuration shall override a parent configuration on request for
     * attribute values and configuration body values.  Unresolved request
     * are redirected up the parent chain until a classic configuration is
     * reached.  Request for child configurations will return a
     * new MergedConfiguration referencing the child of the base and
     * the child of the primary (i.e. a child configuration chain).
     *
     * @param base the base Configuration
     * @param parent the parent Configuration
     */
    public MergedConfiguration( final Configuration base, final Configuration parent )
    {
        if( base == null )
        {
            m_base = new DefaultConfiguration( "-", null );
        }
        else
        {
            m_base = base;
        }
        if( parent == null )
        {
            m_parent = new DefaultConfiguration( "-", null );
        }
        else
        {
            m_parent = parent;
        }
    }

    //=============================================================================
    // Configuration
    //=============================================================================

    /**
     * Return the name of the base node.
     * @return name of the <code>Configuration</code> node.
     */
    public String getName()
    {
        return m_base.getName();
    }

    /**
     * Return a string describing location of the base Configuration.
     * Location can be different for different mediums (ie "file:line" for normal XML files or
     * "table:primary-key" for DB based configurations);
     *
     * @return a string describing location of Configuration
     */
    public String getLocation()
    {
        return m_base.getLocation();
    }

    /**
     * Returns the namespace the main Configuration node
     * belongs to.
     * @exception ConfigurationException may be thrown by the underlying configuration
     * @since 4.1
     * @return a Namespace identifying the namespace of this Configuration.
     */
    public String getNamespace() throws ConfigurationException
    {
        return m_base.getNamespace();
    }

    /**
     * Return a new <code>MergedConfiguration</code> instance. The configuration may be merged
     * depending upon the value of the <code>phoenix-configuration:merge</code> attribute in
     * the base configuration
     *
     * @param child The name of the child node.
     * @return Configuration
     */
    public Configuration getChild( String child )
    {
        return getChild( child, true );
    }

    /**
     * Return a <code>Configuration</code> instance encapsulating the specified
     * child node.
     *
     * @param child The name of the child node.
     * @param createNew If <code>true</code>, a new <code>Configuration</code>
     * will be created and returned if the specified child does not exist in either
     * the base or parent configuratioin. If <code>false</code>, <code>null</code>
     * will be returned when the specified child doesn't exist in either the base or
     * the parent.
     * @return Configuration
     */
    public Configuration getChild( String child, boolean createNew )
    {
        final Configuration b = m_base.getChild( child, false );

        if( null == b )
        {
            return m_parent.getChild( child, createNew );
        }
        else if( b.getAttributeAsBoolean( MERGE_ATTR, false ) )
        {
            return new MergedConfiguration( b, m_parent.getChild( child ) );
        }
        else
        {
            return b;
        }
    }

    /**
     * Return an <code>Array</code> of <code>Configuration</code>
     * elements containing all node children of both base and parent configurations.
     * The array order will reflect the order in the source config file, commencing
     * with the base configuration. This list will be merged for any children that satisfy the
     * <code>phoenix-configuration:merge</code> rules.
     *
     * @return All child nodes
     */
    public Configuration[] getChildren()
    {
        final Configuration[] b = m_base.getChildren();
        final Configuration[] p = m_parent.getChildren();
        final List children = new ArrayList();
        final Set parentUsed = new HashSet();

        for( int i = 0; i < b.length; i++ )
        {
            final String name = b[i].getName();

            if( isLoneConfiguration( name, b ) //is the only one with this name as a child here..
                && b[i].getAttributeAsBoolean( MERGE_ATTR, false ) // and we can merge it
                && isLoneConfiguration( name, p ) ) //and there is only a single item in the parent to merge to
            {
                final Configuration parent = m_parent.getChild( name, true );

                children.add( new MergedConfiguration( b[i], parent ) );
                parentUsed.add( parent );
            }
            else
            {
                children.add( b[i] );
            }
        }

        for( int i = 0; i < p.length; i++ )
        {
            if( !parentUsed.contains( p[i] ) )
            {
                children.add( p[i] );
            }
        }

        return ( Configuration[] ) children.toArray( new Configuration[children.size()] );
    }

    private boolean isLoneConfiguration( String name, Configuration list[] )
    {
        boolean found = false;

        for( int i = 0; i < list.length; i++ )
        {
            if( name.equals( list[i].getName() ) )
            {
                if( found )
                {
                    return false;
                }
                else
                {
                    found = true;
                }
            }
        }

        return true;
    }

    /**
     * Return an <code>Array</code> of <code>Configuration</code>
     * elements containing all node children with the specified name from
     * both base and parent configurations. The array
     * order will reflect the order in the source config file commencing
     * with the base configuration. The configuration may be merged according to the rules for
     * <code>phoenix-configuration:merge</code>
     *
     * @param name The name of the children to get.
     * @return The child nodes with name <code>name</code>
     */
    public Configuration[] getChildren( String name )
    {
        final Configuration[] b = m_base.getChildren( name );
        final Configuration[] p = m_parent.getChildren( name );

        if( b.length == 0 && p.length == 0 ) //both are zero, no need to do anything
        {
            return new Configuration[0];
        }
        else if( b.length == 0 ) // base is empty, return parent
        {
            return p;
        }
        else if( p.length == 0 ) //parent is empty, return base
        {
            return b;
        }
        else if( p.length == 1
            && b.length == 1
            && b[0].getAttributeAsBoolean( MERGE_ATTR, false ) )
        {
            return new Configuration[]{new MergedConfiguration( b[0], p[0] )};
        }
        else
        {
            Configuration[] result = new Configuration[b.length + p.length];
            System.arraycopy( b, 0, result, 0, b.length );
            System.arraycopy( p, 0, result, b.length, p.length );
            return result;
        }
    }

    /**
     * Return an array of all attribute names in both base and parent.
     * <p>
     * <em>The order of attributes in this array can not be relied on.</em> As
     * with XML, a <code>Configuration</code>'s attributes are an
     * <em>unordered</em> set. If your code relies on order, eg
     * <tt>conf.getAttributeNames()[0]</tt>, then it is liable to break if a
     * different XML parser is used.
     * </p>
     *
     * If an attribute named <code>phoenix-configuration:merge</code> exists in either the
     * parent or base, it will not be shown in this list.
     *
     * @return an array of all attribute names
     */
    public String[] getAttributeNames()
    {
        List list = new ArrayList();
        String[] names = m_base.getAttributeNames();
        String[] names2 = m_parent.getAttributeNames();

        for( int i = 0; i < names.length; i++ )
        {
            if( !( MERGE_ATTR.equals( names[i] ) || KEY_ATTR.equals( names[i] ) ) )
            {
                list.add( names[i] );
            }
        }

        for( int i = 0; i < names2.length; i++ )
        {
            final String name = names2[i];

            if( !( MERGE_ATTR.equals( name ) || KEY_ATTR.equals( name ) )
                && list.indexOf( name ) < 0 )
            {
                list.add( name );
            }
        }

        return ( String[] ) list.toArray( new String[list.size()] );
    }

    /**
     * Return the value of specified attribute.  If the base configuration
     * does not contain the attribute, the equivialent operation is applied to
     * the parent configuration.
     *
     * @param paramName The name of the parameter you ask the value of.
     * @return String value of attribute.
     * @exception ConfigurationException If no attribute with that name exists.
     */
    public String getAttribute( String paramName ) throws ConfigurationException
    {
        try
        {
            return m_base.getAttribute( paramName );
        }
        catch( ConfigurationException e )
        {
            return m_parent.getAttribute( paramName );
        }
    }

    /**
     * Return the <code>int</code> value of the specified attribute contained
     * in this node or the parent.
     * @param paramName The name of the parameter you ask the value of.
     * @return int value of attribute
     * @exception ConfigurationException If no parameter with that name exists.
     *                                   or if conversion to <code>int</code> fails.
     */
    public int getAttributeAsInteger( String paramName ) throws ConfigurationException
    {
        try
        {
            return m_base.getAttributeAsInteger( paramName );
        }
        catch( ConfigurationException e )
        {
            return m_parent.getAttributeAsInteger( paramName );
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>long</code>.
     *
     * @param name The name of the parameter you ask the value of.
     * @return long value of attribute
     * @exception ConfigurationException If no parameter with that name exists.
     *                                   or if conversion to <code>long</code> fails.
     */
    public long getAttributeAsLong( String name ) throws ConfigurationException
    {
        try
        {
            return m_base.getAttributeAsLong( name );
        }
        catch( ConfigurationException e )
        {
            return m_parent.getAttributeAsLong( name );
        }
    }

    /**
     * Return the <code>float</code> value of the specified parameter contained
     * in this node.
     * @param paramName The name of the parameter you ask the value of.
     * @return float value of attribute
     * @exception ConfigurationException If no parameter with that name exists.
     *                                   or if conversion to <code>float</code> fails.
     */
    public float getAttributeAsFloat( String paramName ) throws ConfigurationException
    {
        try
        {
            return m_base.getAttributeAsFloat( paramName );
        }
        catch( ConfigurationException e )
        {
            return m_parent.getAttributeAsFloat( paramName );
        }
    }

    /**
     * Return the <code>boolean</code> value of the specified parameter contained
     * in this node.
     *
     * @param paramName The name of the parameter you ask the value of.
     * @return boolean value of attribute
     * @exception ConfigurationException If no parameter with that name exists.
     *                                   or if conversion to <code>boolean</code> fails.
     */
    public boolean getAttributeAsBoolean( String paramName ) throws ConfigurationException
    {
        try
        {
            return m_base.getAttributeAsBoolean( paramName );
        }
        catch( ConfigurationException e )
        {
            return m_parent.getAttributeAsBoolean( paramName );
        }
    }

    /**
     * Return the <code>String</code> value of the node.
     *
     * @return the value of the node.
     * @exception ConfigurationException May be raised by underlying
     *                                   base or parent configuration.
     */
    public String getValue() throws ConfigurationException
    {
        try
        {
            return m_base.getValue();
        }
        catch( ConfigurationException e )
        {
            return m_parent.getValue();
        }
    }

    /**
     * Return the <code>int</code> value of the node.
     * @return int the value as an integer
     * @exception ConfigurationException If conversion to <code>int</code> fails.
     */
    public int getValueAsInteger() throws ConfigurationException
    {
        try
        {
            return m_base.getValueAsInteger();
        }
        catch( ConfigurationException e )
        {
            return m_parent.getValueAsInteger();
        }
    }

    /**
     * Return the <code>float</code> value of the node.
     *
     * @return the value of the node.
     * @exception ConfigurationException If conversion to <code>float</code> fails.
     */
    public float getValueAsFloat() throws ConfigurationException
    {
        try
        {
            return m_base.getValueAsFloat();
        }
        catch( ConfigurationException e )
        {
            return m_parent.getValueAsFloat();
        }
    }

    /**
     * Return the <code>boolean</code> value of the node.
     *
     * @return the value of the node.
     * @exception ConfigurationException If conversion to <code>boolean</code> fails.
     */
    public boolean getValueAsBoolean() throws ConfigurationException
    {
        try
        {
            return m_base.getValueAsBoolean();
        }
        catch( ConfigurationException e )
        {
            return m_parent.getValueAsBoolean();
        }
    }

    /**
     * Return the <code>long</code> value of the node.
     *
     * @return the value of the node.
     * @exception ConfigurationException If conversion to <code>long</code> fails.
     */
    public long getValueAsLong() throws ConfigurationException
    {
        try
        {
            return m_base.getValueAsLong();
        }
        catch( ConfigurationException e )
        {
            return m_parent.getValueAsLong();
        }
    }

    /**
     * Returns the value of the configuration element as a <code>String</code>.
     * If the configuration value is not set, the default value will be
     * used.
     *
     * @param defaultValue The default value desired.
     * @return String value of the <code>Configuration</code>, or default
     *          if none specified.
     */
    public String getValue( String defaultValue )
    {
        try
        {
            return m_base.getValue();
        }
        catch( ConfigurationException e )
        {
            return m_parent.getValue( defaultValue );
        }
    }

    /**
     * Returns the value of the configuration element as an <code>int</code>.
     * If the configuration value is not set, the default value will be
     * used.
     *
     * @param defaultValue The default value desired.
     * @return int value of the <code>Configuration</code>, or default
     *          if none specified.
     */
    public int getValueAsInteger( int defaultValue )
    {
        try
        {
            return m_base.getValueAsInteger();
        }
        catch( ConfigurationException e )
        {
            return m_parent.getValueAsInteger( defaultValue );
        }
    }

    /**
     * Returns the value of the configuration element as a <code>long</code>.
     * If the configuration value is not set, the default value will be
     * used.
     *
     * @param defaultValue The default value desired.
     * @return long value of the <code>Configuration</code>, or default
     *          if none specified.
     */
    public long getValueAsLong( long defaultValue )
    {
        try
        {
            return m_base.getValueAsLong();
        }
        catch( ConfigurationException e )
        {
            return m_parent.getValueAsLong( defaultValue );
        }
    }

    /**
     * Returns the value of the configuration element as a <code>float</code>.
     * If the configuration value is not set, the default value will be
     * used.
     *
     * @param defaultValue The default value desired.
     * @return float value of the <code>Configuration</code>, or default
     *          if none specified.
     */
    public float getValueAsFloat( float defaultValue )
    {
        try
        {
            return m_base.getValueAsFloat();
        }
        catch( ConfigurationException e )
        {
            return m_parent.getValueAsFloat( defaultValue );
        }
    }

    /**
     * Returns the value of the configuration element as a <code>boolean</code>.
     * If the configuration value is not set, the default value will be
     * used.
     *
     * @param defaultValue The default value desired.
     * @return boolean value of the <code>Configuration</code>, or default
     *          if none specified.
     */
    public boolean getValueAsBoolean( boolean defaultValue )
    {
        try
        {
            return m_base.getValueAsBoolean();
        }
        catch( ConfigurationException e )
        {
            return m_parent.getValueAsBoolean( defaultValue );
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>String</code>, or the default value if no attribute by
     * that name exists or is empty.
     *
     * @param name The name of the attribute you ask the value of.
     * @param defaultValue The default value desired.
     * @return String value of attribute. It will return the default
     *         value if the named attribute does not exist, or if
     *         the value is not set.
     */
    public String getAttribute( String name, String defaultValue )
    {
        try
        {
            return m_base.getAttribute( name );
        }
        catch( ConfigurationException e )
        {
            return m_parent.getAttribute( name, defaultValue );
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>int</code>, or the default value if no attribute by
     * that name exists or is empty.
     *
     * @param name The name of the attribute you ask the value of.
     * @param defaultValue The default value desired.
     * @return int value of attribute. It will return the default
     *         value if the named attribute does not exist, or if
     *         the value is not set.
     */
    public int getAttributeAsInteger( String name, int defaultValue )
    {
        try
        {
            return m_base.getAttributeAsInteger( name );
        }
        catch( ConfigurationException e )
        {
            return m_parent.getAttributeAsInteger( name, defaultValue );
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>long</code>, or the default value if no attribute by
     * that name exists or is empty.
     *
     * @param name The name of the attribute you ask the value of.
     * @param defaultValue The default value desired.
     * @return long value of attribute. It will return the default
     *          value if the named attribute does not exist, or if
     *          the value is not set.
     */
    public long getAttributeAsLong( String name, long defaultValue )
    {
        try
        {
            return m_base.getAttributeAsLong( name );
        }
        catch( ConfigurationException e )
        {
            return m_parent.getAttributeAsLong( name, defaultValue );
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>float</code>, or the default value if no attribute by
     * that name exists or is empty.
     *
     * @param name The name of the attribute you ask the value of.
     * @param defaultValue The default value desired.
     * @return float value of attribute. It will return the default
     *          value if the named attribute does not exist, or if
     *          the value is not set.
     */
    public float getAttributeAsFloat( String name, float defaultValue )
    {
        try
        {
            return m_base.getAttributeAsFloat( name );
        }
        catch( ConfigurationException e )
        {
            return m_parent.getAttributeAsFloat( name, defaultValue );
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>boolean</code>, or the default value if no attribute by
     * that name exists or is empty.
     *
     * @param name The name of the attribute you ask the value of.
     * @param defaultValue The default value desired.
     * @return boolean value of attribute. It will return the default
     *         value if the named attribute does not exist, or if
     *         the value is not set.
     */
    public boolean getAttributeAsBoolean( String name, boolean defaultValue )
    {
        try
        {
            return m_base.getAttributeAsBoolean( name );
        }
        catch( ConfigurationException e )
        {
            return m_parent.getAttributeAsBoolean( name, defaultValue );
        }
    }
}



