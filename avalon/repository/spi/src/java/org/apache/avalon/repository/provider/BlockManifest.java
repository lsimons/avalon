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
 * A block manifest that provides a set of convinience operations
 * to access block related attributes.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/01/24 23:20:05 $
 */
public interface BlockManifest
{

   /**
    * Group identifier manifest key.
    */
    public static final String BLOCK_GROUP_KEY = "Block-Group";

   /**
    * Name identifier manifest key.
    */
    public static final String BLOCK_NAME_KEY = "Block-Name";

   /**
    * Version identifier manifest key.
    */
    public static final String BLOCK_VERSION_KEY = "Block-Version";

   /**
    * Get the name of the group that the block is a part of.
    * @return the block group
    */
    String getBlockGroup();

   /**
    * Get the name the block.
    * @return the block name
    */
    String getBlockName();

   /**
    * Get the version of the block.
    * @return the block version
    */
    String getBlockVersion();
}
