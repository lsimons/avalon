/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.configuration.merged;

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
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public class ConfigurationMerger
{
    private static final String MERGE_METADATA_PREFIX = "excalibur-configuration:";

    private static final String MERGE_ATTR = MERGE_METADATA_PREFIX + "merge";
    private static final String KEY_ATTR = MERGE_METADATA_PREFIX + "key-attribute";

    /**
     * Merge two configurations.
     *
     * @param layer Configuration to <i>layer</i> over the base
     * @param base Configuration <i>layer</i> will be merged with
     *
     * @return Result of merge
     *
     * @exception ConfigurationException if
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
            final String name = lc[i].getName();
            final Configuration mergeWith = getMergePartner( lc[i], lc, bc );

            if( null == mergeWith )
            {
                merged.addChild( lc[i] );
            }
            else
            {
                merged.addChild( merge( lc[i], mergeWith ) );

                baseUsed.add( mergeWith );
            }
        }

        for( int i = 0; i < bc.length; i++ )
        {
            if( !baseUsed.contains( bc[i] ) )
            {
                merged.addChild( bc[i] );
            }
        }
    }

    private static Configuration getMergePartner( final Configuration toMerge,
                                                  final Configuration[] layerKids,
                                                  final Configuration[] baseKids )
        throws ConfigurationException
    {
        if( toMerge.getAttributeAsBoolean( MERGE_ATTR, false ) )
        {
            final String keyAttribute = toMerge.getAttribute( KEY_ATTR, null );
            ConfigurationMatcher matcher;

            if( null == keyAttribute )
            {
                matcher = new NamedConfigurationMatcher( toMerge.getName() );
            }
            else
            {
                matcher
                    = new KeyAttributeConfigurationMatcher( toMerge.getName(),
                                                            keyAttribute,
                                                            toMerge.getAttribute( keyAttribute ) );
            }

            final int layerMatch = getMatchingConfiguration( matcher, layerKids );
            final int baseMatch = getMatchingConfiguration( matcher, layerKids );

            if( layerMatch >= 0 && baseMatch >= 0 )
            {
                return baseKids[baseMatch];
            }
        }

        return null;
    }

    private static int getMatchingConfiguration( ConfigurationMatcher matcher,
                                                 Configuration list[] )
    {
        int match = -1;

        for( int i = 0; i < list.length; i++ )
        {
            if( matcher.isMatch( list[i] ) )
            {
                if( match >= 0 )
                {
                    return -1;
                }
                else
                {
                    match = i;
                }
            }
        }

        return match;
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
            if( !names[i].startsWith( MERGE_METADATA_PREFIX ) )
            {
                dest.setAttribute( names[i], source.getAttribute( names[i] ) );
            }
        }
    }
}
