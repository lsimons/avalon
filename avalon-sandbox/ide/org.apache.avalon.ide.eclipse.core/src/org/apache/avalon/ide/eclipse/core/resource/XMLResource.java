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
package org.apache.avalon.ide.eclipse.core.resource;

import org.apache.avalon.ide.eclipse.core.tools.*;
import org.apache.avalon.ide.eclipse.core.xmlmodel.AttributeContainerConverter;
import org.apache.avalon.ide.eclipse.merlin.core.MerlinDeveloperCore;
import org.eclipse.core.runtime.IPath;

import org.apache.avalon.ide.eclipse.core.xmlmodel.XStream;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 *  
 */
public class XMLResource
{

    /**
	 * @uml property=xs associationEnd={multiplicity={(1 1)}}
	 */
    private XStream xs;

    private StringBuffer inputString;
    private String fileName;
    private String pluginId;
    /**
	 *  
	 */
    public XMLResource()
    {
        super();
        xs = new XStream();
        xs.registerConverter(new AttributeContainerConverter(xs.getClassMapper()));
    }

    public void alias(String name, Class clazz)
    {
        xs.alias(name, clazz);
    }

    public Object loadObject()
    {
        IPath pluginPath;

        if (pluginId != null)
        {
            pluginPath = EclipseDirectoryHelper.getPluginLocation(pluginId);
        } else
        {
            pluginPath = EclipseDirectoryHelper.getPluginLocation(MerlinDeveloperCore.PLUGIN_ID);
        }

        pluginPath = pluginPath.append(fileName);

        String str = SystemResource.getFileContents(pluginPath.toString());
        Object obj = xs.fromXML(str);
        return obj;
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

    /**
	 * @return Returns the pluginId. @uml property=pluginId
	 */
    public String getPluginId()
    {
        return pluginId;
    }

    /**
	 * @param pluginId
	 *            The pluginId to set. @uml property=pluginId
	 */
    public void setPluginId(String pluginId)
    {
        this.pluginId = pluginId;
    }

}
