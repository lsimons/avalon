/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

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

package org.apache.avalon.phoenix.components.classloader;

import java.io.File;
import java.net.URL;
import java.security.Policy;
import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.CascadingException;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.phoenix.components.util.PropertyUtil;
import org.apache.avalon.phoenix.components.util.ResourceUtil;
import org.apache.excalibur.policy.builder.PolicyResolver;

/**
 * A basic resolver that resolves Phoenix specific features.
 * (like remapping URLs).
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.9 $ $Date: 2003/04/05 04:25:42 $
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
        //m_context.put( BlockContext.APP_NAME, sarName );
        m_context.put( BlockContext.APP_HOME_DIR, baseDirectory );
        m_workDirectory = workDirectory;
        m_baseDirectory = baseDirectory;
    }

    public Policy createPolicy( final Map grants )
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

    public String resolveTarget( final String target )
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
            throw new CascadingException( message, e );
        }
    }
}
