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

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>*
 */
public class ProjectModel extends AttributeContainer
{

    private String label;
    private String description;
    private boolean selected = false;

    /**
	 * @uml property=directories associationEnd={multiplicity={(0 -1)}
	 * elementType=org.apache.avalon.ide.eclipse.core.xmlmodel.Directory}
	 *  
	 */
    private List directories = new ArrayList();

    /**
	 *  
	 */
    public ProjectModel()
    {
        super();
    }

    /**
	 * @return Returns the description. @uml property=description
	 */
    public String getDescription()
    {
        return description;
    }

    /**
	 * @param description
	 *            The description to set. @uml property=description
	 */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
	 * @return Returns the label. @uml property=label
	 */
    public String getLabel()
    {
        return label;
    }

    /**
	 * @param label
	 *            The label to set. @uml property=label
	 */
    public void setLabel(String label)
    {
        this.label = label;
    }

    /**
	 * @uml property=directories
	 */
    public List getDirectories()
    {
        if (directories == null)
        {
            return new ArrayList();
        } else
        {
            return directories;
        }

    }

    public void addDirectory(Directory directory)
    {
        directories.add(directory);
    }

    /**
     * @return Returns the selected.
     */
    public boolean isSelected()
    {      
        return selected;
        
    }

    /**
     * @param pSelected The selected to set.
     */
    public void setSelected(boolean pSelected)
    {
        selected = pSelected;
    }

}
