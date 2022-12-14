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

package org.apache.avalon.meta.compat;

import org.apache.avalon.meta.info.Type;
import org.apache.avalon.meta.info.builder.TypeBuilder;
import junit.framework.TestCase;

public class CompatTestCase extends TestCase
{
    private Type m_type;

    public CompatTestCase( )
    {
        this( "compat" );
    }

    public CompatTestCase( String name )
    {
        super( name );
    }

    protected void setUp() throws Exception
    {
        TypeBuilder builder = new TypeBuilder();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Class clazz = loader.loadClass( "org.apache.avalon.meta.compat.Test" );
        m_type = builder.buildType( clazz );
    }

    public void testName() throws Exception
    {
        assertTrue( m_type.getInfo().getName().equals( "test" ) );
    }

    public void testLifestyle() throws Exception
    {
        assertTrue( m_type.getInfo().getLifestyle().equals( "singleton" ) );
    }

    public void testClassName() throws Exception
    {
        assertTrue( 
          m_type.getInfo().getClassname().equals( "org.apache.avalon.meta.compat.Test" ) );
    }
}
