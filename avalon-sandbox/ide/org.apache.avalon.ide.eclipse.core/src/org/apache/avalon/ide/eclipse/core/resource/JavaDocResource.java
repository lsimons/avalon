/*
 * 
 * ============================================================================
 * The Apache Software License, Version 1.1
 * ============================================================================
 * 
 * Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowledgment: "This product includes software
 * developed by the Apache Software Foundation (http://www.apache.org/)."
 * Alternately, this acknowledgment may appear in the software itself, if and
 * wherever such third-party acknowledgments normally appear.
 *  4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and "Apache
 * Software Foundation" must not be used to endorse or promote products derived
 * from this software without prior written permission. For written permission,
 * please contact apache@apache.org.
 *  5. Products derived from this software may not be called "Apache", nor may
 * "Apache" appear in their name, without prior written permission of the
 * Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the Apache Software Foundation. For more information on the
 * Apache Software Foundation, please see <http://www.apache.org/> .
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
