/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:
 
 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
 
 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.
 
 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the 
 Apache Software Foundation, please see <http://www.apache.org/>.
 
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
 * @author Peter Donald
 * @version $Revision: 1.2 $
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
            if( methods[ i ].getName().startsWith( "test" ) )
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
