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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Iterator;

import org.apache.metro.studio.eclipse.core.tools.ClassNameAnalyzer;
import org.apache.metro.studio.eclipse.core.tools.DynProjectParam;
import org.apache.metro.studio.eclipse.core.tools.SystemTool;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Metro Development Team </a>
 *         11.08.2004 last change:
 *  
 */
public class Resource
{
    /**
     * This is the name of the root segment (one of the folders directly under
     * the projects name)
     */
    private String rootSegment;

    /**
     * This is the pointer to the templates source
     */
    private String sourceFilePathName;

    /**
     * The packagename, where the output should be stored
     */
    private String packageName;

    /**
     * the parameter object which hold all dynamic parameters
     */
    private transient DynProjectParam dynParam;

    /**
     *  
     */
    public Resource()
    {
        super();
    }

    /**
     * @return Returns the rootSegment.
     */
    public String getRootSegment()
    {
        return rootSegment;
    }

    /**
     * @param rootSegment
     *            The rootSegment to set.
     */
    public void setRootSegment(String rootSegment)
    {
        this.rootSegment = rootSegment;
    }

    /**
     * @return Returns the sourceFilePathName.
     */
    public String getSourceFilePathName()
    {
        return sourceFilePathName;
    }

    /**
     * @param sourceFilePathName
     *            The sourceFilePathName to set.
     */
    public void setSourceFilePathName(String sourceFilePathName)
    {
        this.sourceFilePathName = sourceFilePathName;
    }

    /**
     * opens the templatefile and replaces all occurencies of the parameters
     * contained in the map. Output is writen to <folder>
     * 
     * @param templateName
     * @param map
     * @param folder
     */
    private void createFromTemplate(String templateName, DynProjectParam map,
            String destinationPath)
    {

        try
        {
            if(map==null)
            {
                map = new DynProjectParam();
            }
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
                             * to retain 1.3.1 compatibiliy (WSAD) dont use
                             * "replace" line = line.replaceAll(key, (String)
                             * map.get(key));
                             */
                            line = SystemTool.replaceAll(line, key,
                                    (String) map.get(key));

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

    private String replaceParam(String line, DynProjectParam map)
    {
        if(map==null)return line;
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
                    line = SystemTool.replaceAll(line, key, (String) map
                            .get(key));

                }
            }
        }
        return line;

    }

    private String getFileName()
    {

        ClassNameAnalyzer analyzer = new ClassNameAnalyzer();
        analyzer.setPath(sourceFilePathName);
        return replaceParam(analyzer.getFileName(), getDynParam());

    }

    /**
     * @param directory.
     *            Destination directory template
     * @param templateName
     */
    public void create(DirectoryTemplate directoryTemplate,
            DynProjectParam param)
    {
        setDynParam(param);
        String destinationFilePathName = null;
        Directory dir = directoryTemplate.getDirectory(rootSegment);
        if (dir == null)
            return;
        dir = dir.appendPackage(getPackageName());

        destinationFilePathName = dir.getEclipseFolder().getLocation()
                .toString();
        destinationFilePathName = destinationFilePathName + "/" + getFileName();
        createFromTemplate(sourceFilePathName, param, destinationFilePathName);

    }

    /**
     * @return Returns the packageName.
     */
    public String getPackageName()
    {
        return replaceParam(packageName, getDynParam());

    }

    /**
     * @param packageName
     *            The packageName to set.
     */
    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }

    /**
     * @return Returns the dynParam.
     */
    public DynProjectParam getDynParam()
    {
        return dynParam;
    }

    /**
     * @param dynParam
     *            The dynParam to set.
     */
    public void setDynParam(DynProjectParam dynParam)
    {
        this.dynParam = dynParam;
    }
}