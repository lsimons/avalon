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

package org.apache.avalon.composition.model.test.dynamics;

import org.apache.avalon.framework.logger.Logger;

/**
 * A component that implements the Gizmo service.
 *
 * @avalon.component name="widget" lifestyle="singleton"
 * @avalon.service type="org.apache.avalon.composition.model.test.dynamics.Widget"
 */
public class DefaultWidget implements Widget
{
   //---------------------------------------------------------
   // immutable state
   //---------------------------------------------------------

  /**
   * The logging channel supplied by the container.
   */
   private final Logger m_logger;

   //---------------------------------------------------------
   // constructor
   //---------------------------------------------------------

  /**
   * Creation of a new hello facility.
   * @param logger a logging channel
   */
   public DefaultWidget( Logger logger )
   {
       m_logger = logger;
       m_logger.info( "I've been created" );
   }
}

