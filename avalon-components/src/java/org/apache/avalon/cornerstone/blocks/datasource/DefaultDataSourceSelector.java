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
 
 4. The names "Jakarta", "Apache Avalon", "Avalon Cornerstone", "Avalon
    Framework" and "Apache Software Foundation"  must not be used to endorse
    or promote products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.
 
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

    public void configure( final Configuration configuration )
    {
        m_configuration = configuration;
    }

    public void initialize()
        throws Exception
    {
        m_dataSources = new HashMap();

        Configuration[] dataSourceConfs = getDataSourceConfig();

        for( int i = 0; i < dataSourceConfs.length; i++ )
        {
            final Configuration dataSourceConf = dataSourceConfs[ i ];

            final String name = dataSourceConf.getAttribute( "name" );
            final String clazz = dataSourceConf.getAttribute( "class" );
            final String driver = dataSourceConf.getChild( "driver", true ).getValue( "" );

            final ClassLoader classLoader =
                Thread.currentThread().getContextClassLoader();

            DataSourceComponent component = null;
            if( null == classLoader )
            {
                if( !"".equals( driver ) )
                {
                    Class.forName( driver, true, Thread.currentThread().getContextClassLoader() );
                }

                component = (DataSourceComponent)Class.forName( clazz ).newInstance();
            }
            else
            {
                if( !"".equals( driver ) )
                {
                    classLoader.loadClass( driver );
                }

                component = (DataSourceComponent)classLoader.loadClass( clazz ).newInstance();
            }

            if( component instanceof LogEnabled )
            {
                setupLogger( component, name );
            }
            component.configure( dataSourceConf );
            m_dataSources.put( name, component );

            if( getLogger().isInfoEnabled() )
            {
                getLogger().info( "DataSource " + name + " ready" );
            }
        }
    }

    private Configuration[] getDataSourceConfig()
    {
        final Configuration head =
            m_configuration.getChild( "data-sources" );
        if( 0 != head.getChildren().length )
        {

            final String message =
                "WARNING: Child node <data-sources/> in " +
                "configuration of component named " + m_blockName +
                " has been deprecated. Please put <data-source/> elements" +
                " in root configuration element";
            getLogger().warn( message );
            System.out.println( message );
            return head.getChildren( "data-source" );
        }
        else
        {
            return m_configuration.getChildren( "data-source" );
        }
    }

    public void dispose()
    {
        final Iterator keys = m_dataSources.keySet().iterator();
        while( keys.hasNext() )
        {
            final DataSourceComponent dsc =
                (DataSourceComponent)m_dataSources.get( keys.next() );
            if( dsc instanceof Disposable )
            {
                ((Disposable)dsc).dispose();
            }
        }
    }

    public boolean hasComponent( final Object hint )
    {
        return m_dataSources.containsKey( hint );
    }

    public Component select( final Object hint )
        throws ComponentException
    {
        final Component component = (Component)m_dataSources.get( hint );

        if( null == component )
        {
            throw new ComponentException( "Unable to provide DataSourceComponent for " + hint );
        }

        return component;
    }

    public void release( final Component component )
    {
        //do nothing
    }

}
