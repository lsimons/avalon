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
package org.apache.avalon.playground.jmxtest;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $
 *
 * @avalon.component name="JMXTestComponent" version="0.1" lifestyle="singleton"
 * @avalon.service type="org.apache.avalon.playground.jmxtest.JMXTestService"
 * @avalon.service type="org.apache.avalon.playground.jmxtest.JMXTestComponentMBean"
 */
public class JMXTestComponent extends AbstractLogEnabled implements JMXTestService, Initializable,
    Startable, JMXTestComponentMBean
{
    private int numberOfServiceInvokes = 0;

    public void initialize()
    {
        getLogger().info( "JMXTextComponent initialized instance: " + System.identityHashCode( this ) );
    }

    public void start()
    {
        getLogger().info( "JMXTextComponent started instance: " + System.identityHashCode( this ) );
    }

    public void stop()
    {
        getLogger().info( "JMXTextComponent stopped instance: " + System.identityHashCode( this ) );
    }

    public void serviceMethod()
    {
        getLogger().info( "serviceMethod was invoked on instance: " + System.identityHashCode( this ) );
        numberOfServiceInvokes++;
    }

    public int getNumberOfServiceInvokes()
    {
        getLogger().info( "getNumberOfServiceInvokes was invoked on instance: "
                          + System.identityHashCode( this ) );
        return numberOfServiceInvokes;
    }

}
