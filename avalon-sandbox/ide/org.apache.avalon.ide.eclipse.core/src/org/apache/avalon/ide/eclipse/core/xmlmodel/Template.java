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
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>*
 */
public class Template extends AttributeContainer
{

    private String fileName;
    /**
	 *  
	 */
    public Template()
    {
        super();
    }

    /**
	 * @return Returns the fileName. @uml property=fileName
	 */
    public String getFileName()
    {
        return fileName;
    }

    /**
	 * @param fileName
	 *            The fileName to set. @uml property=fileName
	 */
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

}
