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

package tutorial;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.activity.Disposable;

/**
 * A sample component.  
 *
 * @avalon.component 
 *    version="1.0" 
 *    name="hello" 
 *    lifestyle="singleton"
 * @avalon.service type="tutorial.Hello"
 */
public class HelloComponent 
  implements Hello, Disposable
{
    //-------------------------------------------------------
    // immutable state
    //-------------------------------------------------------

   /**
    * Internal reference to the logging channel supplied to us 
    * by the container. 
    */
    private final Logger m_logger;

    //-------------------------------------------------------
    // constructor
    //-------------------------------------------------------

   /**
    * Creation of a new hello component instance.
    *
    * @param logger the logging channel supplied by the container
    */
    public HelloComponent( Logger logger )
    {
        m_logger = logger;
        m_logger.info( "instantiated" );
    }

    //-------------------------------------------------------
    // Hello service implementation
    //-------------------------------------------------------

   /**
    * The hello service implementation.
    */
    public void sayHello()
    {
        getLogger().info( "HELLO" );
    }

    //-------------------------------------------------------
    // Disposable lifecycle interface
    //-------------------------------------------------------

   /**
    * Component disposal trigger by the container during which
    * the component will release consumed resources.
    */
    public void dispose()
    {
        getLogger().info( "disposal" );
    }

    //-------------------------------------------------------
    // internal utilities
    //-------------------------------------------------------

   /**
    * Return the logging channel assigned to us by the container.
    * @return the logging channel
    */
    private Logger getLogger()
    {
        return m_logger;
    }

}
