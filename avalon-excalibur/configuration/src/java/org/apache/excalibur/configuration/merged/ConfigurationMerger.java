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
package org.apache.excalibur.configuration.merged;

import java.util.HashSet;
import java.util.Set;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.excalibur.configuration.ConfigurationUtil;

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
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
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
     * @exception ConfigurationException if unable to merge
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

            final Configuration[] layerKids = ConfigurationUtil.match( layer,
                                                                       toMerge.getName(),
                                                                       keyAttribute,
                                                                       keyvalue );

            final Configuration[] baseKids = ConfigurationUtil.match( base,
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
}
