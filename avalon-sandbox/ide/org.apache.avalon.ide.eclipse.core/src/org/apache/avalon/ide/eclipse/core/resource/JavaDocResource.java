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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;

import xjavadoc.SourceSet;
import xjavadoc.XClass;
import xjavadoc.XJavaDoc;
import xjavadoc.XTag;
import xjavadoc.filesystem.FileSourceSet;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 *  
 */
public class JavaDocResource
{

    /**
	 * @uml property=xClass associationEnd={multiplicity={(1 1)}}
	 */
    private XClass xClass;

    /**
	 *  
	 */
    public JavaDocResource(XClass clazz)
    {

        xClass = clazz;
    }

    /**
	 * Collects all java source files of a project. If it is not possible to
	 * retrieve the corresponding project of 'element' throw an exception.
	 * @TODO Change, so that no ref to EclipseResource
	 * 
	 * @param IProject
	 *            project
	 */
    public static JavaDocResource[] getJavaDocResources(IProject project)
    {
        XJavaDoc xDoc;

        EclipseResource eclipse = new EclipseResource(project);
        xDoc = new XJavaDoc();
        xDoc.setUseNodeParser(true);

        List infoList = new ArrayList();
        try
        {
            String[] sourcePaths = eclipse.getSourcePaths();
            for (int i = 0; sourcePaths.length > i; i++)
            {
                SourceSet set = new FileSourceSet(new File(sourcePaths[i]));
                xDoc.addSourceSet(set);
            }

            Iterator it = xDoc.getSourceClasses().iterator();
            while (it.hasNext())
            {
                JavaDocResource java = new JavaDocResource((XClass) it.next());
                infoList.add(java);
            }
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return (JavaDocResource[]) infoList.toArray(new JavaDocResource[infoList.size()]);
    }

    public void setPersistent(boolean b)
    {
        XTag tag = xClass.getDoc().getTag("persistent");
        if (b && tag == null)
        {
            xClass.getDoc().addTag("persistent", "");
        }
        if (!b && tag != null)
        {
            xClass.getDoc().removeTag(tag);
        }
    }

    /**
	 * @return
	 */
    public boolean isPersistent()
    {
        return (xClass.getDoc().getTag("persistent") != null);
    }

    /**
	 * @return
	 */
    public String getFullQualifiedName()
    {

        return xClass.getQualifiedName();
    }

    /**
	 *  
	 */
    public void save()
    {

        try
        {
            xClass.save(null);
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
	 * @param string
	 * @return
	 */
    public Object getAttribute(String string)
    {

        XClass sClass;
        sClass = xClass;
        while (!(sClass = sClass.getSuperclass()).getName().equals("Object"))
        {
            if (sClass.getField(string) != null)
            {
                return sClass.getField(string);
            }
        }
        return null;
    }

}
