/*
 * 
 * ============================================================================
 * The Apache Software License, Version 1.1
 * ============================================================================
 * 
 * Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowledgment: "This product includes software
 * developed by the Apache Software Foundation (http://www.apache.org/)."
 * Alternately, this acknowledgment may appear in the software itself, if and
 * wherever such third-party acknowledgments normally appear.
 *  4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and "Apache
 * Software Foundation" must not be used to endorse or promote products derived
 * from this software without prior written permission. For written permission,
 * please contact apache@apache.org.
 *  5. Products derived from this software may not be called "Apache", nor may
 * "Apache" appear in their name, without prior written permission of the
 * Apache Software Foundation.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 *  
 */
public class ComponentResourceManager extends AbstractResourceManager
{

    /**
	 * @uml property=xinfoFile associationEnd={multiplicity={(1 1)}}
	 */
    private IFile xinfoFile;

    /**
	 * @uml property=containedClasses associationEnd={multiplicity={(0 -1)}
	 * elementType=org.apache.avalon.ide.eclipse.core.resource.ClassResourceManager}
	 *  
	 */
    private ClassResourceManager[] containedClasses = null;

    /**
	 * @param file
	 *            must be either a Java source file or an '.xinfo' file, which
	 *            represents a Java resource
	 */
    public ComponentResourceManager(IFile file)
    {
        super(file.getProject());
        xinfoFile = file;
    }

    /**
	 * Get all class resources for the eclipse project, which covers the
	 * component. A class resource covers all important recources belonging to
	 * a class like .xinfo file, javadocs etc.
	 * 
	 * @return ClassResourceManager[]
	 */
    public ClassResourceManager[] getClassResources()
    {

        if (containedClasses != null)
        {
            return containedClasses;
        }

        JavaDocResource javaResources[] =
            JavaDocResource.getJavaDocResources(xinfoFile.getProject());

        List list = new ArrayList();
        ClassResourceManager manager;
        for (int i = 0; javaResources.length > i; i++)
        {
            manager = new ClassResourceManager(xinfoFile.getProject());
            manager.setJavaDocResource(javaResources[i]);
            list.add(manager);
        }
        containedClasses =
            (ClassResourceManager[]) list.toArray(new ClassResourceManager[list.size()]);
        return containedClasses;
    }

}