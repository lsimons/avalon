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
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.StateFactory;

/**
 * Interface representing Namespace/NamingSystem.
 * Associated with each namespace is a name parser,
 * object factories and state factories.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.6 $
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/jndikit/
 */
public interface Namespace
        extends ObjectFactory, StateFactory
{
    NameParser getNameParser()
            throws NamingException;
}
