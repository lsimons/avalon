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

package org.apache.avalon.repository.provider;

/**
 * The defintion of an application builder.
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.5 $ $Date: 2004/01/31 13:29:50 $
 */
public interface Builder
{       
   /**
    * Return the primary class established by the builder.
    * @return the class
    */
    Class getFactoryClass();
 
    /**
     * Return the factory established by the builder.
     * 
     * @return the factory
     */
    Factory getFactory();

    /**
     * Gets the ClassLoader used by this builder.
     * 
     * @return the ClassLoader built by the builder.
     */
    ClassLoader getClassLoader();

}
