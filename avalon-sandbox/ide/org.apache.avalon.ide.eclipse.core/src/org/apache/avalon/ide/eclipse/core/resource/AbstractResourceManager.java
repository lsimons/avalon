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

import org.eclipse.core.resources.IProject;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * 
 * The idea behind the resource package is, that a 'logical' concept like
 * component, project or class mostely is constructed out of many physical
 * resources. Some examples: A merlin service consists of an interfaces (java
 * source as an resource), which contains some meta infos as javadoc tags (a
 * javadoc as resource) and a block.xml a xml file as resource) etc. The
 * 'Manager' classes represent the logical concepts. The 'Resource' classes
 * represent resources, which are related to the logical concept.
 *  
 */
public class AbstractResourceManager
{
    /**
     *  
     */
    public AbstractResourceManager()
    {
        super();
    }

    public AbstractResourceManager(IProject project)
    {
        super();
        eclipseResource = new EclipseResource(project);
        templateResource = new TemplateResource(project);
    }

    private boolean changed = false;

    /**
     * @uml property=eclipseResource associationEnd={multiplicity={(0 1)}}
     */
    private EclipseResource eclipseResource;

    /**
     * @uml property=javaDocResource associationEnd={multiplicity={(0 1)}}
     */
    private JavaDocResource javaDocResource;

    /**
     * @uml property=templateResource associationEnd={multiplicity={(0 1)}}
     */
    private TemplateResource templateResource;

    /**
     * @uml property=xmlResource associationEnd={multiplicity={(0 1)}}
     */
    private XMLResource xmlResource;

    /**
     * @uml property=project associationEnd={multiplicity={(0 1)}}
     */
    private IProject project;

    /**
     * @return Returns the eclipse. @uml property=eclipseResource
     */
    public EclipseResource getEclipseResource()
    {
        return eclipseResource;
    }

    /**
     * @return Returns the javaDoc. @uml property=javaDocResource
     */
    public JavaDocResource getJavaDocResource()
    {
        return javaDocResource;
    }

    /**
     * @return Returns the changed.
     */
    public boolean isChanged()
    {
        return changed;
    }

    /**
     * @param changed
     *            The changed to set. @uml property=changed
     */
    public void setChanged(boolean changed)
    {
        this.changed = changed;
    }

    /**
     * @param resource
     *            @uml property=javaDocResource
     */
    public void setJavaDocResource(JavaDocResource resource)
    {

        javaDocResource = resource;
    }

    /**
     * @return Returns the project. @uml property=project
     */
    public IProject getProject()
    {
        return project;
    }

    /**
     * @return Returns the xmlResource. @uml property=xmlResource
     */
    public XMLResource getXmlResource()
    {
        return xmlResource;
    }

    /**
     * @param xmlResource
     *            The xmlResource to set. @uml property=xmlResource
     */
    public void setXmlResource(XMLResource xmlResource)
    {
        this.xmlResource = xmlResource;
    }

    /**
     * @return Returns the templateResource. @uml property=templateResource
     */
    public TemplateResource getTemplateResource()
    {
        return templateResource;
    }

    /**
     * @param pProject
     */
    public void setProject(IProject pProject)
    {
        project = pProject;

    }
    /**
     * @param pTemplateResource
     *            The templateResource to set.
     */
    public void setTemplateResource(TemplateResource pTemplateResource)
    {
        templateResource = pTemplateResource;
    }

}
