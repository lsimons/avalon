/*
Copyright 2004 The Apache Software Foundation
Licensed  under the  Apache License,  Version 2.0  (the "License");
you may not use  this file  except in  compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed  under the  License is distributed on an "AS IS" BASIS,
WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
implied.

See the License for the specific language governing permissions and
limitations under the License.
*/

package org.apache.avalon.magic;

public interface PluginFacade
{
    /** Returns the Plugin instance of that this PluginFacade refers to.
     */
    Plugin resolve() throws Exception;

    /** Invalidates the plugin, and any created instance should be 
     *  recreated.
     **/    
    void invalidate();
    
    /** Returns the context for the Plugin.
     */
    PluginContext getPluginContext();
    
    /** Return the Classname of the Plugin represented by the Facade.
     **/
    String getPluginClassname();
} 
