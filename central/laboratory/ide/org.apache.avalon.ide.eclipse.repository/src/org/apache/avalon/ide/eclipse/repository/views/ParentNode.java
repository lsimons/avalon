/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.avalon.ide.eclipse.repository.views;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.avalon.ide.repository.RepositoryAgent;
import org.apache.avalon.ide.repository.ResourceInfo;


/** A container node in the RepositoryView.
 * 
 * @author Niclas Hedhman, niclas@hedhman.org
 */
class ParentNode extends ViewNode
{
    private final RepositoryView m_View;
    private ArrayList m_Children;
    
    public ParentNode(RepositoryView pView, RepositoryAgent agent, ResourceInfo info)
    {
        super( pView, agent, info);
        this.m_View = pView;
        m_Children = new ArrayList();
    }

    public void addChild(ViewNode child)
    {
        m_Children.add(child);
        child.setParent(this);
    }
    public void removeChild(ViewNode child)
    {
        m_Children.remove(child);
        child.setParent(null);
    }
    public ViewNode[] getChildren()
    {
        return (ViewNode[]) m_Children.toArray(new ViewNode[m_Children.size()]);
    }
    public boolean hasChildren()
    {
        return m_Children.size() > 0;
    }

    public ViewNode findObjectByName(String name)
    {
        Iterator list = m_Children.iterator();
        while (list.hasNext())
        {
            ViewNode obj = (ViewNode) list.next();
            if (obj.getResourceInfo().getName().equals(name))
                return obj;
        }
        return null;
    }

    public ViewNode findObjectById(String id)
    {
        if( id.endsWith( "/") && id.length() > 1 )
            id = id.substring(0, id.length()-1);
        if( id.equals( getResourceInfo().getIdentification() ))
            return this;
        Iterator list = m_Children.iterator();
        while (list.hasNext())
        {
            ViewNode obj = (ViewNode) list.next();
            String childId = obj.getResourceInfo().getIdentification();
            if ( childId.equals(id) )
                return obj;
            if( id.startsWith( childId ) && ( obj instanceof ParentNode ) )
            {
                return ((ParentNode) obj).findObjectById( id );
            }
        }
        return null;
    }
}