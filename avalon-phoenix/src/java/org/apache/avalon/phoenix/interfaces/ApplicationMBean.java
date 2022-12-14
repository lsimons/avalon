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

package org.apache.avalon.phoenix.interfaces;

/**
 * This is the interface via which you can manager
 * the root container of Applications.
 *
 * @phoenix:mx-topic name="Application"
 *
 * @author Peter Donald
 * @author <a href="mailto:leosimons@apache.org">Leo Simons</a>
 */
public interface ApplicationMBean
{
    String ROLE = ApplicationMBean.class.getName();

    /**
     * Get the name of the application.
     *
     * @phoenix:mx-attribute
     *
     * @return the name of the application
     */
    String getName();

    /**
     * Get the name to display in Management UI.
     *
     * @phoenix:mx-attribute
     *
     * @return the name of the application to display in UI
     */
    String getDisplayName();

    /**
     * Get the string used to describe the application in the UI.
     *
     * @phoenix:mx-attribute
     *
     * @return a short description of the application
     */
    String getDescription();

    /**
     * Get location of Application installation
     *
     * @phoenix:mx-attribute
     *
     * @return the home directory of application
     */
    String getHomeDirectory();

    /**
     * Get the names of the blocks that compose this Application
     *
     * @phoenix:mx-attribute
     *
     * @return list of block names
     */
    String[] getBlockNames();

    /**
     * Return true if the application is
     * running or false otherwise.
     *
     * @phoenix:mx-attribute
     *
     * @return true if application is running, false otherwise
     */
    boolean isRunning();

    /**
     * Start the application running.
     * This is only valid when isRunning() returns false,
     * otherwise it will generate an IllegalStateException.
     *
     * @phoenix:mx-operation
     *
     * @throws IllegalStateException if application is already running
     * @throws ApplicationException if the application failed to start.
     *            the message part of exception will contain more information
     *            pertaining to why the application failed to startup
     */
    void start()
        throws IllegalStateException, ApplicationException;

    /**
     * Shutdown and restart the application running.
     * This is only valid when isRunning() returns true,
     * otherwise it will generate an IllegalStateException.
     * This is equivelent to  calling stop() and then start()
     * in succession.
     *
     * @phoenix:mx-operation
     *
     * @throws IllegalStateException if application is not already running
     * @throws ApplicationException if the application failed to stop or start.
     *            the message part of exception will contain more information
     *            pertaining to why the application failed to startup/shutdown
     */
    void restart()
        throws IllegalStateException, ApplicationException;

    /**
     * Stop the application running.
     * This is only valid when isRunning() returns true,
     * otherwise it will generate an IllegalStateException.
     *
     * @phoenix:mx-operation
     *
     * @throws IllegalStateException if application is not already running
     * @throws ApplicationException if the application failed to shutdown.
     *            the message part of exception will contain more information
     *            pertaining to why the application failed to shutodwn
     */
    void stop()
        throws IllegalStateException, ApplicationException;
}
