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
 * Delcaration of a repository resource reference.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class ResourceRef 
{
    private String m_key;
    private Policy m_policy;
    private String m_tag;

    public ResourceRef( String key )
    {
        this( key, new Policy(), null );
    }

    public ResourceRef( String key, Policy policy, String tag )
    {
        m_key = key;
        m_policy = policy;
        if( null == tag )
        {
            m_tag = "impl";
        }
        else if( "".equals( tag ) )
        {
            m_tag = "impl";
        }
        else
        {
            m_tag = tag;
        }
    }

    public String getKey()
    {
        return m_key;
    }

    public String getTag()
    {
        return m_tag;
    }

    public Policy getPolicy()
    {
        return m_policy;
    }

    public boolean equals( Object other )
    {
        if( other instanceof ResourceRef )
        {
            ResourceRef ref = (ResourceRef) other;
            if( !getKey().equals( ref.getKey() ) ) return false;
            if( !getPolicy().equals( ref.getPolicy() ) ) return false;
            return true;
        }
        return false;
    }

    public String toString()
    {
        return "[resource key=\"" + getKey() + "\"]";
    }
}
