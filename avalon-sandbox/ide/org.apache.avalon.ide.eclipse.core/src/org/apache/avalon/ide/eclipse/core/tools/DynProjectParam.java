/*
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
