/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.transport.publishing;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.excalibur.altrmi.common.MethodRequest;
import org.apache.excalibur.altrmi.server.AltrmiAuthenticator;
import org.apache.excalibur.altrmi.server.AltrmiPublisher;
import org.apache.excalibur.altrmi.server.ClassRetriever;
import org.apache.excalibur.altrmi.server.MethodInvocationHandler;
import org.apache.excalibur.altrmi.server.PublicationDescription;
import org.apache.excalibur.altrmi.server.PublicationException;
import org.apache.excalibur.altrmi.server.impl.AbstractServer;
import org.apache.excalibur.altrmi.server.impl.classretrievers.JarFileClassRetriever;
import org.apache.excalibur.altrmi.server.impl.classretrievers.NoClassRetriever;
import org.apache.excalibur.altrmi.server.impl.classretrievers.AbstractDynamicGeneratorClassRetriever;
import org.apache.excalibur.altrmi.server.impl.classretrievers.BcelDynamicGeneratorClassRetriever;

/**
 * Abstract Publisher.
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @author Thomas Kiesgen
 * @version $Revision: 1.20 $
 */
public abstract class AbstractPublisher
    extends AbstractLogEnabled
    implements AltrmiPublisher, Startable, Serviceable, Contextualizable, Configurable,
    Initializable
{
    private AbstractServer m_abstractServer;
    private ClassRetriever m_classRetriever;
    private AltrmiAuthenticator m_altrmiAuthenticator;
    protected File m_baseDirectory;
    private boolean m_isDynamicPublisher = false;

    /**
     *
     * @param configuration
     * @throws ConfigurationException
     */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        String classRetrieverType = configuration.getChild( "classRetrieverType" ).getValue();

        if( classRetrieverType.equals( "jarFile" ) )
        {
            StringTokenizer st =
                new StringTokenizer( configuration.getChild( "gerneratedClassJarURLs" ).getValue(),
                                     "," );
            Vector vector = new Vector();

            while( st.hasMoreTokens() )
            {
                try
                {
                    String url = st.nextToken();

                    if( url.startsWith( "./" ) )
                    {
                        File file = new File( m_baseDirectory, url.substring( 2, url.length() ) );

                        vector.add( file.toURL() );
                    }
                    else
                    {
                        vector.add( new URL( url ) );
                    }
                }
                catch( MalformedURLException e )
                {
                    getLogger()
                        .debug( "Can't create URL from config element 'gerneratedClassJarURLs'",
                                e );
                }
            }

            URL[] urls = new URL[ vector.size() ];

            vector.copyInto( urls );

            m_classRetriever = new JarFileClassRetriever( urls );
        }
        else if( classRetrieverType.equals( "none" ) )
        {
            m_classRetriever = new NoClassRetriever();
        }
        else if( classRetrieverType.equals( "bcel" ) )
        {
            AbstractDynamicGeneratorClassRetriever generator = new BcelDynamicGeneratorClassRetriever();
            File classGenDir = new File( m_baseDirectory, configuration.getChild( "classGenDir" ).getValue( "" ) );
            generator.setClassGenDir( classGenDir.getAbsolutePath() );
            m_classRetriever = generator;

            m_isDynamicPublisher = true;
            getLogger().debug( "setting classgen dir for generator to " + classGenDir.getAbsolutePath() );
            getLogger().debug( "setting class retriever to bcel dynamic generator" );
        }


        else
        {
            throw new ConfigurationException(
                "classRetrieverType must be 'bcel', 'jarFile' or 'none'" );
        }
    }

    /**
     * contextualize as per Contextualizable interface
     * @param context
     */
    public void contextualize( final Context context )
    {
        m_baseDirectory = ( (BlockContext)context ).getBaseDirectory();
    }

    /**
     * Service as per Serviceable interface
     * @param manager a service manager
     * @throws ServiceException if a problem during servicing
     * @phoenix:dependency name="org.apache.excalibur.altrmi.server.AltrmiAuthenticator"
     */
    public void service( ServiceManager manager )
        throws ServiceException
    {
        m_altrmiAuthenticator =
            (AltrmiAuthenticator)manager.lookup( AltrmiAuthenticator.class.getName() );
    }

    /**
     * initialize as per Initializable interface
     * @throws Exception
     */
    public void initialize() throws Exception
    {
        m_abstractServer.setClassRetriever( m_classRetriever );
        m_abstractServer.setAuthenticator( m_altrmiAuthenticator );
    }

    /**
     *
     * @param implementation
     * @param asName
     * @param interfaceToExpose
     * @throws PublicationException
     */
    public void publish( Object implementation, String asName, Class interfaceToExpose )
        throws PublicationException
    {
        if( getLogger().isDebugEnabled() )
            getLogger().debug( "Publishing object [as: " + asName + ", impl: " + implementation
                              + ", interf: "+ interfaceToExpose + "]" );

        if( m_isDynamicPublisher )
        {
            ( ( AbstractDynamicGeneratorClassRetriever ) m_classRetriever ).generate( asName, interfaceToExpose, this.getClass().getClassLoader() );
                if( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "generated dynamic proxy for published interface " + asName );
                }
        }

        m_abstractServer.publish( implementation, asName, interfaceToExpose );
    }

    /**
     * Publish an service
     * @param implementation
     * @param asName
     * @param publicationDescription
     * @throws PublicationException
     */
    public void publish(
        Object implementation, String asName, PublicationDescription publicationDescription )
        throws PublicationException
    {
        if( getLogger().isDebugEnabled() )
            getLogger().debug( "Publishing object [as: " + asName + ", impl: " + implementation + "]" );

        if( m_isDynamicPublisher )
        {
            ( ( AbstractDynamicGeneratorClassRetriever ) m_classRetriever ).generate( asName, publicationDescription, this.getClass().getClassLoader() );
                if( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "generated dynamic proxy for published interface " + asName );
                }
        }



        m_abstractServer.publish( implementation, asName, publicationDescription );
    }

    /**
     *
     * @param object
     * @param asName
     * @throws PublicationException
     */
    public void unPublish( Object object, String asName ) throws PublicationException
    {
        if( getLogger().isDebugEnabled() )
            getLogger().debug( "Unpublishing object [nane: " + asName + ", impl: " + object + "]" );

        m_abstractServer.unPublish( object, asName );
    }

    /**
     *
     * @param object
     * @param asName
     * @param o1
     * @throws PublicationException
     */
    public void replacePublished( Object object, String asName, Object o1 ) throws PublicationException
    {
        if( getLogger().isDebugEnabled() )
            getLogger().debug( "Replacing published object [nane: " + asName + ", existing: " + object + ", new: " + o1 + "]" );

        m_abstractServer.replacePublished( object, asName, o1 );
    }

    /**
     *
     * @throws Exception
     */
    public void start() throws Exception
    {
        m_abstractServer.start();
    }

    /**
     *
     * @throws Exception
     */
    public void stop() throws Exception
    {
        m_abstractServer.stop();
    }

    /**
     *
     * @param request
     * @param publishedName
     * @return
     */
    public MethodInvocationHandler getMethodInvocationHandler( MethodRequest request, String publishedName )
    {
        return m_abstractServer.getMethodInvocationHandler( request, publishedName );
    }

    /**
     *
     * @param publishedName
     * @return
     */
    public MethodInvocationHandler getMethodInvocationHandler(String publishedName)
    {
        return m_abstractServer.getMethodInvocationHandler( publishedName );
    }

    /**
     *
     * @return
     */
    protected AbstractServer getAbstractServer()
    {
        return m_abstractServer;
    }

    /**
     *
     * @param abstractServer
     */
    protected void setAbstractServer( AbstractServer abstractServer )
    {
        m_abstractServer = abstractServer;
    }
}
