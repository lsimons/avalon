/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

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

package org.apache.avalon.phoenix.components.configuration.merger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

/**
 * The ConfigurationMerger will take a Configuration object and layer it over another.
 *
 * It will use special attributes on the layer's children to control how children
 * of the layer and base are combined. In order for a child of the layer to be merged with a
 * child of the base, the following must hold true:
 * <ol>
 *   <li>The child in the <b>layer</b> Configuration has an attribute named
 *       <code>phoenix-configuration:merge</code> and its value is equal to a boolean
 *       <code>TRUE</code>
 *   </li>
 *   <li>There must be a single child in both the layer and base with the same getName() <b>OR</b>
 *       there exists an attribute named <code>phoenix-configuration:key-attribute</code>
 *       that names an attribute that exists on both the layer and base that can be used to match
 *       multiple children of the same getName()
 *   </li>
 * </ol>
 *
 * @see ConfigurationSplitter
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public class ConfigurationMerger
{
    /**
     * Merge two configurations.
     *
     * @param layer Configuration to <i>layer</i> over the base
     * @param base Configuration <i>layer</i> will be merged with
     *
     * @return Result of merge
     *
     * @exception org.apache.avalon.framework.configuration.ConfigurationException if unable to merge
     */
    public static Configuration merge( final Configuration layer, final Configuration base )
        throws ConfigurationException
    {
        final DefaultConfiguration merged =
            new DefaultConfiguration( base.getName(),
                                      "Merged [layer: " + layer.getLocation()
                                      + ", base: " + base.getLocation() + "]" );

        copyAttributes( base, merged );
        copyAttributes( layer, merged );

        mergeChildren( layer, base, merged );

        merged.setValue( getValue( layer, base ) );
        merged.makeReadOnly();

        return merged;
    }

    private static void mergeChildren( final Configuration layer,
                                       final Configuration base,
                                       final DefaultConfiguration merged )
        throws ConfigurationException
    {
        final Configuration[] lc = layer.getChildren();
        final Configuration[] bc = base.getChildren();
        final Set baseUsed = new HashSet();

        for( int i = 0; i < lc.length; i++ )
        {
            final Configuration mergeWith = getMergePartner( lc[ i ], layer, base );

            if( null == mergeWith )
            {
                merged.addChild( lc[ i ] );
            }
            else
            {
                merged.addChild( merge( lc[ i ], mergeWith ) );

                baseUsed.add( mergeWith );
            }
        }

        for( int i = 0; i < bc.length; i++ )
        {
            if( !baseUsed.contains( bc[ i ] ) )
            {
                merged.addChild( bc[ i ] );
            }
        }
    }

    private static Configuration getMergePartner( final Configuration toMerge,
                                                  final Configuration layer,
                                                  final Configuration base )
        throws ConfigurationException
    {
        if( toMerge.getAttributeAsBoolean( Constants.MERGE_ATTR, false ) )
        {
            final String keyAttribute = toMerge.getAttribute( Constants.KEY_ATTR, null );
            final String keyvalue =
                keyAttribute == null ? null : toMerge.getAttribute( keyAttribute );

            final Configuration[] layerKids = match( layer,
                                                                       toMerge.getName(),
                                                                       keyAttribute,
                                                                       keyvalue );

            final Configuration[] baseKids = match( base,
                                                                      toMerge.getName(),
                                                                      keyAttribute,
                                                                      keyvalue );

            if( layerKids.length == 1 && baseKids.length == 1 )
            {
                return baseKids[ 0 ];
            }
            else
            {
                throw new ConfigurationException( "Unable to merge configuration item, "
                                                  + "multiple matches on child or base [name: "
                                                  + toMerge.getName() + "]" );
            }
        }

        return null;
    }

    private static String getValue( final Configuration layer, final Configuration base )
    {
        try
        {
            return layer.getValue();
        }
        catch( ConfigurationException e )
        {
            return base.getValue( null );
        }
    }

    private static void copyAttributes( final Configuration source,
                                        final DefaultConfiguration dest )
        throws ConfigurationException
    {
        final String[] names = source.getAttributeNames();

        for( int i = 0; i < names.length; i++ )
        {
            if( !names[ i ].startsWith( Constants.MERGE_METADATA_PREFIX ) )
            {
                dest.setAttribute( names[ i ], source.getAttribute( names[ i ] ) );
            }
        }
    }


    /**
     * Return all occurance of a configuration child containing the supplied attribute name.
     * @param config the configuration
     * @param element the name of child elements to select from the configuration
     * @param attribute the attribute name to filter (null will match any attribute name)
     * @return an array of configuration instances matching the query
     */
    public static Configuration[] match( final Configuration config,
                                         final String element,
                                         final String attribute )
    {
        return match( config, element, attribute, null );
    }

    /**
     * Return occurance of a configuration child containing the supplied attribute name and value.
     * @param config the configuration
     * @param element the name of child elements to select from the configuration
     * @param attribute the attribute name to filter (null will match any attribute name )
     * @param value the attribute value to match (null will match any attribute value)
     * @return an array of configuration instances matching the query
     */
    public static Configuration[] match( final Configuration config,
                                         final String element,
                                         final String attribute,
                                         final String value )
    {
        final ArrayList list = new ArrayList();
        final Configuration[] children = config.getChildren( element );

        for( int i = 0; i < children.length; i++ )
        {
            if( null == attribute )
            {
                list.add( children[ i ] );
            }
            else
            {
                String v = children[ i ].getAttribute( attribute, null );

                if( v != null )
                {
                    if( ( value == null ) || v.equals( value ) )
                    {
                        // it's a match
                        list.add( children[ i ] );
                    }
                }
            }
        }

        return (Configuration[])list.toArray( new Configuration[ list.size() ] );
    }
}
