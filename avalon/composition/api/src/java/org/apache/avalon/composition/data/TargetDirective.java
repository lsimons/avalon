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

import org.apache.avalon.composition.data.GrantDirective;

import org.apache.avalon.framework.configuration.Configuration;

import org.apache.avalon.logging.data.CategoriesDirective;
import org.apache.avalon.logging.data.CategoryDirective;

/**
 * <p>A target is a tagged configuration fragment.  The tag is a path
 * seperated by "/" charaters qualifying the component that the target
 * configuration is to be applied to.</p>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.5 $ $Date: 2004/02/25 22:54:09 $
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

    /**
     * Supplimentary permissions assignable to a classloader.
     */
    private final GrantDirective m_grant;

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
        m_grant = null;
    }

    /**
     * Create a new Target instance applyable to a container.
     *
     * @param path target container path
     * @param categories the logging category directives 
     * @param grant a grant directive
     */
    public TargetDirective( 
      final String path, 
      final CategoriesDirective categories, 
      final GrantDirective grant )
    {
        m_path = path;
        m_categories = categories;
        m_grant = grant;
        m_config = null;
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
     * Return an optional grant directive that may be applied as 
     * a supplimentary security context during the establishment of 
     * a new container classloader.
     *
     * @return the grant directive (possibly null)
     */
    public GrantDirective getGrantDirective()
    {
        return m_grant;
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
          + (getGrantDirective() != null )
          + " ]";
    }
}
