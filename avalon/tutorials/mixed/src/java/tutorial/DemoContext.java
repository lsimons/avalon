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

package tutorial;

import java.io.File;

import org.apache.avalon.framework.context.Context;

/**
 * An example of an convinience interface that extends the 
 * standard Avalon Context interface.
 */
public interface DemoContext extends Context
{

   /**
    * Return the component name.
    * @return the component name
    */
    String getName();

   /**
    * Return the name of the partition assigned to the component.
    * @return the partition name
    */
    String getPartition();

   /**
    * Return the home directory.
    * @return the directory
    */
    File getHomeDirectory();

   /**
    * Return the temporary working directory.
    * @return the directory
    */
    File getWorkingDirectory();
}
