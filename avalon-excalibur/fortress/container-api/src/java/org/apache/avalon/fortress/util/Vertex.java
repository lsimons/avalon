/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

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
package org.apache.avalon.fortress.util.dag;

import java.util.ArrayList;
import java.util.List;

/**
 * Vertex is used to track dependencies and each node in a graph.  Typical
 * uses would be to ensure components are started up and torn down in the
 * proper order, or bundles were loaded and unloaded in the proper order, etc.
 *
 * @author <a href="bloritsch.at.d-haven.org">Berin Loritsch</a>
 * @version CVS $ Revision: 1.1 $
 */
public final class Vertex implements Comparable
{
    private final Object m_node;
    private int m_indegrees;
    private int m_currentIndegrees;
    private int m_order;
    private final List m_dependencies;

    /**
     * A vertex wraps a node, which can be anything.
     *
     * @param node  The wrapped node.
     */
    public Vertex( final Object node )
    {
        m_node = node;
        m_indegrees = 0;
        m_order = 0;
        m_dependencies = new ArrayList();
        reset();
    }

    /**
     * Mark this vertex so that it knows it is referenced.  It is used to
     * determine the number of indegrees this vertex has.
     */
    private void isReferenced()
    {
        m_indegrees++;
        m_currentIndegrees++;
    }

    /**
     * Reset the Vertex so that all the flags and runtime states are set back
     * to the original values.
     */
    public void reset()
    {
        m_currentIndegrees = m_indegrees;
        m_order = 0;
    }

    /**
     * Provide an ordinal or order number for the vertex, used in properly
     * sorting the verteces.
     *
     * @param orderNum  The ordinal for this vertex.
     */
    public void setOrder( int orderNum )
    {
        m_order = orderNum;
    }

    /**
     * Get the number of indegrees for this Vertex.  An indegree is an incomming
     * edge, or a Vertex that depends on this one.
     *
     * @return  The current number of tracked indegrees
     */
    public int getIndegrees()
    {
        return m_currentIndegrees;
    }

    /**
     * Account for one of the indegrees.  This decrements the current number of
     * tracked indegrees, and is used in the topological sort algorithm.
     */
    public void accountForIndegree()
    {
        m_currentIndegrees--;
    }

    /**
     * Get the wrapped node that this Vertex represents.
     *
     * @return the node
     */
    public Object getNode()
    {
        return m_node;
    }

    /**
     * Add a dependecy to this Vertex.  The Vertex that this one depends on will
     * be marked as referenced and then added to the list of dependencies.  The
     * list is checked before the dependency is added.
     *
     * @param v  The vertex we depend on.
     */
    public void addDependency( Vertex v )
    {
        if ( !m_dependencies.contains( v ) )
        {
            m_dependencies.add( v );
            v.isReferenced();
        }
    }

    /**
     * Get the list of dependencies.
     *
     * @return  The list of dependencies.
     */
    public List getDependencies()
    {
        return m_dependencies;
    }

    /**
     * Used in the sort algorithm to sort all the Verteces so that they respect
     * the ordinal they were given during the topological sort.
     *
     * @param o  The other Vertex to compare with
     * @return -1 if this < o, 0 if this == o, or 1 if this > o
     */
    public int compareTo( final Object o )
    {
        final Vertex other = (Vertex) o;
        int orderInd = 0;

        if ( m_order < other.m_order ) orderInd = -1;
        if ( m_order > other.m_order ) orderInd = 1;

        return orderInd;
    }

    /**
     * Get the ordinal for this vertex.
     *
     * @return  the order.
     */
    public int getOrder()
    {
        return m_order;
    }
}
