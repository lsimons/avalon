/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.TXT file.
 *
 * Original contribution by OSM SARL, http://www.osm.net
 */
package org.apache.avalon.framework.configuration;

import java.util.Vector;

import org.apache.avalon.framework.CascadingRuntimeException;

/**
 * General utility supporting static operations for generating string
 * representations of a configuration suitable for debugging.
 * @author Stephen McConnell <mcconnell@osm.net>
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public class ConfigurationUtil
{
    private static final Configuration[] EMPTY_CONFS = new Configuration[0];

    /**
     * Returns a simple string representation of the the supplied configuration.
     * @param config a configuration
     * @return a simplified text representation of a configuration suitable
     *     for debugging
     */
    public static String list( Configuration config )
    {
        final StringBuffer buffer = new StringBuffer();
        list( buffer, "  ", config );
        buffer.append( "\n" );
        return buffer.toString();
    }

    private static void list( StringBuffer buffer, String lead, Configuration config )
    {

        buffer.append( "\n" + lead + "<" + config.getName() );
        String[] names = config.getAttributeNames();
        if( names.length > 0 )
        {
            for( int i = 0; i < names.length; i++ )
            {
                buffer.append( " "
                               + names[i] + "=\""
                               + config.getAttribute( names[i], "???" ) + "\"" );
            }
        }
        Configuration[] children = config.getChildren();
        if( children.length > 0 )
        {
            buffer.append( ">" );
            for( int j = 0; j < children.length; j++ )
            {
                list( buffer, lead + "  ", children[j] );
            }
            buffer.append( "\n" + lead + "</" + config.getName() + ">" );
        }
        else
        {
            if( config.getValue( null ) != null )
            {
                buffer.append( ">...</" + config.getName() + ">" );
            }
            else
            {
                buffer.append( "/>" );
            }
        }
    }

    /**
     * Return all occurance of a configuration child containing the supplied attribute name.
     * @param config the configuration
     * @param element the name of child elements to select from the configuration
     * @param attribute the attribute name to filter
     * @param value the attribute value to match (null will match any attribute value)
     * @return an array of configuration instances matching the query
     */
    public static Configuration[] match( Configuration config, String element, String attribute )
    {
        return match( config, element, attribute, null );
    }

    /**
     * Return occurance of a configuration child containing the supplied attribute name and value.
     * @param config the configuration
     * @param element the name of child elements to select from the configuration
     * @param attribute the attribute name to filter
     * @param value the attribute value to match (null will match any attribute value)
     * @return an array of configuration instances matching the query
     */
    public static Configuration[] match(
      final Configuration config, final String element, final String attribute, final String value )
    {
        Vector vector = new Vector();

        Configuration[] children = config.getChildren( element );
        for( int i = 0; i < children.length; i++ )
        {
            String v = children[i].getAttribute( attribute, null );
            if( v != null )
            {
                if( ( value == null ) || v.equals( value ) )
                {
                    // it's a match
                    vector.add( children[i] );
                }
            }
        }
        return ( Configuration[] ) vector.toArray( EMPTY_CONFS );
    }

    /**
     * Return the first occurance of a configuration child containing the supplied attribute name and value
     * or create a new empty configuration if no match found.
     * @param config the configuration
     * @param element the name of child elements to select from the configuration
     * @param attribute the attribute name to filter
     * @param value the attribute value to match (null will match any attribute value)
     * @return a configuration instances matching the query or empty configuration
     */
    public static Configuration matchFirstOccurance(
      Configuration config, String element, String attribute, String value )
    {
        try
        {
            return matchFirstOccurance( config, element, attribute, value, true );
        }
        catch( ConfigurationException e )
        {
            // will not happen
            throw new CascadingRuntimeException( "Unexpected exception condition.", e );
        }
    }

    /**
     * Return the first occurance of a configuration child containing the supplied attribute
     * name and value.  If the supplied creation policy if TRUE and no match is found, an
     * empty configuration instance is returned, otherwise a null will returned.
     * @param config the configuration
     * @param element the name of child elements to select from the configuration
     * @param attribute the attribute name to filter
     * @param value the attribute value to match (null will match any attribute value)
     * @param create the creation policy if no match
     * @return a configuration instances matching the query
     */
    public static Configuration matchFirstOccurance(
      Configuration config, String element, String attribute, String value, boolean create )
      throws ConfigurationException
    {
        Configuration[] children = config.getChildren( element );
        for( int i = 0; i < children.length; i++ )
        {
            String v = children[i].getAttribute( attribute, null );
            if( v != null )
            {
                if( ( value == null ) || v.equals( value ) )
                {
                    // it's a match
                    return children[i];
                }
            }
        }

        if( create )
            return new DefaultConfiguration( element, null );
        return null;
    }

    /**
     * Create a copy of the specified configuration giving it a new name. This performs
     * a shallow copy, child configurations will still be the same objects as the original
     * configuration had
     *
     * @param config configuration to branch
     * @param name name for new configuration
     * @return configuration with new name
     */
    public static Configuration branch( final Configuration config, final String name )
    {
        final DefaultConfiguration c = createNew( config, name );
        final String[] attributes = config.getAttributeNames();
        final Configuration[] kids = config.getChildren();

        c.setValue( config.getValue( null ) );

        for( int i = 0; i < attributes.length; i++ )
        {
            try
            {
                c.setAttribute( attributes[i], config.getAttribute( attributes[i] ) );
            }
            catch( ConfigurationException e )
            {
                throw new CascadingRuntimeException( "Configuration is missing advertised attribute", e );
            }
        }

        for( int i = 0; i < kids.length; i++ )
        {
            c.addChild( kids[i] );
        }

        c.makeReadOnly();

        return c;
    }

    private static DefaultConfiguration createNew( final Configuration config, final String name )
    {
        if( config instanceof AbstractConfiguration )
        {
            try
            {
                return new DefaultConfiguration( name,
                                                 config.getLocation(),
                                                 config.getNamespace(),
                                                 ( ( AbstractConfiguration ) config ).getPrefix() );
            }
            catch( ConfigurationException e )
            {
                //Ignore. If there is an error in the namespaces, we won't copy namespaces stuff
            }
        }

        return new DefaultConfiguration( name, config.getLocation() );
    }
}



