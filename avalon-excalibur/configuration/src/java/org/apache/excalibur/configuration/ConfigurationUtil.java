/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.TXT file.
 *
 * Original contribution by OSM SARL, http://www.osm.net
 */

package org.apache.excalibur.configuration;

import org.apache.avalon.framework.configuration.Configuration;

/**
 * General utility supporting static operations for generating string 
 * representations of a configuration suitable for debugging.
 * @author Stephen McConnell <mcconnell@osm.net>
 */
public class ConfigurationUtil 
{
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
        buffer.append("\n");
        return buffer.toString(); 
    }

    private static void list( StringBuffer buffer, String lead, Configuration config )
    {

        buffer.append( "\n" + lead + "<" + config.getName() );
        String[] names = config.getAttributeNames();
        if( names.length > 0 )
        {
            for( int i=0; i<names.length; i++ )
            {
                buffer.append( " " 
                  + names[i] + "=\"" 
                  + config.getAttribute( names[i], "???" ) + "\"" ); 
            }
        }
        Configuration[] children = config.getChildren();
        if( children.length > 0 )
        {
            buffer.append(">");
            for( int j=0; j<children.length; j++ )
            {
                 list( buffer, lead + "  ", children[j] ); 
            }
            buffer.append( "\n" + lead + "</" + config.getName() + ">");
        }
        else
        {
            if( config.getValue( null ) != null )
            {
                buffer.append( ">...</" + config.getName() + ">");
            }
            else
            {
                buffer.append( "/>");
            }
        }
    }
}



