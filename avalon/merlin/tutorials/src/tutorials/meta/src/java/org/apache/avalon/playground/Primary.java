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

package org.apache.avalon.playground;

import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * @avalon.component version="1.3" name="primary-component" lifestyle="singleton"
 * @avalon.service type="org.apache.avalon.playground.PrimaryService" version="9.8"
 */
public class Primary extends AbstractLogEnabled 
  implements PrimaryService, Contextualizable
{
   /**
    * @avalon.entry key="urn:avalon:home" type="java.io.File"
    */
    public void contextualize( Context context ) throws ContextException
    {
        getLogger().info( "ready" );
    }
}