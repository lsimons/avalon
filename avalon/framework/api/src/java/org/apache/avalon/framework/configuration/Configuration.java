/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.configuration;

/**
 * <code>Configuration</code> is a interface encapsulating a configuration node
 * used to retrieve configuration values. This is a "read only" interface
 * preventing applications from modifying their own configurations.
 * <br />
 *
 * The contract surrounding the <code>Configuration</code> is that once
 * it is created, information never changes.  The <code>Configuration</code>
 * is built by the <code>SAXConfigurationBuilder</code> and the
 * <code>ConfigurationImpl</code> helper classes.
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:pier@apache.org">Pierpaolo Fumagalli</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Configuration
{
    /**
     * Return the name of the node.
     *
     * @post getName() != null
     *
     * @return name of the <code>Configuration</code> node.
     */
    String getName();

    /**
     * Return a string describing location of Configuration.
     * Location can be different for different mediums (ie "file:line" for normal XML files or
     * "table:primary-key" for DB based configurations);
     *
     * @return a string describing location of Configuration
     */
    String getLocation();

    /**
     * Return a new <code>Configuration</code> instance encapsulating the
     * specified child node.
     *
     * @pre child != null
     * @post getConfiguration() != null
     *
     * @param child The name of the child node.
     * @return Configuration
     */
    Configuration getChild( String child );

    /**
     * Return a new <code>Configuration</code> instance encapsulating the
     * specified child node.
     *
     * @pre child != null
     * @post getConfiguration() != null
     *
     * @param child The name of the child node.
     * @return Configuration
     */
    Configuration getChild( String child, boolean createNew );

    /**
     * Return an <code>Array</code> of <code>Configuration</code>
     * elements containing all node children.
     *
     * @return The child nodes with name
     */
    Configuration[] getChildren();

    /**
     * Return an <code>Array</code> of <code>Configuration</code>
     * elements containing all node children with the specified name.
     *
     * @pre name != null
     * @post getConfigurations() != null
     *
     * @param name The name of the children to get.
     * @return The child nodes with name
     */
    Configuration[] getChildren( String name );

    /**
     * Return an array of all attribute names.
     */
    String[] getAttributeNames();

    /**
     * Return the value of specified attribute.
     *
     * @pre paramName != null
     * @post getAttribute != null
     *
     * @param paramName The name of the parameter you ask the value of.
     * @return String value of attribute.
     * @exception ConfigurationException If no attribute with that name exists.
     */
    String getAttribute( String paramName ) throws ConfigurationException;

    /**
     * Return the <code>int</code> value of the specified attribute contained
     * in this node.
     *
     * @pre paramName != null
     * @post getAttributeAsInteger() != null
     *
     * @param paramName The name of the parameter you ask the value of.
     * @return int value of attribute
     * @exception ConfigurationException If no parameter with that name exists.
     *                                   or if conversion to <code>int</code> fails.
     */
    int getAttributeAsInteger( String paramName ) throws ConfigurationException;

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>long</code>.
     *
     * @pre paramName != null
     * @post getAttributeAsLong() != null
     *
     * @param paramName The name of the parameter you ask the value of.
     * @return long value of attribute
     * @exception ConfigurationException If no parameter with that name exists.
     *                                   or if conversion to <code>long</code> fails.
     */
    long getAttributeAsLong( String name ) throws ConfigurationException;

    /**
     * Return the <code>float</code> value of the specified parameter contained
     * in this node.
     *
     * @pre paramName != null
     * @post getAttributeAsFloat() != null
     *
     * @param paramName The name of the parameter you ask the value of.
     * @return float value of attribute
     * @exception ConfigurationException If no parameter with that name exists.
     *                                   or if conversion to <code>float</code> fails.
     */
    float getAttributeAsFloat( String paramName ) throws ConfigurationException;

    /**
     * Return the <code>boolean</code> value of the specified parameter contained
     * in this node.<br>
     *
     * @pre paramName != null
     * @post getAttributeAsBoolean() != null
     *
     * @param paramName The name of the parameter you ask the value of.
     * @return boolean value of attribute
     * @exception ConfigurationException If no parameter with that name exists.
     *                                   or if conversion to <code>boolean</code> fails.
     */
    boolean getAttributeAsBoolean( String paramName ) throws ConfigurationException;

    /**
     * Return the <code>String</code> value of the node.
     *
     * @post getValue() != null
     *
     * @return the value of the node.
     */
    String getValue() throws ConfigurationException;

    /**
     * Return the <code>int</code> value of the node.
     *
     * @post getValueAsInteger() != null
     *
     * @return the value of the node.
     *
     * @exception ConfigurationException If conversion to <code>int</code> fails.
     */
    int getValueAsInteger() throws ConfigurationException;

    /**
     * Return the <code>float</code> value of the node.
     *
     * @post getValueAsFloat() != null
     *
     * @return the value of the node.
     * @exception ConfigurationException If conversion to <code>float</code> fails.
     */
    float getValueAsFloat() throws ConfigurationException;

    /**
     * Return the <code>boolean</code> value of the node.
     *
     * @post getValueAsBoolean() != null
     *
     * @return the value of the node.
     * @exception ConfigurationException If conversion to <code>boolean</code> fails.
     */
    boolean getValueAsBoolean() throws ConfigurationException;

    /**
     * Return the <code>long</code> value of the node.<br>
     *
     * @post getValueAsLong() != null
     *
     * @return the value of the node.
     * @exception ConfigurationException If conversion to <code>long</code> fails.
     */
    long getValueAsLong() throws ConfigurationException;

    /**
     * Returns the value of the configuration element as a <code>String</code>.
     * If the configuration value is not set, the default value will be
     * used.
     *
     * @pre defaultValue != null
     * @post getValue(defaultValue) != null
     *
     * @param defaultValue The default value desired.
     * @return String value of the <code>Configuration</code>, or default
     *          if none specified.
     */
    String getValue( String defaultValue );

    /**
     * Returns the value of the configuration element as an <code>int</code>.
     * If the configuration value is not set, the default value will be
     * used.
     *
     * @pre defaultValue != null
     * @post getValueAsInteger(defaultValue) != null
     *
     * @param defaultValue The default value desired.
     * @return int value of the <code>Configuration</code>, or default
     *          if none specified.
     */
    int getValueAsInteger( int defaultValue );

    /**
     * Returns the value of the configuration element as a <code>long</code>.
     * If the configuration value is not set, the default value will be
     * used.
     *
     * @pre defaultValue != null
     * @post getValueAsLong(defaultValue) != null
     *
     * @param defaultValue The default value desired.
     * @return long value of the <code>Configuration</code>, or default
     *          if none specified.
     */
    long getValueAsLong( long defaultValue );

    /**
     * Returns the value of the configuration element as a <code>float</code>.
     * If the configuration value is not set, the default value will be
     * used.
     *
     * @pre defaultValue != null
     * @post getValueAsFloat(defaultValue) != null
     *
     * @param defaultValue The default value desired.
     * @return float value of the <code>Configuration</code>, or default
     *          if none specified.
     */
    float getValueAsFloat( float defaultValue );

    /**
     * Returns the value of the configuration element as a <code>boolean</code>.
     * If the configuration value is not set, the default value will be
     * used.
     *
     * @pre defaultValue != null
     * @post getValueAsBoolean(defaultValue) != null
     *
     * @param defaultValue The default value desired.
     * @return boolean value of the <code>Configuration</code>, or default
     *          if none specified.
     */
    boolean getValueAsBoolean( boolean defaultValue );

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>String</code>, or the default value if no attribute by
     * that name exists or is empty.
     *
     * @pre name != null
     * @pre defaultValue != null
     * @post getAttribute(name, defaultValue) != null
     *
     * @param name The name of the attribute you ask the value of.
     * @param defaultValue The default value desired.
     * @return String value of attribute. It will return the default
     *         value if the named attribute does not exist, or if
     *         the value is not set.
     */
    String getAttribute( String name, String defaultValue );

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>int</code>, or the default value if no attribute by
     * that name exists or is empty.
     *
     * @pre name != null
     * @pre defaultValue != null
     * @post getAttributeAsInteger(name, defaultValue) != null
     *
     * @param name The name of the attribute you ask the value of.
     * @param defaultValue The default value desired.
     * @return int value of attribute. It will return the default
     *         value if the named attribute does not exist, or if
     *         the value is not set.
     */
    int getAttributeAsInteger( String name, int defaultValue );

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>long</code>, or the default value if no attribute by
     * that name exists or is empty.
     *
     * @pre name != null
     * @pre defaultValue != null
     * @post getAttributeAsLong(name, defaultValue) != null
     *
     * @param name The name of the attribute you ask the value of.
     * @param defaultValue The default value desired.
     * @return long value of attribute. It will return the default
     *          value if the named attribute does not exist, or if
     *          the value is not set.
     */
    long getAttributeAsLong( String name, long defaultValue );

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>float</code>, or the default value if no attribute by
     * that name exists or is empty.
     *
     * @pre name != null
     * @pre defaultValue != null
     * @post getAttributeAsFloat(name, defaultValue) != null
     *
     * @param name The name of the attribute you ask the value of.
     * @param defaultValue The default value desired.
     * @return float value of attribute. It will return the default
     *          value if the named attribute does not exist, or if
     *          the value is not set.
     */
    float getAttributeAsFloat( String name, float defaultValue );

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>boolean</code>, or the default value if no attribute by
     * that name exists or is empty.
     *
     * @pre name != null
     * @pre defaultValue != null
     * @post getAttributeAsBoolean(name, defaultValue) != null
     *
     * @param name The name of the attribute you ask the value of.
     * @param defaultValue The default value desired.
     * @return boolean value of attribute. It will return the default
     *         value if the named attribute does not exist, or if
     *         the value is not set.
     */
    boolean getAttributeAsBoolean( String name, boolean defaultValue );
}
