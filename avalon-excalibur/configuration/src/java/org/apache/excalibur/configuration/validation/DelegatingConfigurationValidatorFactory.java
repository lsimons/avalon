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
package org.apache.excalibur.configuration.validation;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * Default ConfigurationValidator implementation that allows schemas to be plugged-in
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public class DelegatingConfigurationValidatorFactory
    extends AbstractLogEnabled
    implements Configurable, Initializable, Disposable, ConfigurationValidatorFactory
{
    private Map m_delegates = new HashMap();
    private String m_supportedTypes;

    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        final Configuration[] delegates = configuration.getChildren( "delegate" );
        final StringBuffer types = new StringBuffer();

        for( int i = 0; i < delegates.length; i++ )
        {
            final String type = delegates[ i ].getAttribute( "schema-type" );
            final DelegateEntry entry =
                new DelegateEntry( type,
                                   delegates[ i ].getAttribute( "class" ),
                                   delegates[ i ] );
            m_delegates.put( type, entry );
            if( i > 0 )
            {
                types.append( "," );
            }

            types.append( type );
        }

        this.m_supportedTypes = types.toString();
    }

    public void initialize()
        throws Exception
    {
        for( Iterator i = m_delegates.values().iterator(); i.hasNext(); )
        {
            final DelegateEntry entry = (DelegateEntry)i.next();
            final Class clazz = Class.forName( entry.getClassName() );
            final ConfigurationValidatorFactory validator =
                (ConfigurationValidatorFactory)clazz.newInstance();

            ContainerUtil.enableLogging( validator, getLogger() );
            ContainerUtil.configure( validator, entry.getConfiguration() );
            ContainerUtil.initialize( validator );

            entry.setValidatorFactory( validator );
        }
    }

    public void dispose()
    {
        for( Iterator i = m_delegates.values().iterator(); i.hasNext(); )
        {
            ContainerUtil.dispose( ( (DelegateEntry)i.next() ).getValidatorFactory() );
        }
    }

    public ConfigurationValidator createValidator( String schemaType, InputStream schema )
        throws ConfigurationException
    {
        final DelegateEntry entry = (DelegateEntry)m_delegates.get( schemaType );
        if( entry == null )
        {
            final String msg = "Invalid schema type: " + schemaType +
                ". Validator only supports: " + m_supportedTypes;
            throw new ConfigurationException( msg );
        }

        return entry.getValidatorFactory().createValidator( schemaType, schema );
    }
}
