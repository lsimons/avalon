/*
 * Copyright 2004 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avalon.finder.test;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.activity.Disposable;

/**
 * A component that implements the Widget service using ECM semantics.
 */
public class ECMWidget implements Widget, Configurable, Disposable
{
   //---------------------------------------------------------
   // static
   //---------------------------------------------------------

   public static String ROLE = Widget.class.getName();

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
   public ECMWidget( Logger logger )
   {
       m_logger = logger;
       m_logger.debug( "hello" );
   }

   //---------------------------------------------------------
   // Configurable
   //---------------------------------------------------------

   /**
    * Configuration of the gizmo by the container.
    * @param config the supplied configuration
    */
    public void configure( Configuration config ) throws ConfigurationException
    {
        final String message = config.getChild( "message" ).getValue( null );
        if( null != message )
        {
            m_logger.debug( message );
        }
    }

   //---------------------------------------------------------
   // Disposable
   //---------------------------------------------------------

  /**
   * End-of-life processing initiated by the container.
   */
   public void dispose()
   {
        m_logger.debug( "time to die" );
   }

   //---------------------------------------------------------
   // Object
   //---------------------------------------------------------

   public String toString()
   {
       return "[ecm-widget:" + System.identityHashCode( this ) + "]";
   }
}

