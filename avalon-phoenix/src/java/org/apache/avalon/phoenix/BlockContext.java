/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix;

import java.io.File;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.framework.logger.Logger;

/**
 * Context via which Blocks communicate with container.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public interface BlockContext
    extends Context
{
    String    APP_NAME          = "app.name";
    String    APP_HOME_DIR      = "app.home";
    String    NAME              = "block.name";

    /**
     * Base directory of .sar application.
     *
     * TODO: Should this be getHomeDirectory() or getWorkingDirectory() or other?
     * TODO: Should a Block be able to declare it doesn't use the Filesystem? If
     * it declares this then it would be an error to call this method.
     *
     * @return the base directory
     */
    File getBaseDirectory();

    /**
     * Retrieve name of block.
     *
     * @return the name of block
     */
    String getName();

    /**
     * Retrieve thread pool by category.
     * ThreadPools are given names so that you can manage different thread
     * count to different components.
     *
     * @param category the category
     * @return the ThreadManager
     * @deprecated Use ThreadManager service rather than 
     *             accessing ThreadPool via this method.
     */
    ThreadPool getThreadPool( String category );

    /**
     * Retrieve default thread pool.
     * Equivelent to getThreadPool( "default" );
     *
     * @return the default ThreadPool
     * @deprecated Use ThreadManager service rather than 
     *             accessing ThreadPool via this method.
     */
    ThreadPool getDefaultThreadPool();

    /**
     * Retrieve logger coresponding to named category.
     *
     * TODO: Determine if this is really necessary ?
     *
     * @return the logger
     */
    Logger getLogger( String name );

    /**
     * Retrieve logger coresponding to root category of application.
     *
     * @return the base logger
     * @deprecated Use the getLogger(String) version
     */
    Logger getBaseLogger();

    /**
     * Retrieve the proxy for this object.
     * Each Block is referenced by other Blocks via their Proxy. When Phoenix 
     * shuts down the Block, it can automatically invalidate the proxy. Thus 
     * any attempt to call a method on a "dead"/shutdown object will result in
     * an <code>IllegalStateException</code>. This is desirable as it will 
     * stop objects from using the Block when it is in an invalid state.
     *
     * <p>The proxy also allows Phoenix to associate "Context" information with
     * the object. For instance, a <code>Block</code> may expect to run with a 
     * specific ContextClassLoader set. However if this Block were to be passed
     * to another component that processed the Block in a thread that did not 
     * have the correct context information setup, then the Block could fail
     * to perform as expected. By passing the proxy instead, the correct context
     * information is maintained by Phoenix.</p>
     *
     * <p>Note that only interfaces that the Block declares as offered services
     * will actually be implemented by the proxy.</p>
     */
    //Object getProxy();

    /**
     * This method is similar to <code>getProxy()</code> except that it operates
     * on arbitrary objects. It will in effect proxy all interfaces that the 
     * component supports.
     *
     * <p>Proxying arbitrary objects is useful for the same reason it is useful
     * to proxy the Block. Thus it is recomended that when needed you pass
     * Proxys of objects to minimize the chance of incorrect behaviour.</p>
     */
    //Object getProxy( Object other );

    /**
     * This method generates a Proxy of the specified object using the 
     * specified interfaces. In other respects it is identical to 
     * getProxy( Object other )
     */
    //Object getProxy( Object other, Class[] interfaces );

    /**
     * Retrieve a resource from the SAR file. The specified
     * name is relative the root of the archive. So you could 
     * use it to retrieve a html page from within sar by loading
     * the resource named "data/main.html" or similar.
     */
    //InputStream getResourceAsStream( String name );

    /**
     * This method gives you access to a named ClassLoader. The ClassLoaders
     * for an application are declared in the <code>environment.xml</code>
     * descriptor.
     */
    //ClassLoader getClassLoader( String name );

    /**
     * Retrieve the MBeanServer for this application.
     *
     * NOTE: Unsure if this will ever be implemented
     * may be retrievable via CM instead, or perhaps in
     * a directory or whatever.
     */
    //MBeanServer getMBeanServer();
}
