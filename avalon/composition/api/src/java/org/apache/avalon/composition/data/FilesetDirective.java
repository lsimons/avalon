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
 * <p>A fileset directive is a scoped defintion of a set of files.  A fileset
 * a structurally defined as a base directory and a set of relative filenames
 * represented as include directives.</p>
 *
 * <p><b>XML</b></p>
 * <pre>
 *   &lt;fileset dir="<font color="darkred">lib</font>"&gt;
 *     &lt;include name="<font color="darkred">avalon-framework.jar</font>"/&gt;
 *     &lt;include name="<font color="darkred">logkit.jar</font>"/&gt;
 *   &lt;/dirset&gt;
 * </pre>
 *
 * @see IncludeDirective
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.2 $ $Date: 2004/01/24 23:25:24 $
 */
public class FilesetDirective implements Serializable
{
    /**
     * The base directory from which include directives will be resolved.
     */
    private final String m_base;

    /**
     * The set of include directives.
     */
    private final IncludeDirective[] m_includes;

    /**
     * Create a FilesetDirective instance.
     *
     * @param base the base directory path against which includes are evaluated
     * @param includes the set of includes to include in the fileset
     */
    public FilesetDirective( final String base, final IncludeDirective[] includes )
    {
        m_base = base;
        m_includes = includes;
    }

    /**
     * Return the base directory.
     *
     * @return the directory
     */
    public String getBaseDirectory()
    {
        return m_base;
    }

    /**
     * Return the set of include directives.
     *
     * @return the include set
     */
    public IncludeDirective[] getIncludes()
    {
        return m_includes;
    }
}
