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
 * Description of repository requests.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.3 $ $Date: 2004/01/24 23:25:24 $
 */
public final class RepositoryDirective implements Serializable
{
     private static final ResourceDirective[] EMPTY_RESOURCES = new ResourceDirective[0]; 

    /**
     * The resource references
     */
    private ResourceDirective[] m_resources;

    /**
     * Create a empty RepositoryDirective.
     */
    public RepositoryDirective()
    {
        this( null );
    }

    /**
     * Create a RepositoryDirective instance.
     *
     * @param resources the resources to be included in a classloader
     */
    public RepositoryDirective( final ResourceDirective[] resources )
    {
        if( resources == null )
        {
            m_resources = EMPTY_RESOURCES;
        }
        else
        {
            m_resources = resources;
        }
    }

    /**
     * Return the set of resource directives.
     *
     * @return the resource directive set
     */
    public ResourceDirective[] getResources()
    {
        return m_resources;
    }
}
