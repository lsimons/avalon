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

package org.apache.avalon.tools.model;

import org.apache.tools.ant.BuildException;

/**
 * Project info.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class Gump 
{
    public static final Gump NULL_GUMP = new Gump( null, null, false, false );

    private String m_alias;
    private String m_id;
    private boolean m_ignore;
    private boolean m_classpath;

    public Gump( final String alias, final String id, boolean classpath, boolean ignore )
    {
        m_alias = alias;
        m_id = id;
        m_ignore = ignore;
        m_classpath = classpath;
    }

   /**
    * Gump alias name.
    */
    public String getAlias()
    {
        return m_alias;
    }

   /**
    * Gump project artifact id.
    */
    public String getId()
    {
        return m_id;
    }

   /**
    * Return true if this defintion can be ignored when 
    * building a gump project dependency.
    */
    public boolean isIgnorable()
    {
        return m_ignore;
    }

   /**
    * Return true if this defintion is required as part of the 
    * classpath established by gump.
    */
    public boolean isClasspathEntry()
    {
        return m_classpath;
    }
}
