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

package org.apache.avalon.phoenix.framework.tools.infobuilder;

import org.apache.avalon.phoenix.framework.info.Attribute;
import org.apache.avalon.phoenix.framework.info.ContextDescriptor;
import org.apache.avalon.phoenix.framework.info.EntryDescriptor;
import org.apache.avalon.phoenix.framework.info.FeatureDescriptor;
import org.apache.avalon.phoenix.framework.info.ServiceDescriptor;
import org.apache.avalon.phoenix.framework.info.ComponentInfo;
import org.apache.avalon.phoenix.framework.info.LoggerDescriptor;
import org.apache.avalon.phoenix.framework.info.DependencyDescriptor;
import org.apache.avalon.phoenix.framework.info.ComponentDescriptor;
import org.apache.avalon.framework.Version;
import java.util.Properties;

/**
 * This is a set of constants and utility methods
 * to enablesupport of Legacy BlockInfo files.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003/03/22 12:07:13 $
 */
public class LegacyUtil
{
    public static final String MX_ATTRIBUTE_NAME = "phoenix:mx";
    public static final Attribute MX_ATTRIBUTE = new Attribute( MX_ATTRIBUTE_NAME, null );
    public static final String VERSION_ATTRIBUTE_NAME = "phoenix:version";
    public static final String VERSION_ATTRIBUTE_PARAMETER = "version";
    public static final ContextDescriptor CONTEXT_DESCRIPTOR =
        new ContextDescriptor( "org.apache.avalon.phoenix.BlockContext",
                               EntryDescriptor.EMPTY_SET,
                               Attribute.EMPTY_SET );

    private LegacyUtil()
    {
    }

    /**
     * Return the version specified (if any) for feature.
     *
     * @param type the type
     * @return the translated schema type
     */
    public static String translateToSchemaUri( final String type )
    {
        if( type.equals( "relax-ng" ) )
        {
            return "http://relaxng.org/ns/structure/1.0";
        }
        else
        {
            return type;
        }
    }

    /**
     * Return the version specified (if any) for feature.
     *
     * @param feature the feature
     * @return the version string
     */
    public static String getVersionString( final FeatureDescriptor feature )
    {
        final Attribute tag = feature.getAttribute( "avalon" );
        if( null != tag )
        {
            return tag.getParameter( "version" );
        }
        return null;
    }

    public static Attribute createVersionAttribute( final String version )
    {
        final Properties parameters = new Properties();
        parameters.setProperty( VERSION_ATTRIBUTE_PARAMETER, version );
        return new Attribute( VERSION_ATTRIBUTE_NAME, parameters );
    }

    /**
     * Return true if specified service is a management service.
     *
     * @param service the service
     * @return true if specified service is a management service, false otherwise.
     */
    public static boolean isMxService( final ServiceDescriptor service )
    {
        final Attribute tag = service.getAttribute( MX_ATTRIBUTE_NAME );
        return null != tag;
    }

    /**
     * Create a version for a feature. Defaults to 1.0 if not specified.
     *
     * @param feature the feature
     * @return the Version object
     */
    public static Version toVersion( final FeatureDescriptor feature )
    {
        final String version = getVersionString( feature );
        if( null == version )
        {
            return new Version( 1, 0, 0 );
        }
        else
        {
            return Version.getVersion( version );
        }
    }

    /**
     * Create a {@link ComponentInfo} for a Listener with specified classname.
     *
     * @param implementationKey the classname of listener
     * @return the ComponentInfo for listener
     */
    public static ComponentInfo createListenerInfo( final String implementationKey )
    {
        final ComponentDescriptor descriptor =
            new ComponentDescriptor( implementationKey, Attribute.EMPTY_SET );
        return new ComponentInfo( descriptor,
                                  ServiceDescriptor.EMPTY_SET,
                                  LoggerDescriptor.EMPTY_SET,
                                  ContextDescriptor.EMPTY_CONTEXT,
                                  DependencyDescriptor.EMPTY_SET,
                                  null,
                                  null );
    }
}
