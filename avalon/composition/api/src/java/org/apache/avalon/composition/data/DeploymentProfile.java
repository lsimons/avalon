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
 * Abstract base class for ComponentProfile and ContainmentProfile.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.10 $ $Date: 2004/03/10 10:52:17 $
 */
public abstract class DeploymentProfile implements Serializable
{
    public static final int DEFAULT = -1;
    public static final int ENABLED = 1;
    public static final int DISABLED = 0;

    //--------------------------------------------------------------------------
    // state
    //--------------------------------------------------------------------------

    /**
     * The name of the component profile. This is an
     * abstract name used during assembly.
     */
    private String m_name;

    /**
     * The activation policy.
     */
    private final int m_activation;

   /**
    * The mode under which this profile was established.
    */
    private final Mode m_mode;

    //--------------------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------------------

    public DeploymentProfile( 
      final String name, int activation, Mode mode ) 
    {
        m_activation = activation;
        if( mode != null )
        {
            m_mode = Mode.IMPLICIT;
        }
        else
        {
            m_mode = mode;
        }
        
        if( name == null )
        {
            m_name = "untitled";
        }
        else
        {
            m_name = name;
        }
    }

    //--------------------------------------------------------------------------
    // implementation
    //--------------------------------------------------------------------------

    /**
     * Return the name of meta-data instance.
     *
     * @return the name of the component.
     */
    public String getName()
    {
        return m_name;
    }

   /**
    * Get the activation directive for the profile.
    *
    * @return the declared activation policy
    * @see #DEFAULT
    * @see #ENABLED
    * @see #DISABLED 
    */
    public int getActivationDirective()
    {
        return m_activation;
    }

    /**
     * Returns the creation mode for this profile.
     * @return a value of EXPLICIT, PACKAGED or IMPLICIT
     */
    public Mode getMode()
    {
        return m_mode;
    }

    /**
     * Returns a string representation of the profile.
     * @return a string representation
     */
    public String toString()
    {
        return "[" + getName() + "]";
    }
}
