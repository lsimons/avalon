/*
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
