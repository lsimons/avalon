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

package org.apache.avalon.meta.info.builder.tags;

/**
 * Interface holding static tag constants.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/02/21 13:27:04 $
 */
public interface Tags
{
   /**
    * The defalt namespace for tags related to the meta package.
    */ 
    public static final String NAMESPACE = "avalon";

   /**
    * The delimiter between namespace and tag key.
    */
    public static final String DELIMITER = ".";

   /**
    * The namespace tag combined with the delimiter.
    */
    public static final String NAMESPACE_TAG = NAMESPACE + DELIMITER + "namespace";
}
