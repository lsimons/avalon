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

import org.apache.avalon.framework.configuration.Configuration;

import org.apache.avalon.logging.data.CategoriesDirective;
import org.apache.avalon.logging.data.CategoryDirective;

/**
 * <p>A target is a tagged configuration fragment.  The tag is a path
 * seperated by "/" charaters qualifying the component that the target
 * configuration is to be applied to.</p>
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.3 $ $Date: 2004/02/21 13:27:03 $
 */
public class TargetDirective implements Serializable
{
    //========================================================================
    // immutable state
    //========================================================================

    /**
     * The path.
     */
    private final String m_path;

    /**
     * The configuration.
     */
    private final Configuration m_config;

    /**
     * The configuration.
     */
    private final CategoriesDirective m_categories;

    //========================================================================
    // constructors
    //========================================================================

    /**
     * Create a new null Target instance.
     *
     * @param path target path
     */
    public TargetDirective( final String path )
    {
        this( path, null );
    }

    /**
     * Create a new Target instance.
     *
     * @param path target path
     * @param configuration the configuration 
     */
    public TargetDirective( final String path, final Configuration configuration )
    {
        this( path, configuration, null );
    }

    /**
     * Create a new Target instance.
     *
     * @param path target path
     * @param configuration the configuration 
     * @param categories the logging category directives 
     */
    public TargetDirective( 
      final String path, 
      final Configuration configuration, 
      final CategoriesDirective categories )
    {
        m_path = path;
        m_config = configuration;
        m_categories = categories;
    }

    //========================================================================
    // implementation
    //========================================================================

    /**
     * Return the target path.
     *
     * @return the target path
     */
    public String getPath()
    {
        return m_path;
    }

    /**
     * Return the target configuration.
     *
     * @return the target configuration
     */
    public Configuration getConfiguration()
    {
        return m_config;
    }

    /**
     * Return the logging categories directive.
     *
     * @return the logging categories (possibly null)
     */
    public CategoriesDirective getCategoriesDirective()
    {
        return m_categories;
    }

    /**
     * Return a string representation of the target.
     * @return a string representing the target instance
     */
    public String toString()
    {
        return "[target: " + getPath() + ", " 
          + (getConfiguration() != null ) + ", " 
          + (getCategoriesDirective() != null ) + ", " 
          + " ]";
    }

}
