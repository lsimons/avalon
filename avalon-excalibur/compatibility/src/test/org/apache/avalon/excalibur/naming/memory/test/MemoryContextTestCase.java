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
package org.apache.avalon.excalibur.naming.memory.test;

import java.util.Hashtable;

import javax.naming.Context;

import org.apache.avalon.excalibur.naming.memory.MemoryInitialContextFactory;
import org.apache.avalon.excalibur.naming.test.AbstractContextTestCase;

/**
 * Unit testing for Memory system
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.5 $
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/jndikit/
 */
public class MemoryContextTestCase extends AbstractContextTestCase
{
    private int m_id = 0;
    private Context m_rootContext;

    public MemoryContextTestCase( String name )
    {
        super( name );
    }

    protected void setUp()
    {
        try
        {
            final MemoryInitialContextFactory factory = new MemoryInitialContextFactory();
            m_rootContext = factory.getInitialContext( new Hashtable() );
            m_context = m_rootContext.createSubcontext( "test" + m_id++ );
        }
        catch( Exception e )
        {
        }
    }

    protected void tearDown()
    {
        try
        {
            m_context.close();
            m_context = null;
            m_rootContext.close();
        }
        catch( Exception e )
        {
        }
    }
}
