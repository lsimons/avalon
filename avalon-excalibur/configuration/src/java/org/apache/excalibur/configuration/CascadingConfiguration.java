/* 
 * Copyright 2002-2004 The Apache Software Foundation
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
package org.apache.excalibur.configuration;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

/**
 * The CascadingConfiguration is a classic Configuration backed by parent
 * Configuration.  Operations such as getChild return a CascadingConfiguration
 * encapsulating both a primary and parent configuration.  Requests for attribute
 * values are resolved against the base configuration initially.  If the result
 * of the resolution is unsucessful, the request is applied against the parent
 * configuration.  As a parent may also be a CascadingConfiguration, the evaluation
 * will be applied until a value is resolved against a class parent Configuration.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public class CascadingConfiguration implements Configuration
{
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
     * Create a CascadingConfiguration with specified parent.  The base
     * configuration shall override a parent configuration on request for
     * attribute values and configuration body values.  Unresolved request
     * are redirected up the parent chain until a classic configuration is
     * reached.  Request for child configurations will return a
     * new CascadingConfiguration referencing the child of the base and
     * the child of the primary (i.e. a child configuration chain).
     *
     * @param base the base Configuration
     * @param parent the parent Configuration
     */
    public CascadingConfiguration( final Configuration base, final Configuration parent )
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
     * Return a new <code>CascadingConfiguration</code> instance encapsulating the
     * specified child node of the base and parent node.
     *
     * @param child The name of the child node.
     * @return Configuration
     */
    public Configuration getChild( String child )
    {
        return new CascadingConfiguration( m_base.getChild( child ), m_parent.getChild( child ) );
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
        if( createNew )
        {
            return getChild( child );
        }
        Configuration c = m_base.getChild( child, false );
        if( c != null )
        {
            return c;
        }
        return m_parent.getChild( child, false );
    }

    /**
     * Return an <code>Array</code> of <code>Configuration</code>
     * elements containing all node children of both base and parent configurations.
     * The array order will reflect the order in the source config file, commencing
     * with the base configuration.
     *
     * @return All child nodes
     */
    public Configuration[] getChildren()
    {
        Configuration[] b = m_base.getChildren();
        Configuration[] p = m_parent.getChildren();
        Configuration[] result = new Configuration[ b.length + p.length ];
        System.arraycopy( b, 0, result, 0, b.length );
        System.arraycopy( p, 0, result, b.length, p.length );
        return result;
    }

    /**
     * Return an <code>Array</code> of <code>Configuration</code>
     * elements containing all node children with the specified name from
     * both base and parent configurations. The array
     * order will reflect the order in the source config file commencing
     * with the base configuration.
     *
     * @param name The name of the children to get.
     * @return The child nodes with name <code>name</code>
     */
    public Configuration[] getChildren( String name )
    {
        Configuration[] b = m_base.getChildren( name );
        Configuration[] p = m_parent.getChildren( name );
        Configuration[] result = new Configuration[ b.length + p.length ];
        System.arraycopy( b, 0, result, 0, b.length );
        System.arraycopy( p, 0, result, b.length, p.length );
        return result;
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
     * @return an array of all attribute names
     */
    public String[] getAttributeNames()
    {
        java.util.Vector vector = new java.util.Vector();
        String[] names = m_base.getAttributeNames();
        String[] names2 = m_parent.getAttributeNames();
        for( int i = 0; i < names.length; i++ )
        {
            vector.add( names[ i ] );
        }
        for( int i = 0; i < names2.length; i++ )
        {
            if( vector.indexOf( names2[ i ] ) < 0 )
            {
                vector.add( names2[ i ] );
            }
        }
        return (String[])vector.toArray( new String[ 0 ] );
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



