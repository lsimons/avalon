/* 
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.metro.transit;

import java.net.URL;
import java.io.File;
import java.util.jar.Manifest;


/**
 * The initial context established by an initial repository factory.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: InitialContext.java 30977 2004-07-30 08:57:54Z niclas $
 */
public interface InitialContext
{
    //------------------------------------------------------------------
    // static 
    //------------------------------------------------------------------

    static final String GROUP = "metro";

    static final String HOME_SYMBOL =        GROUP.toUpperCase() + "_HOME";
    static final String HOME_KEY =           GROUP + ".home";

    static final String CACHE_KEY =          GROUP + ".initial.cache";
    static final String HOSTS_KEY =          GROUP + ".initial.hosts";
    static final String ONLINE_KEY =         GROUP + ".initial.online";
    static final String POLICY_KEY =         GROUP + ".initial.policy";
    static final String TIMESTAMP_KEY =      GROUP + ".initial.timestamp";
    static final String SNAPSHOT_KEY =       GROUP + ".initial.snapshot";
    static final String DEBUG_KEY =          GROUP + ".initial.debug";
    static final String CONTROLLER_KEY =     GROUP + ".initial.artifact";

    static final String PROXY_HOST_KEY =     GROUP + ".proxy.host";
    static final String PROXY_PORT_KEY =     GROUP + ".proxy.port";
    static final String PROXY_USERNAME_KEY = GROUP + ".proxy.username";
    static final String PROXY_PASSWORD_KEY = GROUP + ".proxy.password";

    static final String PROPERTY_FILENAME =  GROUP + ".properties";

    static final int NULL_POLICY = -1;
    static final int FAST_POLICY = 0;
    static final int SNAPSHOT_POLICY = 1;
    static final int TIMESTAMP_POLICY = 2;
    static final int RELOAD_POLICY = 3;

    static final InitialContextFactory FACTORY = new InitialContextFactory();

    /**
     * Return the online mode.
     * 
     * @return the online mode
     */
    boolean getOnlineMode();

   /**
    * Get the timestamp policy.
    *
    * @return the timestamp policy
    */
    Policy getTimestampPolicy();

    /**
     * Return the debug policy.
     * 
     * @return the debug policy
     */
    boolean getDebugPolicy();

    /**
     * Return cache root directory.
     * 
     * @return the cache directory
     */
    File getCacheDirectory();

    /**
     * Return the initial set of host names.
     * @return the host names sequence
     */
    String[] getHosts();

    /**
     * Create a return a new monitor.
     * @return the monitor
     */
    Monitor getMonitor();

}
