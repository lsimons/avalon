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

package org.apache.avalon.activation.appliance;

import java.io.Serializable;
import java.net.URL;

/**
 * An appliance event.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/01/24 23:25:20 $
 */
public class ApplianceEvent implements Serializable
{
    //------------------------------------------------------------------
    // static
    //------------------------------------------------------------------

    public static final int COMMISSIONED = 0;
    public static final int DECOMMISSIONED = 4;

    //------------------------------------------------------------------
    // immutable state
    //------------------------------------------------------------------

    private final URL m_url;
    private final int m_state;

    //------------------------------------------------------------------
    // constructor
    //------------------------------------------------------------------

    public ApplianceEvent( URL url, int state )
    {
       m_url = url;
       m_state = state;
    }

    //------------------------------------------------------------------
    // implementation
    //------------------------------------------------------------------

   /**
    * Get the state of the appliance.
    *
    * @return the state value
    */
    public int getState()
    {
        return m_state;
    }

   /**
    * Get the appliance url.
    *
    * @return the appliance url
    */
    public URL getURL()
    {
        return m_url;
    }
}

