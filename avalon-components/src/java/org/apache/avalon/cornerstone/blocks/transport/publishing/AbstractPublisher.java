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
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
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

/**
 * @phoenix:service name="org.apache.excalibur.altrmi.server.AltrmiPublisher"
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.18 $
 */
public abstract class AbstractPublisher
    extends AbstractLogEnabled
    implements AltrmiPublisher, Startable, Serviceable, Contextualizable, Configurable,
    Initializable
{

    protected AbstractServer m_abstractServer;
    private ClassRetriever m_classRetriever;
    protected AltrmiAuthenticator m_altrmiAuthenticator;
    protected File m_baseDirectory;

    /**
     * Pass the <code>Configuration</code> to the <code>Configurable</code>
     * class. This method must always be called after the constructor
     * and before any other method.
     *
     * @param configuration the class configurations.
     */
    public void configure( Configuration configuration ) throws ConfigurationException
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
        else
        {
            throw new ConfigurationException(
                "classRetrieverType must be 'baseMobileClass', 'jarFile' or 'none'" );
        }
    }

    public void contextualize( final Context context )
    {
        m_baseDirectory = ( (BlockContext)context ).getBaseDirectory();
    }

    /**
     * @phoenix:dependency name="org.apache.excalibur.altrmi.server.AltrmiAuthenticator"
     */
    public void service( ServiceManager manager )
        throws ServiceException
    {
        m_altrmiAuthenticator =
            (AltrmiAuthenticator)manager.lookup( AltrmiAuthenticator.class.getName() );
    }

    /**
     * Initialialize the component. Initialization includes
     * allocating any resources required throughout the
     * components lifecycle.
     *
     * @exception Exception if an error occurs
     */
    public void initialize() throws Exception
    {
        m_abstractServer.setClassRetriever( m_classRetriever );
        m_abstractServer.setAuthenticator( m_altrmiAuthenticator );
    }

    /**
     * Method publish
     *
     *
     * @param implementation
     * @param asName
     * @param interfaceToExpose
     *
     *
     * @throws PublicationException
     *
     */
    public void publish( Object implementation, String asName, Class interfaceToExpose )
        throws PublicationException
    {
        if( getLogger().isDebugEnabled() )
            getLogger().debug( "Publishing object [as: " + asName + ", impl: " + implementation
                              + ", interf: "+ interfaceToExpose + "]" );

        m_abstractServer.publish( implementation, asName, interfaceToExpose );
    }

    /**
     * Method publish
     *
     *
     * @param implementation
     * @param asName
     * @param publicationDescription
     *
     *
     * @throws PublicationException
     *
     */
    public void publish(
        Object implementation, String asName, PublicationDescription publicationDescription )
        throws PublicationException
    {
        if( getLogger().isDebugEnabled() )
            getLogger().debug( "Publishing object [as: " + asName + ", impl: " + implementation + "]" );

        m_abstractServer.publish( implementation, asName, publicationDescription );
    }

    /**
     * Method unPublish
     *
     *
     * @param o
     * @param s
     *
     *
     * @throws PublicationException
     *
     */
    public void unPublish( Object o, String s ) throws PublicationException
    {
        if( getLogger().isDebugEnabled() )
            getLogger().debug( "Unpublishing object [nane: " + s + ", impl: " + o + "]" );

        m_abstractServer.unPublish( o, s );
    }

    /**
     * Method replacePublished
     *
     *
     * @param o
     * @param s
     * @param o1
     *
     *
     * @throws PublicationException
     *
     */
    public void replacePublished( Object o, String s, Object o1 ) throws PublicationException
    {
        if( getLogger().isDebugEnabled() )
            getLogger().debug( "Replacing published object [nane: " + s + ", existing: " + o + ", new: " + o1 + "]" );

        m_abstractServer.replacePublished( o, s, o1 );
    }

    /**
     * Starts the component.
     *
     * @exception Exception if Component can not be started
     */
    public void start() throws Exception
    {
        m_abstractServer.start();
    }

    /**
     * Stops the component.
     *
     * @exception Exception if the Component can not be Stopped.
     */
    public void stop() throws Exception
    {
        m_abstractServer.stop();
    }

    /**
     * Method getMethodInvocationHandler
     *
     *
     * @param request
     * @param s
     *
     * @return
     *
     */
    public MethodInvocationHandler getMethodInvocationHandler( MethodRequest request, String publishedName )
    {
        return m_abstractServer.getMethodInvocationHandler( request, publishedName );
    }

    public MethodInvocationHandler getMethodInvocationHandler(String publishedName)
    {
        return m_abstractServer.getMethodInvocationHandler( publishedName );
    }
}
