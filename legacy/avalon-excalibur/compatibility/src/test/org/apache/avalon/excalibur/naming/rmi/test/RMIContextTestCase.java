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
package org.apache.avalon.excalibur.naming.rmi.test;

import java.lang.reflect.Method;
import java.util.Hashtable;
import javax.naming.Context;

import org.apache.avalon.excalibur.naming.rmi.RMIInitialContextFactory;
import org.apache.avalon.excalibur.naming.rmi.server.Main;
import org.apache.avalon.excalibur.naming.test.AbstractContextTestCase;

/**
 * Unit testing for JNDI system
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.6 $
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/jndikit/
 */
public class RMIContextTestCase
        extends AbstractContextTestCase
{
    private static int m_numTests = 0;
    private static int m_id = 0;
    private static Main m_server = new Main();
    private static Thread m_serverThread;
    private Context m_rootContext;
    private static boolean m_setUp = false;

    static
    {
        Class testCase = AbstractContextTestCase.class;

        Method[] methods = testCase.getMethods();

        for( int i = 0; i < methods.length; i++ )
        {
            if( methods[i].getName().startsWith( "test" ) )
            {
                RMIContextTestCase.m_numTests++;
            }
        }
    }

    public RMIContextTestCase( String name )
    {
        super( name );
    }

    public void setUp()
    {
        try
        {
            if( !RMIContextTestCase.m_setUp )
            {
                RMIContextTestCase.m_server.start();

                RMIContextTestCase.m_serverThread = new Thread( m_server );
                RMIContextTestCase.m_serverThread.start();
                RMIContextTestCase.m_setUp = true;
            }

            final RMIInitialContextFactory factory = new RMIInitialContextFactory();
            m_rootContext = factory.getInitialContext( new Hashtable() );

            m_context = m_rootContext.createSubcontext( "test" + RMIContextTestCase.m_id++ );
        }
        catch( final Exception e )
        {
            System.out.println( "Failed test initialisation " + e );
            e.printStackTrace();
        }
    }

    public void tearDown()
    {
        try
        {
            m_context.close();
            m_context = null;
            m_rootContext.close();

            if( RMIContextTestCase.m_id >= RMIContextTestCase.m_numTests )
            {
                RMIContextTestCase.m_server.stop();
                RMIContextTestCase.m_serverThread.interrupt();
            }
        }
        catch( final Exception e )
        {
            System.out.println( "Failed test destruction" + e );
            e.printStackTrace();
        }
    }
}
