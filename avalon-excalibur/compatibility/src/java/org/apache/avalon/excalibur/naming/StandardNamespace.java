/* 
 * Copyright 2002-2004 The Apache Software Foundation
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
package org.apache.avalon.excalibur.naming;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;

/**
 * Namespace that directly uses NamingManager.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.6 $
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/jndikit/
 */
public class StandardNamespace
        implements Namespace
{
    private NameParser m_nameParser;

    /**
     * Construct a Namespace with specified NameParser.
     *
     * @param nameParser the NameParser for Namespace
     */
    public StandardNamespace( final NameParser nameParser )
    {
        m_nameParser = nameParser;
    }

    public NameParser getNameParser()
    {
        return m_nameParser;
    }

    public Object getStateToBind( final Object object,
            final Name name,
            final Context parent,
            final Hashtable environment )
            throws NamingException
    {
        return NamingManager.getStateToBind( object, name, parent, environment );
    }

    public Object getObjectInstance( final Object object,
            final Name name,
            final Context parent,
            final Hashtable environment )
            throws Exception
    {
        return NamingManager.getObjectInstance( object, name, parent, environment );
    }
}
