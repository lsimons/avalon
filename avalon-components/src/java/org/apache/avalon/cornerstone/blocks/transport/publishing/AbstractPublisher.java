
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.transport.publishing;



import org.apache.commons.altrmi.server.AltrmiPublisher;
import org.apache.commons.altrmi.server.PublicationException;
import org.apache.commons.altrmi.server.AltrmiServer;
import org.apache.commons.altrmi.server.ClassRetriever;
import org.apache.commons.altrmi.server.AltrmiAuthenticator;
import org.apache.commons.altrmi.server.impl.classretrievers.JarFileClassRetriever;
import org.apache.commons.altrmi.server.impl.classretrievers.NoClassRetriever;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.cornerstone.services.sockets.SocketManager;

import java.util.StringTokenizer;
import java.util.Vector;

import java.net.MalformedURLException;


/**
 * Class AbstractPublisher
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.7 $
 */
public abstract class AbstractPublisher extends AbstractLogEnabled
        implements AltrmiPublisher, Startable, Composable, Configurable, Initializable, Block
{

    protected AltrmiServer m_AltrmiServer;
    private ClassRetriever m_ClassRetriever;
    protected AltrmiAuthenticator m_AltrmiAuthenticator;

    /**
     * Pass the <code>Configuration</code> to the <code>Configurable</code>
     * class. This method must always be called after the constructor
     * and before any other method.
     *
     * @param configuration the class configurations.
     */
    public void configure(Configuration configuration) throws ConfigurationException
    {

        String classRetrieverType = configuration.getChild("classRetrieverType").getValue();

        if (classRetrieverType.equals("jarFile"))
        {
            StringTokenizer st =
                new StringTokenizer(configuration.getChild("gerneratedClassJarURLs").getValue(),
                                    ",");
            Vector vector = new Vector();

            while (st.hasMoreTokens())
            {
                vector.add(st.nextToken());
            }

            String[] urls = new String[vector.size()];

            vector.copyInto(urls);

            try
            {
                m_ClassRetriever = new JarFileClassRetriever(urls);
            }
            catch (MalformedURLException mufe)
            {
                throw new ConfigurationException("URL Invalid", mufe);
            }
        }
        else if (classRetrieverType.equals("none"))
        {
            m_ClassRetriever = new NoClassRetriever();
        }
        else
        {
            throw new ConfigurationException(
                "classRetrieverType must be 'baseMobileClass', 'jarFile' or 'none'");
        }
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
    public void compose(ComponentManager manager) throws ComponentException
    {
        m_AltrmiAuthenticator =
            (AltrmiAuthenticator) manager.lookup(AltrmiAuthenticator.class.getName());
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
        m_AltrmiServer.setClassRetriever(m_ClassRetriever);
        m_AltrmiServer.setAuthenticator(m_AltrmiAuthenticator);
    }

    /**
     * Method publish
     *
     *
     * @param o
     * @param s
     * @param aClass
     *
     * @throws AltrmiPublicationException
     *
     */
    public void publish(Object o, String s, Class aClass) throws PublicationException
    {
        m_AltrmiServer.publish(o, s, aClass);
    }

    /**
     * Method publish
     *
     *
     * @param o
     * @param s
     * @param aClass
     * @param aClass1
     *
     * @throws AltrmiPublicationException
     *
     */
    public void publish(Object o, String s, Class aClass, Class aClass1)
            throws PublicationException
    {
        m_AltrmiServer.publish(o, s, aClass, aClass1);
    }

    /**
     * Method publish
     *
     *
     * @param o
     * @param s
     * @param aClass
     * @param classes
     *
     * @throws AltrmiPublicationException
     *
     */
    public void publish(Object o, String s, Class aClass, Class[] classes)
            throws PublicationException
    {
        m_AltrmiServer.publish(o, s, aClass, classes);
    }

    /**
     * Method publish
     *
     *
     * @param o
     * @param s
     * @param classes
     *
     * @throws AltrmiPublicationException
     *
     */
    public void publish(Object o, String s, Class[] classes) throws PublicationException
    {
        m_AltrmiServer.publish(o, s, classes);
    }

    /**
     * Method publish
     *
     *
     * @param o
     * @param s
     * @param classes
     * @param classes1
     *
     * @throws AltrmiPublicationException
     *
     */
    public void publish(Object o, String s, Class[] classes, Class[] classes1)
            throws PublicationException
    {
        m_AltrmiServer.publish(o, s, classes, classes1);
    }

    /**
     * Method unPublish
     *
     *
     * @param o
     * @param s
     *
     * @throws AltrmiPublicationException
     *
     */
    public void unPublish(Object o, String s) throws PublicationException
    {
        m_AltrmiServer.unPublish(o, s);
    }

    /**
     * Method replacePublished
     *
     *
     * @param o
     * @param s
     * @param o1
     *
     * @throws AltrmiPublicationException
     *
     */
    public void replacePublished(Object o, String s, Object o1) throws PublicationException
    {
        m_AltrmiServer.replacePublished(o, s, o1);
    }

    /**
     * Starts the component.
     *
     * @exception Exception if Component can not be started
     */
    public void start() throws Exception
    {
        m_AltrmiServer.start();
    }

    /**
     * Stops the component.
     *
     * @exception Exception if the Component can not be Stopped.
     */
    public void stop() throws Exception
    {
        m_AltrmiServer.stop();
    }
}
