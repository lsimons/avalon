/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.classloader;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.security.Policy;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.phoenix.components.util.ResourceUtil;
import org.apache.excalibur.policy.builder.PolicyResolver;

/**
 * A basic resolver that resolves Phoenix specific features.
 * (like remapping URLs).
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/09/22 08:52:13 $
 */
class SarPolicyResolver
    extends AbstractLogEnabled
    implements PolicyResolver
{
    private final static Resources REZ =
        ResourceManager.getPackageResources( SarPolicyResolver.class );

    private final File m_baseDirectory;
    private final File m_workDirectory;
    private final DefaultContext m_context;

    SarPolicyResolver( final File baseDirectory,
                       final File workDirectory )
    {
        final HashMap map = new HashMap();
        map.putAll( System.getProperties() );
        m_context = new DefaultContext( map );
        m_context.put( "/", File.separator );
        m_context.put( "app.home", baseDirectory );
        m_workDirectory = workDirectory;
        m_baseDirectory = baseDirectory;
    }

    public Policy createPolicy( Map grants )
        throws Exception
    {
        final SarPolicy sarPolicy = new SarPolicy( grants );
        ContainerUtil.enableLogging( sarPolicy, getLogger() );
        ContainerUtil.initialize( sarPolicy );
        return sarPolicy;
    }

    public URL resolveLocation( String location )
        throws Exception
    {
        if( null == location )
        {
            return null;
        }
        else
        {
            location = expand( location );
            location = ResourceUtil.expandSarURL( location,
                                                  m_baseDirectory,
                                                  m_workDirectory );
            return new URL( location );
        }
    }

    public String resolveTarget( String target )
    {
        try
        {
            return expand( target );
        }
        catch( Exception e )
        {
            final String message = "Error resolving: " + target;
            getLogger().warn( message, e );
            return target;
        }
    }

    private String expand( final String value )
        throws Exception
    {
        try
        {
            final Object resolvedValue =
                PropertyUtil.resolveProperty( value, m_context, false );
            return resolvedValue.toString();
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "policy.error.property.resolve", value );
            throw new Exception( message, e );
        }
    }
}
