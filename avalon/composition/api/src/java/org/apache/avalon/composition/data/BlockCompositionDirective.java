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

/**
 * A block reference directive contains an identifier and verion of 
 * a local resource to be included by reference into 
 * a container.
 *
 * @author <a href="mailto:mcconnell@avalon.apache.org">Stephen McConnell</a>
 * @version $Revision: 1.3 $ $Date: 2004/01/24 23:25:23 $
 */
public class BlockCompositionDirective extends DeploymentProfile
{
    /**
     * The version identifier.
     */
    private final ResourceDirective m_resource;

    /**
     * Nested targets.
     */
    private final TargetDirective[] m_targets;

    /**
     * Creation of a new resource directive.
     * @param name the name to assign to the container 
     *   established by the composition directive
     * @param resource a resource reference from which a block 
     *   description can be resolved
     */
    public BlockCompositionDirective( 
      final String name, ResourceDirective resource )
    {
        this( name, resource, new TargetDirective[0] );
    }

    /**
     * Creation of a new resource directive.
     * @param name the name to assign to the container 
     *   established by the composition directive
     * @param resource a resource reference from which a block 
     *   description can be resolved
     */
    public BlockCompositionDirective( 
      final String name, ResourceDirective resource, TargetDirective[] targets )
    {
        super( name, true, Mode.EXPLICIT );
        if( resource == null )
        {
            throw new NullPointerException( "resource" );
        }
        m_resource = resource;
        m_targets = targets;
    }

    /**
     * Return the resource reference.
     * @return the resource
     */
    public ResourceDirective getResource()
    {
        return m_resource;
    }

    /**
     * Return the relative targets.
     * @return the targets
     */
    public TargetDirective[] getTargetDirectives()
    {
        return m_targets;
    }
}
