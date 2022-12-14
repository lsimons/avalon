/* 
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avalon.composition.data;

import java.io.Serializable;

/**
 * A DependencyDirective contains information describing how a 
 * dependency should be resolved.  
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.3 $ $Date: 2004/02/24 22:18:21 $
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
