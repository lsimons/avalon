/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.avalon.configuration.Configurable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.avalon.logger.AbstractLoggable;

/**
 * Default RoleManager implementation.  It populates the RoleManager
 * from a configuration file.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:ricardo@apache,org">Ricardo Rocha</a>
 * @author <a href="mailto:giacomo@apache,org">Giacomo Pati</a>
 * @version CVS $Revision: 1.1 $ $Date: 2001/04/18 13:16:36 $
 */
public class DefaultRoleManager 
    extends AbstractLoggable 
    implements RoleManager, Configurable
{
    private Map  m_shorthands;
    private Map  m_classNames;
    private Map  m_hintClassNames;

    public final String getRoleForName( final String shorthandName )
    {
        final String role = (String)m_shorthands.get( shorthandName );

        getLogger().debug( "looking up shorthand " + shorthandName + 
                           ", returning " + role );

        return role;
    }

    public final String getDefaultClassNameForRole( final String role )
    {
        return (String)m_classNames.get( role );
    }

    public final String getDefaultClassNameForHint( final String role, 
                                                    final String shorthand )
    {
        getLogger().debug( "looking up hintmap for role " + role );

        final Map hintMap = (Map)m_hintClassNames.get( role );

        if( null == hintMap )
        {
            return "";
        }

        getLogger().debug( "looking up classname for hint " + shorthand );
        return (String)hintMap.get( shorthand );
    }

    public final void configure( final Configuration configuration ) 
        throws ConfigurationException
    {
        final Map shorts = new HashMap();
        final Map classes = new HashMap();
        final Map hintclasses = new HashMap();

        final Configuration[] roles = configuration.getChildren( "role" );

        for( int i = 0; i < roles.length; i++ )
        {
            final String name = roles[ i ].getAttribute( "name" );
            final String shorthand = roles[ i ].getAttribute( "shorthand" );
            final String defaultClassName = 
                roles[ i ].getAttribute( "default-class", null );

            shorts.put( shorthand, name );

            if( null != defaultClassName )
            {
                classes.put( name, defaultClassName );
            }

            final Configuration[] hints = roles[ i ].getChildren( "hint" );
            if( hints.length > 0 )
            {
                HashMap hintMap = new HashMap();

                for( int j = 0; j < hints.length; j++ )
                {
                    final String shortHand = hints[ j ].getAttribute("shorthand").trim();
                    final String className = hints[ j ].getAttribute("class").trim();

                    hintMap.put( shortHand, className );
                    getLogger().debug( "Adding hint type " + shortHand + " associated with role " + 
                                       name + " and class " + className );
                }
                
                hintclasses.put( name, Collections.unmodifiableMap( hintMap ) );
            }

            getLogger().debug( "added Role " + name + " with shorthand " +
                               shorthand + " for " + defaultClassName );
        }

        m_shorthands = Collections.unmodifiableMap( shorts );
        m_classNames = Collections.unmodifiableMap( classes );
        m_hintClassNames = Collections.unmodifiableMap( hintclasses );
    }
}
