/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "D-Haven" and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.avalon.fortress.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Encapsulate the Service information, and encapsulate all the
 * logic to serialize the service.
 */
final class Service
{
    private ClassLoader m_loader;

	private final Set m_components;

	private final Class m_type;
    
    private boolean m_isRole;
    private boolean m_areRolesCollected;

    /**
     * Initialize a service with the type name.
     * 
     * @param type
     */
	public Service(final String type, boolean isRole) throws ClassNotFoundException
    {
        if (type==null)throw new NullPointerException("type");
        
        m_loader = Thread.currentThread().getContextClassLoader();
        m_type = m_loader.loadClass(type);
        m_components = new TreeSet();
        m_isRole = isRole;
        m_areRolesCollected = false;
    }
    
    public String getType()
    {
        return m_type.getName();
    }
    
    public void addComponent(final Component type)
    {
        if (type==null)throw new NullPointerException("type");
        
        m_components.add(type);
    }
    
    /**
     * Output the service info
     * 
     * @param rootDir
     * @throws IOException
     */
    public void serialize(final File rootDir) throws IOException, ClassNotFoundException
    {
        collectComponents();
        
        if (m_components.isEmpty()) return;
        
        File serviceFile = new File(rootDir, "META-INF/services/" + getType());
        PrintWriter writer = null;
        
        try
        {
            writer = new PrintWriter(new FileWriter(serviceFile, true));
            
            Iterator it = m_components.iterator();
            while( it.hasNext() )
            {
                Component comp = (Component)it.next();
                writer.println(comp.getType());
            }
        }
        finally
        {
            if (null != writer) {writer.close();}
        }
    }

	/**
	 * 
	 */
	private void collectComponents() throws ClassNotFoundException
    {
		if ( (! m_isRole) || m_areRolesCollected ) return;
        
        Iterator it = Component.m_repository.iterator();
        while( it.hasNext() )
        {
            Component comp = (Component)it.next();		
            Class component = m_loader.loadClass(comp.getType());
            
            if ( m_type.isAssignableFrom(component) )
            {
                addComponent(comp);
            }
        }
	}
}
