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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 *  
 */
public class ComponentResourceManager extends AbstractResourceManager
{

    /**
	 * @uml property=xinfoFile associationEnd={multiplicity={(1 1)}}
	 */
    private IFile xinfoFile;

    /**
	 * @uml property=containedClasses associationEnd={multiplicity={(0 -1)}
	 * elementType=org.apache.avalon.ide.eclipse.core.resource.ClassResourceManager}
	 *  
	 */
    private ClassResourceManager[] containedClasses = null;

    /**
	 * @param file
	 *            must be either a Java source file or an '.xinfo' file, which
	 *            represents a Java resource
	 */
    public ComponentResourceManager(IFile file)
    {
        super(file.getProject());
        xinfoFile = file;
    }

    /**
	 * Get all class resources for the eclipse project, which covers the
	 * component. A class resource covers all important recources belonging to
	 * a class like .xinfo file, javadocs etc.
	 * 
	 * @return ClassResourceManager[]
	 */
    public ClassResourceManager[] getClassResources()
    {

        if (containedClasses != null)
        {
            return containedClasses;
        }

        JavaDocResource javaResources[] =
            JavaDocResource.getJavaDocResources(xinfoFile.getProject());

        List list = new ArrayList();
        ClassResourceManager manager;
        for (int i = 0; javaResources.length > i; i++)
        {
            manager = new ClassResourceManager(xinfoFile.getProject());
            manager.setJavaDocResource(javaResources[i]);
            list.add(manager);
        }
        containedClasses =
            (ClassResourceManager[]) list.toArray(new ClassResourceManager[list.size()]);
        return containedClasses;
    }

}