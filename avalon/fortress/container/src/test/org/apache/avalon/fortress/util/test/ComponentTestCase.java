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
package org.apache.avalon.fortress.util.test;

import junit.framework.TestCase;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.NullLogger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.DefaultServiceManager;
import org.apache.avalon.fortress.test.util.FullLifecycleComponent;

/**
 * This class provides basic facilities for enforcing Avalon's contracts
 * within your own code.
 *
 * @author <a href="bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/04/21 17:52:09 $
 */
public final class ComponentTestCase
    extends TestCase
{
    public ComponentTestCase( String test )
    {
        super( test );
    }

    public void testCorrectLifecycle()
        throws Exception
    {
        final FullLifecycleComponent component = new FullLifecycleComponent();
        component.enableLogging( new NullLogger() );
        component.contextualize( new DefaultContext() );
        component.service( new DefaultServiceManager() );
        component.configure( new DefaultConfiguration( "", "" ) );
        component.parameterize( new Parameters() );
        component.initialize();
        component.start();
        component.suspend();
        component.resume();
        component.stop();
        component.dispose();
    }

    public void testMissingLogger()
        throws Exception
    {
        FullLifecycleComponent component = new FullLifecycleComponent();

        try
        {
            component.contextualize( new DefaultContext() );
        }
        catch ( Exception e )
        {
            return;
        }
        fail( "Did not detect missing logger" );
    }

    public void testOutOfOrderInitialize()
        throws Exception
    {
        final org.apache.avalon.fortress.util.test.FullLifecycleComponent component = new org.apache.avalon.fortress.util.test.FullLifecycleComponent();
        component.enableLogging( new NullLogger() );
        component.contextualize( new DefaultContext() );
        component.service( new DefaultServiceManager() );
        try
        {
            component.initialize();
            component.parameterize( new Parameters() );
        }
        catch ( Exception e )
        {
            return;
        }
        fail( "Did not detect out of order initialization" );
    }

    public void testOutOfOrderDispose()
        throws Exception
    {
        final org.apache.avalon.fortress.util.test.FullLifecycleComponent component = new org.apache.avalon.fortress.util.test.FullLifecycleComponent();
        component.enableLogging( new NullLogger() );
        component.contextualize( new DefaultContext() );
        component.service( new DefaultServiceManager() );
        component.configure( new DefaultConfiguration( "", "" ) );
        component.parameterize( new Parameters() );
        component.initialize();
        component.start();
        component.suspend();
        component.resume();

        try
        {
            component.dispose();
            component.stop();
        }
        catch ( Exception e )
        {
            return;
        }
        fail( "Did not detect out of order disposal" );
    }

    public void testDoubleAssignOfLogger()
    {
        final org.apache.avalon.fortress.util.test.FullLifecycleComponent component = new org.apache.avalon.fortress.util.test.FullLifecycleComponent();
        try
        {
            component.enableLogging( new NullLogger() );
            component.enableLogging( new NullLogger() );
        }
        catch ( Exception e )
        {
            // test successfull
            return;
        }

        fail( "Did not detect double assignment of Logger" );
    }

    public void testDoubleAssignOfContext()
    {
        final org.apache.avalon.fortress.util.test.FullLifecycleComponent component = new org.apache.avalon.fortress.util.test.FullLifecycleComponent();
        component.enableLogging( new NullLogger() );
        try
        {
            component.contextualize( new DefaultContext() );
            component.contextualize( new DefaultContext() );
        }
        catch ( Exception e )
        {
            // test successfull
            return;
        }

        fail( "Did not detect double assignment of Context" );
    }

    public void testDoubleAssignOfParameters()
        throws Exception
    {
        final org.apache.avalon.fortress.util.test.FullLifecycleComponent component = new org.apache.avalon.fortress.util.test.FullLifecycleComponent();
        component.enableLogging( new NullLogger() );
        component.contextualize( new DefaultContext() );
        component.service( new DefaultServiceManager() );
        component.configure( new DefaultConfiguration( "", "" ) );

        try
        {
            component.parameterize( new Parameters() );
            component.parameterize( new Parameters() );
        }
        catch ( Exception e )
        {
            // test successfull
            return;
        }

        fail( "Did not detect double assignment of Parameters" );
    }

    public void testDoubleAssignOfConfiguration() throws Exception
    {
        final org.apache.avalon.fortress.util.test.FullLifecycleComponent component = new org.apache.avalon.fortress.util.test.FullLifecycleComponent();
        component.enableLogging( new NullLogger() );
        component.contextualize( new DefaultContext() );
        component.service( new DefaultServiceManager() );
        try
        {
            component.configure( new DefaultConfiguration( "", "" ) );
            component.configure( new DefaultConfiguration( "", "" ) );
        }
        catch ( Exception e )
        {
            // test successfull
            return;
        }

        fail( "Did not detect double assignment of Configuration" );
    }

    public void testDoubleAssignOfComponentManger()
        throws Exception
    {
        final org.apache.avalon.fortress.util.test.FullLifecycleComponent component = new org.apache.avalon.fortress.util.test.FullLifecycleComponent();
        component.enableLogging( new NullLogger() );
        component.contextualize( new DefaultContext() );
        try
        {
            component.service( new DefaultServiceManager() );
            component.service( new DefaultServiceManager() );
        }
        catch ( Exception e )
        {
            // test successfull
            return;
        }

        fail( "Did not detect double assignment of ComponentLocator" );
    }
}
