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

import java.io.Serializable;
import java.util.Properties;
import javax.naming.CompoundName;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;

/**
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/jndikit/
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public class DefaultNameParser
        implements Serializable, NameParser
{
    protected static Properties c_syntax = new Properties();

    static
    {
        c_syntax.put( "jndi.syntax.direction", "left_to_right" );
        c_syntax.put( "jndi.syntax.ignorecase", "false" );
        c_syntax.put( "jndi.syntax.separator", "/" );
    }

    public Name parse( final String name )
            throws NamingException
    {
        return new CompoundName( name, c_syntax );
    }
}
