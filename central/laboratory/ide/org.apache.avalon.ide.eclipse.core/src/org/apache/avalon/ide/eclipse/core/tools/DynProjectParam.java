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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 *  
 */
public class DynProjectParam
{

    /**
	 * @uml property=param associationEnd={multiplicity={(0 1)}
	 * qualifier=(constant:java.lang.String string:java.lang.String)}
	 */
    Map param = new HashMap();

    public void setFullImplementationClassName(String fullClassName)
    {

        param.put("%implementationpackage%", extractPackage(fullClassName));
        param.put("%implementationclass%", extractClassName(fullClassName));
        param.put("%full_implementationclass%", fullClassName);

    }
    public void setFullServiceClassName(String fullClassName)
    {

        param.put("%servicepackage%", extractPackage(fullClassName));
        param.put("%serviceclass%", extractClassName(fullClassName));
        param.put("%full_serviceclass%", fullClassName);

    }
    public void setContainerName(String containerName)
    {

        param.put("%containername%", containerName);

    }

    /**
	 * @param fullClassName
	 * @return the package part of the fully qualified className
	 */
    private String extractPackage(String fullClassName)
    {
        if (fullClassName.trim().length() > 0)
        {
            return fullClassName.substring(0, fullClassName.lastIndexOf('.'));
        } else
        {
            return "";
        }
    }

    /**
	 * @param fullClassName
	 * @return className part of the fully qualified className
	 */
    private String extractClassName(String fullClassName)
    {
        if (fullClassName.trim().length() > 0)
        {
            return fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
        } else
        {
            return "";
        }
    }
    /**
	 * @param string
	 */
    public void setProjectName(String string)
    {

        param.put("%projectname%", string);
    }
    /**
	 *  
	 */
    public String getProjectName()
    {

        return (String) param.get("%projectname%");
    }
    /**
	 * @param string
	 */
    public void setVirtualServiceName(String string)
    {

        param.put("%virtualservicename%", string);

    }
    /**
	 * @param string
	 */
    public void setVersion(String string)
    {

        param.put("%version%", string);

    }
    /**
     * @param string
     */
    public void setDatabaseName(String string)
    {

        param.put("%databasename%", string);

    }
    
    /**
	 * @param string
	 * @return Object
	 */
    public Object get(String string)
    {

        return param.get(string);
    }
    /**
	 * @return Set
	 */
    public Set keySet()
    {
        return param.keySet();
    }
    /**
	 * @return (String) package of the service
	 */
    public String getServicePackage()
    {
        return (String) param.get("%servicepackage%");
    }
    /**
	 * @return (String) package of the implementation class
	 */
    public String getImplementationPackage()
    {
        return (String) param.get("%implementationpackage%");
    }

}
