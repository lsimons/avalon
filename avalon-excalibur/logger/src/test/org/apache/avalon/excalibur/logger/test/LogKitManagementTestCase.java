/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

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
package org.apache.avalon.excalibur.logger.test;

import junit.swingui.TestRunner;
import org.apache.avalon.excalibur.testcase.CascadingAssertionFailedError;
import org.apache.avalon.excalibur.testcase.ExcaliburTestCase;

/**
 * LogKitManagementTest.
 *
 * @author <a href="mailto:giacomo@apache,org">Giacomo Pati</a>
 * @version CVS $Revision: 1.3 $ $Date: 2002/08/07 13:37:00 $
 */
public class LogKitManagementTestCase
    extends ExcaliburTestCase
{

    public static void main( final String[] args ) throws Exception
    {
        final String[] testCaseName = {LogKitManagementTestCase.class.getName()};
        TestRunner.main( testCaseName );
    }

    public LogKitManagementTestCase( final String name )
    {
        super( name );
    }

    public void testComponent()
        throws CascadingAssertionFailedError
    {
        TestComponent tc = null;

        try
        {
            tc = (TestComponent)manager.lookup( TestComponent.ROLE + "/A" );
            tc.test( getLogEnabledLogger(), "Test log entry A" );
        }
        catch( Exception e )
        {
            throw new CascadingAssertionFailedError( "There was an error in the LogKitManagement test", e );
        }
        finally
        {
            assertTrue( "The test component could not be retrieved.", null != tc );
            manager.release( tc );
        }

        try
        {
            tc = (TestComponent)manager.lookup( TestComponent.ROLE + "/B" );
            tc.test( getLogEnabledLogger(), "Test log entry B" );
        }
        catch( Exception e )
        {
            throw new CascadingAssertionFailedError( "There was an error in the LogKitManagement test", e );
        }
        finally
        {
            assertTrue( "The test component could not be retrieved.", null != tc );
            manager.release( tc );
        }

        try
        {
            tc = (TestComponent)manager.lookup( TestComponent.ROLE + "/C" );
            tc.test( getLogEnabledLogger(), "Test log entry C" );
        }
        catch( Exception e )
        {
            throw new CascadingAssertionFailedError( "There was an error in the LogKitManagement test", e );
        }
        finally
        {
            assertTrue( "The test component could not be retrieved.", null != tc );
            manager.release( tc );
        }
    }
}
