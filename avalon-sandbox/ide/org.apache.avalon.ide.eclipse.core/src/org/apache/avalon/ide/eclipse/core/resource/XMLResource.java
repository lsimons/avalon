/*
   
      Copyright 2004. The Apache Software Foundation.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. 
   
 */
package org.apache.avalon.ide.eclipse.core.resource;

import org.apache.avalon.ide.eclipse.core.tools.EclipseDirectoryHelper;
import org.apache.avalon.ide.eclipse.core.xmlmodel.XStream;
import org.apache.avalon.ide.eclipse.merlin.core.MerlinDeveloperCore;
import org.eclipse.core.runtime.IPath;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 *  
 */
public class XMLResource
{

    /**
	 * @uml property=xs associationEnd={multiplicity={(1 1)}}
	 */
    private XStream xs;

    private StringBuffer inputString;
    private String fileName;
    private String pluginId;
    /**
	 *  
	 */
    public XMLResource()
    {
        super();
        xs = new XStream();
    }

    public void alias(String name, Class clazz)
    {
        xs.alias(name, clazz);
    }

    public Object loadObject()
    {
        IPath pluginPath;

        if (pluginId != null)
        {
            pluginPath = EclipseDirectoryHelper.getPluginLocation(pluginId);
        } else
        {
            pluginPath = EclipseDirectoryHelper.getPluginLocation(MerlinDeveloperCore.PLUGIN_ID);
        }

        pluginPath = pluginPath.append(fileName);

        String str = SystemResource.getFileContents(pluginPath.toString());
        Object obj = xs.fromXML(str);
        return obj;
    }

    /**
	 * @return Returns the fileName. @uml property=fileName
	 */
    public String getFileName()
    {
        return fileName;
    }

    /**
	 * @param fileName
	 *            The fileName to set. @uml property=fileName
	 */
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    /**
	 * @return Returns the pluginId. @uml property=pluginId
	 */
    public String getPluginId()
    {
        return pluginId;
    }

    /**
	 * @param pluginId
	 *            The pluginId to set. @uml property=pluginId
	 */
    public void setPluginId(String pluginId)
    {
        this.pluginId = pluginId;
    }

}
