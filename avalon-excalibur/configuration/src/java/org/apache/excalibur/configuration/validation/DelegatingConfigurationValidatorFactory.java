/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2002 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.excalibur.configuration.validation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.InputStream;

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
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public class DelegatingConfigurationValidatorFactory extends AbstractLogEnabled
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
            final String type = delegates[i].getAttribute( "schema-type" );

            this.m_delegates.put( type,
                                  new DelegateEntry( type,
                                                     delegates[i].getAttribute( "class" ),
                                                     delegates[i] )
            );

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
        final DelegateEntry entry = (DelegateEntry)this.m_delegates.get( schemaType );

        if( entry == null )
        {
            final String msg = "Invalid schema type: " + schemaType
                + ". Validator only supports: " + m_supportedTypes;

            throw new ConfigurationException( msg );
        }

        return entry.getValidatorFactory().createValidator( schemaType, schema );
    }
}
