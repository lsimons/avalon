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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Iterator;

import org.apache.avalon.ide.eclipse.core.tools.DynProjectParam;
import org.eclipse.core.resources.IProject;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * 
 * To use a templateResource you have to follow the following steps:
 * 
 *  - setTemplateSourcePath()       path where to find the template
 *  - setParameter()                see tools.DynProjectParam
 *  - createTemplate(destinationPathFileName, templateSourceFileName)
 *  
 */
public class TemplateResource
{

    /**
	 * @uml property=project associationEnd={multiplicity={(1 1)}}
	 */
    private IProject project;

    /**
	 * @uml property=parameter associationEnd={multiplicity={(0 1)}}
	 */
    private DynProjectParam parameter;

    private String templatePath;
    /**
	 *  
	 */
    public TemplateResource(IProject project)
    {
        super();
        this.project = project;
    }

    /**
	 * opens the templatefile and replaces all occurencies of the parameters
	 * contained in the map. Output is writen to <folder>
	 * 
	 * @param templateName
	 * @param map
	 * @param folder
	 */
    private void createFromTemplate(
        String templateName,
        DynProjectParam map,
        String destinationPath)
    {

        try
        {
            InputStream input = new FileInputStream(new File(templateName));
            InputStreamReader file = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(file);

            String outPath = destinationPath;
            FileOutputStream ostream = new FileOutputStream(outPath);
            OutputStreamWriter out = new OutputStreamWriter(ostream);

            String line;
            Iterator it = map.keySet().iterator();
            String key;

            while ((line = reader.readLine()) != null)
            {
                while (it.hasNext())
                {
                    if ((key = (String) it.next()).startsWith("%"))
                    {
                        if ((line.indexOf(key)) != -1)
                        {
                            /* 
                             * to retain 1.3.1 compatibiliy (WSAD) dont use "replace"
                             * line = line.replaceAll(key, (String) map.get(key));
                             */
                            line = SystemResource.replaceAll(line, key, (String) map.get(key)); 
                            
                        }
                    }
                }
                out.write(line);
                out.write("\n");
                it = map.keySet().iterator();
            }
            out.close();
            file.close();
        } catch (Exception e)
        {
            System.out.println(e);
        }
    }
    public static String replaceParam(String line, DynProjectParam map)
    {

        Iterator it = map.keySet().iterator();
        String key;

        while (it.hasNext())
        {
            if ((key = (String) it.next()).startsWith("%"))
            {
                if ((line.indexOf(key)) != -1)
                {
                    /* 
                     * to retain 1.3.1 compatibiliy (WSAD) dont use "replace"
                     * line = line.replaceAll(key, (String) map.get(key));
                     */
                    line = SystemResource.replaceAll(line, key, (String) map.get(key));
               
                }
            }
        }
        return line;

    }
    /**
	 * @param destinationPath
	 *            (relative to projects location)
	 * @param templateName
	 */
    public void createTemplate(String destinationPath, String templateName)
    {

        createFromTemplate(templatePath + templateName, parameter, destinationPath);

    }

    /**
	 * @param string
	 */
    public void setTemplateSourcePath(String string)
    {

        templatePath = string;

    }

    /**
	 * @param param
	 *            @uml property=parameter
	 */
    public void setParameter(DynProjectParam param)
    {

        parameter = param;
    }

}
