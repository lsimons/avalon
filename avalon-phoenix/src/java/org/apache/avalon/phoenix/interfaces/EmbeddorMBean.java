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

import java.util.Date;

/**
 * This is the interface via which the Management interface interacts
 * with the Embeddor.
 *
 * @phoenix:mx-topic name="Embeddor"
 *
 * @author Peter Donald
 */
public interface EmbeddorMBean
{
    String ROLE = EmbeddorMBean.class.getName();

    /**
     * Get name by which the server is known.
     * Usually this defaults to "Phoenix" but the admin
     * may assign another name. This is useful when you
     * are managing a cluster of Phoenix servers.
     *
     * @phoenix:mx-attribute
     * @phoenix:mx-description Name by which this server is known.
     *
     * @return the name of server
     */
    String getName();

    /**
     * Get location of Phoenix installation
     *
     * @phoenix:mx-attribute
     *
     * @return the home directory of phoenix
     */
    String getHomeDirectory();

    /**
     * Get the date at which this server started.
     *
     * @phoenix:mx-attribute
     *
     * @return the date at which this server started
     */
    Date getStartTime();

    /**
     * Retrieve the number of millisecond
     * the server has been up.
     *
     * @phoenix:mx-attribute
     *
     * @return the the number of millisecond the server has been up
     */
    long getUpTimeInMillis();

    /**
     * Retrieve a string identifying version of server.
     * Usually looks like "v4.0.1a".
     *
     * @phoenix:mx-attribute
     * @phoenix:mx-description Retrieve a string identifying version of server.
     *
     * @return version string of server.
     */
    String getVersion();

    /**
     * Get a string defining the build.
     * Possibly the date on which it was built, where it was built,
     * with what features it was built and so forth.
     *
     * @phoenix:mx-attribute
     *
     * @return the string describing build
     */
    String getBuild();

    /**
     * Request the Embeddor shutsdown.
     *
     * @phoenix:mx-operation
     */
    void shutdown();

    /**
     * Request the embeddor to restart.
     *
     * @phoenix:mx-operation
     *
     * @throws UnsupportedOperationException if restart not a supported operation
     */
    void restart()
        throws UnsupportedOperationException;
}
