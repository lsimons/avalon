/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix;

/**
 * Implementations of this interface receive notifications about
 * changes to the state of Application.
 * The implementation <em>must</em> have a zero argument
 * constructor and is instantiated before any other component of the Server
 * Application. To receive notification events, the implementation class
 * should be specified in the <code>assembly.xml</code> descriptor.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public interface ApplicationListener extends BlockListener
{

    /**
     * Notification that an application is being started.
     *
     * @param event the ApplicationEvent
     *
     */
    void applicationStarting( ApplicationEvent applicationEvent ) throws Exception;

    /**
     * Notification that an application has now started.
     *
     *
     */
    void applicationStarted();

    /**
     * Notification that an application is being stopped.
     *
     *
     */
    void applicationStopping();

    /**
     * Notification that an application has stopped.
     *
     *
     */
    void applicationStopped();

    /**
     * Notification that an application has failed at some moment.
     * This is for information only as Phoenix will do the right
     * thing for correct shutdown both before and after this method
     * is called.  The user of this method should NOT call System.exit()
     *
     *
     * @param causeOfFailure
     *
     */
    void applicationFailure( Exception causeOfFailure );
}
