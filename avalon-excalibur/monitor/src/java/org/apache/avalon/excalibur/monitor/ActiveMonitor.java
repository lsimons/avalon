/* 
 * Copyright 2002-2004 The Apache Software Foundation
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
package org.apache.avalon.excalibur.monitor;

import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * The ActiveMonitor is used to actively check a set of resources to see if they have
 * changed.  It will be implemented as a Component, that can be retrieved from
 * the ComponentLocator.  It defaults to checking every 1 minute.  The configuration
 * looks like this:
 *
 * <pre>
 *   &lt;active-monitor&gt;
 *     &lt;thread priority="<i>5</i>" frequency="<i>60000</i>"/&gt;
 *     &lt;init-resources&gt;
 *       &lt;-- This entry can be repeated for every resource you want to register immediately --&gt;
 *
 *       &lt;resource key="<i>file:./myfile.html</i>" class="<i>org.apache.avalon.excalibur.monitor.FileResource</i>"/&gt;
 *     &lt;/init-resources&gt;
 *   &lt;/active-monitor&gt;
 * </pre>
 *
 * @avalon.component name="active-monitor" lifestyle="singleton"
 * @avalon.service type="org.apache.avalon.excalibur.monitor.Monitor" version="1.0"
 * @x-avalon.info name=active-monitor
 * @x-avalon.lifestyle type=singleton
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: ActiveMonitor.java,v 1.4 2004/02/28 11:47:32 cziegeler Exp $
 */
public class ActiveMonitor
    extends org.apache.avalon.excalibur.monitor.impl.ActiveMonitor
    implements LogEnabled, Configurable, Startable, ThreadSafe
{
    private Logger m_logger;

    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
    }

    /**
     * Configure the ActiveMonitor.
     */
    public final void configure( final Configuration config )
        throws ConfigurationException
    {
        final Configuration thread = config.getChild( "thread" );
        final long frequency =
            thread.getAttributeAsLong( "frequency", 1000L * 60L );
        final int priority =
            thread.getAttributeAsInteger( "priority", Thread.MIN_PRIORITY );

        setFrequency( frequency );
        setPriority( priority );

        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "Active monitor will sample all resources every " +
                            frequency + " milliseconds with a thread priority of " +
                            priority + "(Minimum = " + Thread.MIN_PRIORITY +
                            ", Normal = " + Thread.NORM_PRIORITY +
                            ", Maximum = " + Thread.MAX_PRIORITY + ")." );
        }

        final Configuration[] resourcesConfig =
            config.getChild( "init-resources" ).getChildren( "resource" );
        final Resource[] resources =
            MonitorUtil.configureResources( resourcesConfig, m_logger );
        addResources( resources );
    }
}
