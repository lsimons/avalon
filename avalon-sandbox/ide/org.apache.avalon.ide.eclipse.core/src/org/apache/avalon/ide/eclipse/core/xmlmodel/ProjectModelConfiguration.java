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
package org.apache.avalon.ide.eclipse.core.xmlmodel;

import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.ide.eclipse.core.resource.XMLResource;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>*
 */
public class ProjectModelConfiguration
{

    /**
	 * @uml property=wizards associationEnd={multiplicity={(0 -1)}
	 * elementType=org.apache.avalon.ide.eclipse.core.xmlmodel.ProjectMeta}
	 *  
	 */
    private List projectModels = new ArrayList();

    /**
	 * @uml property=xmlResource associationEnd={multiplicity={(0 1)}}
	 */
    private XMLResource xmlResource;

    /**
	 *  
	 */
    public ProjectModelConfiguration()
    {
        super();
    }

    public static ProjectModelConfiguration newInstance(XMLResource xml)
    {

        xml.alias("projectModelConfiguration", ProjectModelConfiguration.class);
        xml.alias("projectModel", ProjectModel.class);
        xml.alias("directory", Directory.class);
        xml.alias("library", Library.class);
        xml.alias("template", Template.class);
        return (ProjectModelConfiguration) xml.loadObject();
    }

    public void addProjectModel(ProjectModel model)
    {
        projectModels.add(model);
    }

    /**
	 * @uml property=wizards
	 */
    public List getProjectModels()
    {
        return projectModels;
    }

}
