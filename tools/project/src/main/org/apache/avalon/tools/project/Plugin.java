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

package org.apache.avalon.tools.project;

import java.io.File;

/**
 * Defintion of a project. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class Plugin extends Definition
{
    private ProjectRef[] m_projects;
    private ResourceRef[] m_resources;
    private File m_basedir;

    public Plugin(
      String key, File basedir, Info info, 
      ResourceRef[] resources, ProjectRef[] projects, PluginRef[] plugins )
    {
        super( key, basedir, info, resources, projects, plugins );
    }

    public boolean equals( Object other )
    {
        if( super.equals( other ) && ( other instanceof Plugin ))
        {
            return true;
        }
        return false;
    }
}
