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

public class DelegatingConfigurationValidator extends AbstractLogEnabled
    implements Configurable, Initializable, Disposable,
    ConfigurationValidator, ConfigurationValidatorMBean
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DelegatingConfigurationValidator.class );

    private final Map m_blockTypeMap = Collections.synchronizedMap( new HashMap() );

    private final Map m_delegates = new HashMap();

    private String m_supportedTypes;

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration[] delegates = configuration.getChildren( "delegate" );
        final StringBuffer types = new StringBuffer();

        for( int i = 0; i < delegates.length; i++ )
        {
            final String type = delegates[ i ].getAttribute( "schema-type" );

            this.m_delegates.put( type,
                                  new DelegateEntry( type,
                                                     delegates[ i ].getAttribute( "class" ),
                                                     delegates[ i ] )
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
            final ConfigurationValidator validator = (ConfigurationValidator)clazz.newInstance();

            ContainerUtil.enableLogging( validator, getLogger() );
            ContainerUtil.configure( validator, entry.getConfiguration() );
            ContainerUtil.initialize( validator );

            entry.setValidator( validator );
        }
    }

    public void dispose()
    {
        for( Iterator i = m_delegates.values().iterator(); i.hasNext(); )
        {
            ContainerUtil.dispose( ( (DelegateEntry)i.next() ).getValidator() );
        }
    }

    public void addSchema( final String application, final String block, final String schemaType, final String url )
        throws ConfigurationException
    {
        final DelegateEntry entry = (DelegateEntry)this.m_delegates.get( schemaType );

        if( entry == null )
        {
            final String msg = REZ.getString( "jarv.error.badtype",
                                              schemaType,
                                              this.m_supportedTypes );

            throw new ConfigurationException( msg );
        }

        entry.getValidator().addSchema( application, block, schemaType, url );
        this.m_blockTypeMap.put( createKey( application, block ), schemaType );
    }

    public boolean isFeasiblyValid( final String application, final String block, final Configuration configuration )
        throws ConfigurationException
    {
        final ConfigurationValidator delegate = getDelegate( application, block );

        return delegate.isFeasiblyValid( application, block, configuration );
    }

    public boolean isValid( final String application, final String block, final Configuration configuration )
        throws ConfigurationException
    {
        final ConfigurationValidator delegate = getDelegate( application, block );

        return delegate.isValid( application, block, configuration );
    }

    public void removeSchema( final String application, final String block )
    {
        final String type = (String)m_blockTypeMap.get( createKey( application, block ) );

        if( null != type )
        {
            final DelegateEntry entry = (DelegateEntry)m_delegates.get( type );

            entry.getValidator().removeSchema( application, block );
        }
    }

    private ConfigurationValidator getDelegate( final String application, final String block )
        throws ConfigurationException
    {
        final String type = (String)this.m_blockTypeMap.get( createKey( application, block ) );

        if( null == type )
        {
            final String msg = REZ.getString( "jarv.error.noschema", application, block );

            throw new ConfigurationException( msg );
        }

        return ( (DelegateEntry)this.m_delegates.get( type ) ).getValidator();
    }

    private String createKey( final String application, final String block )
    {
        return application + "." + block;
    }

    public String getSchema( final String application, final String block )
    {
        final String type = (String)m_blockTypeMap.get( createKey( application, block ) );

        if( null != type )
        {
            final DelegateEntry entry = (DelegateEntry)m_delegates.get( type );
            final ConfigurationValidator validator = entry.getValidator();

            if( validator instanceof ConfigurationValidatorMBean )
            {
                return ( (ConfigurationValidatorMBean)validator ).getSchema( application,
                                                                             block );

            }
        }

        return null;
    }

    public String getSchemaType( final String application, final String block )
    {
        return (String)this.m_blockTypeMap.get( createKey( application, block ) );
    }
}
