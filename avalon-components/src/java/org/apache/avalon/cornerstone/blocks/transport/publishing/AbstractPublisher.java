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
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.commons.altrmi.common.MethodRequest;
import org.apache.commons.altrmi.server.AltrmiAuthenticator;
import org.apache.commons.altrmi.server.AltrmiPublisher;
import org.apache.commons.altrmi.server.ClassRetriever;
import org.apache.commons.altrmi.server.MethodInvocationHandler;
import org.apache.commons.altrmi.server.PublicationDescription;
import org.apache.commons.altrmi.server.PublicationException;
import org.apache.commons.altrmi.server.impl.AbstractServer;
import org.apache.commons.altrmi.server.impl.classretrievers.JarFileClassRetriever;
import org.apache.commons.altrmi.server.impl.classretrievers.NoClassRetriever;

/**
 * Class AbstractPublisher
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.12 $
 */
public abstract class AbstractPublisher extends AbstractLogEnabled
    implements AltrmiPublisher, Startable, Composable, Contextualizable, Configurable,
    Initializable, Block
{

    protected AbstractServer m_AbstractServer;
    private ClassRetriever m_ClassRetriever;
    protected AltrmiAuthenticator m_AltrmiAuthenticator;
    protected File mBaseDirectory;

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
                        File file = new File( mBaseDirectory, url.substring( 2, url.length() ) );

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

            m_ClassRetriever = new JarFileClassRetriever( urls );
        }
        else if( classRetrieverType.equals( "none" ) )
        {
            m_ClassRetriever = new NoClassRetriever();
        }
        else
        {
            throw new ConfigurationException(
                "classRetrieverType must be 'baseMobileClass', 'jarFile' or 'none'" );
        }
    }

    /**
     * Method contextualize
     *
     *
     *
     * @param context
     *
     */
    public void contextualize( final Context context )
    {
        mBaseDirectory = ( (BlockContext)context ).getBaseDirectory();
    }

    /**
     * Method compose
     *
     *
     * @param manager
     *
     * @throws ComponentException
     *
     */
    public void compose( ComponentManager manager ) throws ComponentException
    {
        m_AltrmiAuthenticator =
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
        m_AbstractServer.setClassRetriever( m_ClassRetriever );
        m_AbstractServer.setAuthenticator( m_AltrmiAuthenticator );
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
        m_AbstractServer.publish( implementation, asName, interfaceToExpose );
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
        m_AbstractServer.publish( implementation, asName, publicationDescription );
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
        m_AbstractServer.unPublish( o, s );
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
        m_AbstractServer.replacePublished( o, s, o1 );
    }

    /**
     * Starts the component.
     *
     * @exception Exception if Component can not be started
     */
    public void start() throws Exception
    {
        m_AbstractServer.start();
    }

    /**
     * Stops the component.
     *
     * @exception Exception if the Component can not be Stopped.
     */
    public void stop() throws Exception
    {
        m_AbstractServer.stop();
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
    public MethodInvocationHandler getMethodInvocationHandler( MethodRequest request, String s )
    {
        return m_AbstractServer.getMethodInvocationHandler( request, s );
    }
}
