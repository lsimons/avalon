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

package org.apache.avalon.composition.data;

import java.io.Serializable;

/**
 * A DependencyDirective contains information describing how a 
 * dependency should be resolved.  
 *
 * @author <a href="mailto:mcconnell@osm.net">Stephen McConnell</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/09/24 09:31:05 $
 */
public final class DependencyDirective implements Serializable
{

   /**
    * The dependency key that the directive refers to.
    */
    private final String m_key;

   /**
    * The dependency source (possibly null)
    */
    private final String m_source;

   /**
    * The set of features used during selection.
    */
    private final SelectionDirective[] m_features;

   /**
    * Creation of a new dependency directive.
    * 
    * @param key the dependency key
    * @param source path to the source provider component
    */
    public DependencyDirective( String key, String source )
    {
        m_key = key;
        m_source = source;
        m_features = new SelectionDirective[0];
    }

   /**
    * Creation of a new dependency directive.
    * 
    * @param key the dependency key
    * @param features the set of selection directives
    */
    public DependencyDirective( String key, SelectionDirective[] features )
    {
        m_key = key;
        m_features = features;
        m_source = null;
    }

   /**
    * Return the dependency key.
    * @return the key
    */
    public String getKey()
    {
        return m_key;
    }

   /**
    * Return the dependency source path.
    * @return the path
    */
    public String getSource()
    {
        return m_source;
    }

   /**
    * Return the set of selection directive constraints.
    * @return the selection directive set
    */
    public SelectionDirective[] getSelectionDirectives()
    {
        return m_features;
    }
}
