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
package org.apache.metro.studio.eclipse.core.templateengine;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.apache.metro.studio.eclipse.core.MetroStudioCore;
import org.apache.metro.studio.eclipse.core.tools.DynProjectParam;
import org.eclipse.core.resources.IProject;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Metro Development Team</a>
 * 11.08.2004
 * last change:
 * 
 */
public class ResourceTemplateManager
{

    private Hashtable resourceTemplates = new Hashtable();
    private DirectoryTemplateManager directoryManager;
    
    final static String baseDir = MetroStudioCore.getDefault()
    .getPluginLocation().toString();
    public static final String DEFAULT_CONFIG_PATH = baseDir + "config/resources.test_cfg";
    
    /**
     * 
     */
    public ResourceTemplateManager()
    {
        super();
    }

    public static ResourceTemplateManager load(String resourceFilePathName)
    {
        if(resourceFilePathName == null)
        {
            resourceFilePathName = getDefaultFilePathName();
        }
        
        XStream xstream = new XStream(new DomDriver()); 
        initXStream(xstream);

        new DirectoryTemplateManager().addXStreamAliases(xstream);
        FileReader reader = null;
        try
        {
            reader = new FileReader(resourceFilePathName);

        } catch (FileNotFoundException e)
        {
            MetroStudioCore.log(e,
                    "can't open Resource Template configuration file");
            return null;
        }
        
        ResourceTemplateManager rm = (ResourceTemplateManager) xstream.fromXML(reader);
        return rm;

    }
    /**
     * @return
     */
    private static String getDefaultFilePathName()
    {

        return ResourceTemplateManager.DEFAULT_CONFIG_PATH;
    }

    /**
     * @param xstream
     */
    public static void initXStream(XStream xstream)
    {
        xstream.alias("ResourceTemplates", ResourceTemplateManager.class);
        xstream.alias("ResourceTemplate", ResourceTemplate.class);
        xstream.alias("Resource", Resource.class);
        xstream.alias("Library", Library.class);
    }

    public void addResourceTemplate(ResourceTemplate resource)
    {
        resourceTemplates.put(resource.getTemplateId(), resource);
    }

    /**
     * @return String[]. An array of all keys
     */
    public String[] listTemplateNames()
    {
        Set set = resourceTemplates.keySet();
        String[] keyList = new String[set.size()];
        Iterator it = set.iterator();
        for (int i=0; i<set.size(); i++)
        {
            keyList[i] = (String)it.next();
        }
        return keyList;
    }

    /**
     * @param string
     * @return
     */
    public ResourceTemplate getTemplate(String key)
    {
        return (ResourceTemplate)resourceTemplates.get(key);
    }

    /**
     * @param dm
     */
    public void importDirectoryTemplates(DirectoryTemplateManager dm)
    {
        directoryManager = dm;
    }
    
    /**
     * Create a directorystrucure and all required resources in a project
     */
    public void create(IProject project, String resourceName, DynProjectParam param)
    {
        ResourceTemplate template = getTemplate(resourceName);
        if(template == null)
        {
            MetroStudioCore.log(null, "cant find resource template <" + resourceName +">");
            return;
        }
        template.create(project, directoryManager, param);
    }

    /**
     * 
     */
    public Hashtable getResourceTemplates()
    {
        return resourceTemplates;
    }

}
