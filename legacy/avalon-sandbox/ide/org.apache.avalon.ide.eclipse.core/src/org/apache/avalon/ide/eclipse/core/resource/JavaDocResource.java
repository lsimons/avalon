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
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;

import xjavadoc.SourceSet;
import xjavadoc.XClass;
import xjavadoc.XJavaDoc;
import xjavadoc.XTag;
import xjavadoc.filesystem.FileSourceSet;
import xjavadoc.filesystem.XJavadocFile;

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
    private static JavaDocResource[] docCache;

    public JavaDocResource(String qualifiedClassName, String filePathName)
    {
        XJavaDoc xDoc;
        xDoc = new XJavaDoc();
        xDoc.setUseNodeParser(true);
        xDoc.addAbstractFile(qualifiedClassName, new XJavadocFile(new File(filePathName)));

        xClass = xDoc.getXClass(qualifiedClassName);
    }
    /**
     *  
     */
    public JavaDocResource(XClass clazz)
    {

        xClass = clazz;
    }
    public static void createJavaDocCache(IProject project)
    {
        docCache = getJavaDocResources(project);
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
    
    public static JavaDocResource getJavaDocResource(String qualifiedClassName)
    {
        if (docCache==null) return null;
        
        for(int i=0; docCache.length>i; i++){
            if(docCache[i].getQualifiedName().equals(qualifiedClassName))
            {
                return docCache[i];
            }
        }
        return null;
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
     * @param string
     * @return
     */
    public boolean isPersistent()
    {

        XClass sClass;
        XClass clazz = xClass;
        
        if(isClassPersistent(clazz)) return true;

        while (!(sClass = clazz.getSuperclass()).getName().equals("Object"))
           {
            if (isClassPersistent(sClass))
               {
                return isClassPersistent(sClass);
            }
            clazz = sClass;
        }
        return isClassPersistent(clazz);
    }
    
    /**
     * @return
     */
    public boolean isClassPersistent(XClass xClazz)
    {
        XTag tag;

        if (xClazz.getDoc().getTag("persistent") != null)
            return true;

        try
        {
            if ((tag = xClazz.getDoc().getTag("uml")) != null)
            {
                String stereotype = tag.getValue();
                Properties prop = new Properties();
                prop.load(new StringBufferInputStream(stereotype));
                if ((stereotype = (String) prop.get("stereotypes")) == null)
                    return false;
                String array[] = stringToArray(stereotype);
                for (int j = 0; array.length > j; j++)
                {
                    if (array[j].equals("persistent"))
                        return true;
                }
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param pStereotype
     * @return
     */
    private String[] stringToArray(String pStereotype)
    {
        List result = new ArrayList();
        StringBuffer buf = new StringBuffer(pStereotype);
        char ch;
        boolean open = false;
        StringBuffer out = new StringBuffer();

        for (int i = 0; buf.length() > i; i++)
        {
            ch = buf.charAt(i);
            if (ch == '{')
                continue;
            if (ch == '}')
                continue;
            if (ch == '"' && !open)
            {
                open = true;
                out = new StringBuffer();
                continue;
            }
            if (ch == '"' && open)
            {
                open = false;
                result.add(out.toString());
                continue;
            }

            if (open)
                out.append(ch);

        }

        return (String[]) result.toArray(new String[result.size()]);
    }

    /**
     * @return
     */
    public String getQualifiedName()
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
        XClass clazz = xClass;

        while (!(sClass = clazz.getSuperclass()).getName().equals("Object"))
        {
            if (sClass.getField(string) != null)
            {
                return sClass.getField(string);
            }
            clazz = sClass;
        }
        return clazz.getField(string);
    }

}
