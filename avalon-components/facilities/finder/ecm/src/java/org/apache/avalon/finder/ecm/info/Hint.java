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

package org.apache.avalon.finder.ecm.info;

/**
 * Immutable descriptor of a hint.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/04/04 15:00:55 $
 */
public class Hint
{
    private final String m_shorthand;
    private final String m_classname;

    public Hint( final String shorthand, String classname )
    {
        m_shorthand = shorthand;
        m_classname = classname;
    }

    public String getShorthand()
    {
        return m_shorthand;
    }

    public String getClassname()
    {
        return m_classname;
    }
}
