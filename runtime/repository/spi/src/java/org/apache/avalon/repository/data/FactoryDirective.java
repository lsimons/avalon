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

package org.apache.avalon.repository.data;

import java.io.Serializable;
import java.util.Properties;

import org.apache.avalon.repository.Artifact;

/**
 * A FactoryDirective combines a artifact with a set of properties to be 
 * applied to the factory criteria.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id$
 */
public class FactoryDirective implements Serializable
{
    //-----------------------------------------------------------
    // immutable state
    //-----------------------------------------------------------

    private final Artifact m_artifact;

    private final Properties m_properties;

    //-----------------------------------------------------------
    // constructor
    //-----------------------------------------------------------

    /**
     * Creates a new FactoryDirective.
     * 
     * @param artifact the factory artifact
     * @param properties the associated properties
     */
    public FactoryDirective( final Artifact artifact, final Properties properties ) 
    {
        m_artifact = artifact;
        m_properties = properties;
    }

    //-----------------------------------------------------------
    // public
    //-----------------------------------------------------------

   /**
    * Return the system artifact.
    * @return the factory artifact reference
    */
    public Artifact getArtifact()
    {
        return m_artifact;
    }

   /**
    * Return the factory properties.
    * @return the factory properties.
    */
    public Properties getProperties()
    {
        return m_properties;
    }
}
