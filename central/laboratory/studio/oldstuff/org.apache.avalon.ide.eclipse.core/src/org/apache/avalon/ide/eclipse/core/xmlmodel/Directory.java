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

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>*
 */
public class Directory extends AttributeContainer
{

    private Boolean source = new Boolean(false);

    /**
	 * @uml property=templates associationEnd={multiplicity={(0 -1)}
	 * elementType=org.apache.avalon.ide.eclipse.core.xmlmodel.Template}
	 *  
	 */
    private List templates = new ArrayList();

    /**
	 * @uml property=libraries associationEnd={multiplicity={(0 -1)}
	 * elementType=org.apache.avalon.ide.eclipse.core.xmlmodel.Library}
	 *  
	 */
    private List libraries = new ArrayList();

    /**
	 *  
	 */
    public Directory()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
	 * @return Returns the source.
	 */
    public boolean isSource()
    {
        if (source == null)
            return false;
        return source.booleanValue();
    }

    /**
	 * @param source
	 *            The source to set. @uml property=source
	 */
    public void setSource(Boolean source)
    {
        this.source = source;
    }

    public void addTemplate(Template template)
    {
        templates.add(template);
    }

    public void addLibrary(Library library)
    {
        templates.add(library);
    }

    /**
	 * @uml property=templates
	 */
    public List getTemplates()
    {
        if (templates == null)
            return new ArrayList();
        return templates;
    }

    /**
	 * @uml property=libraries
	 */
    public List getLibraries()
    {
        if (libraries == null)
            return new ArrayList();
        return libraries;
    }

}
