/*
 * 
 * ============================================================================
 * The Apache Software License, Version 1.1
 * ============================================================================
 * 
 * Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowledgment: "This product includes software developed by the Apache
 * Software Foundation (http://www.apache.org/)." Alternately, this
 * acknowledgment may appear in the software itself, if and wherever such
 * third-party acknowledgments normally appear. 4. The names "Jakarta", "Apache
 * Avalon", "Avalon Framework" and "Apache Software Foundation" must not be
 * used to endorse or promote products derived from this software without prior
 * written permission. For written permission, please contact
 * apache@apache.org. 5. Products derived from this software may not be called
 * "Apache", nor may "Apache" appear in their name, without prior written
 * permission of the Apache Software Foundation.
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
