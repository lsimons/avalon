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

import javax.naming.NameParser;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.StateFactory;

/**
 * This is the default namespace implementation.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.6 $
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/jndikit/
 */
public class DefaultNamespace
        extends AbstractNamespace
{
    private NameParser m_nameParser;

    public DefaultNamespace( final NameParser nameParser )
    {
        this( nameParser,
                new ObjectFactory[0],
                new StateFactory[0] );
    }

    public DefaultNamespace( final NameParser nameParser,
            final ObjectFactory[] objectFactorySet,
            final StateFactory[] stateFactorySet )
    {
        m_nameParser = nameParser;
        m_objectFactorySet = objectFactorySet;
        m_stateFactorySet = stateFactorySet;
    }

    public NameParser getNameParser()
    {
        return m_nameParser;
    }

    public synchronized void addStateFactory( final StateFactory stateFactory )
    {
        super.addStateFactory( stateFactory );
    }

    public synchronized void addObjectFactory( final ObjectFactory objectFactory )
    {
        super.addObjectFactory( objectFactory );
    }
}
