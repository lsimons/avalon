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

package org.apache.avalon.tools.project;


/**
 * Project info.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class Policy 
{
    public static final boolean ENABLED = true;
    public static final boolean DISABLED = false;

    private final boolean m_build;
    private final boolean m_test;
    private final boolean m_runtime;

    public Policy( boolean build, boolean test, boolean runtime )
    {
        m_build = build;
        m_test = test;
        m_runtime = runtime;
    }

    public boolean isBuildEnabled()
    {
        return m_build;
    }
    
    public boolean isTestEnabled()
    {
        return m_test;
    }

    public boolean isRuntimeEnabled()
    {
        return m_runtime;
    }

    public boolean equals( Object other )
    {
        if( other instanceof Policy )
        {
            Policy policy = (Policy) other;
            if( m_build != policy.m_build ) return false;
            if( m_test != policy.m_test ) return false;
            if( m_runtime != policy.m_runtime ) return false;
            return true;
        }
        return false;
    }
}
