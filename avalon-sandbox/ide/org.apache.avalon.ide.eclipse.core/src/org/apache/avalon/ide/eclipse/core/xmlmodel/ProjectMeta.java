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
package org.apache.avalon.ide.eclipse.core.xmlmodel;

import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.ide.eclipse.core.tools.DynProjectParam;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>*
 */
public class ProjectMeta extends AttributeContainer
{

    private String label;
    private String description;

    /**
	 * @uml property=directories associationEnd={multiplicity={(0 -1)}
	 * elementType=org.apache.avalon.ide.eclipse.core.xmlmodel.Directory}
	 *  
	 */
    private List directories = new ArrayList();

    /**
	 * @uml property=parameter associationEnd={multiplicity={(0 1)}}
	 */
    private DynProjectParam parameter;

    /**
	 *  
	 */
    public ProjectMeta()
    {
        super();
    }

    /**
	 * @return Returns the description. @uml property=description
	 */
    public String getDescription()
    {
        return description;
    }

    /**
	 * @param description
	 *            The description to set. @uml property=description
	 */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
	 * @return Returns the label. @uml property=label
	 */
    public String getLabel()
    {
        return label;
    }

    /**
	 * @param label
	 *            The label to set. @uml property=label
	 */
    public void setLabel(String label)
    {
        this.label = label;
    }

    /**
	 * @uml property=directories
	 */
    public List getDirectories()
    {
        return directories;
    }

    public void addDirectory(Directory directory)
    {
        directories.add(directory);
    }

    /**
	 * @return
	 */
    public ImageDescriptor getImage()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
	 * @return Returns the parameter. @uml property=parameter
	 */
    public DynProjectParam getParameter()
    {
        return parameter;
    }

    /**
	 * @param parameter
	 *            The parameter to set. @uml property=parameter
	 */
    public void setParameter(DynProjectParam parameter)
    {
        this.parameter = parameter;
    }

}
