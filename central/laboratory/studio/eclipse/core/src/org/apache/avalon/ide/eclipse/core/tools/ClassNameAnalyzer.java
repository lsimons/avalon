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
package org.apache.avalon.ide.eclipse.core.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>*
 */
public class ClassNameAnalyzer
{

    /**
	 * @uml property=segments associationEnd={multiplicity={(0 -1)}
	 * elementType=java.lang.String}
	 *  
	 */
    private List segments = new ArrayList();

    public void setFullClassName(String name)
    {

        while (name.indexOf(".") != -1)
        {
            segments.add(name.substring(0, name.indexOf(".")));
            name = name.substring(name.indexOf(".") + 1, name.length());
        }
        segments.add(name);
    }

    public String getPath()
    {

        StringBuffer buff = new StringBuffer();
        for (int i = 0; segments.size() > i + 2; i++)
        {
            buff.append((String) segments.get(i));
            buff.append("/");
        }
        return buff.toString();
    }

    /**
	 * @return
	 */
    public Object getFileName()
    {

        StringBuffer buff = new StringBuffer();
        int size = segments.size();
        for (int i = size - 2; size > i; i++)
        {
            buff.append((String) segments.get(i));
            if (i < size - 1)
                buff.append(".");
        }
        return buff.toString();
    }

    /**
	 * @param directory
	 */
    public void setPath(String directory)
    {

        while (directory.indexOf("/") != -1)
        {
            segments.add(directory.substring(0, directory.indexOf("/")));
            directory = directory.substring(directory.indexOf("/") + 1, directory.length());
        }
        segments.add(directory);
    }

    /**
	 * @return Returns the segments. @uml property=segments
	 */
    public List getSegments()
    {
        return segments;
    }

}
