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

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 *  
 */
public class Library
{

    String name;
    /**
	 *  
	 */
    public Library()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
	 * @return Returns the name. @uml property=name
	 */
    public String getName()
    {
        return name;
    }

    /**
	 * @param name
	 *            The name to set. @uml property=name
	 */
    public void setName(String name)
    {
        this.name = name;
    }

}
