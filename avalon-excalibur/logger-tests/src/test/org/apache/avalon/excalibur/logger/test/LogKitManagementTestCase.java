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
package org.apache.avalon.excalibur.logger.test;

import junit.swingui.TestRunner;

import org.apache.avalon.excalibur.testcase.CascadingAssertionFailedError;
import org.apache.avalon.excalibur.testcase.ExcaliburTestCase;

/**
 * LogKitManagementTest.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.1 $ $Date: 2004/03/17 13:22:38 $
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
        LogKitTestComponent tc = null;

        try
        {
            tc = (LogKitTestComponent)lookup( LogKitTestComponent.ROLE + "/A" );
            tc.test( getLogEnabledLogger(), "Test log entry A" );
        }
        catch( Exception e )
        {
            throw new CascadingAssertionFailedError( "There was an error in the LogKitManagement test", e );
        }
        finally
        {
            assertTrue( "The test component could not be retrieved.", null != tc );
            release( tc );
        }

        try
        {
            tc = (LogKitTestComponent)lookup( LogKitTestComponent.ROLE + "/B" );
            tc.test( getLogEnabledLogger(), "Test log entry B" );
        }
        catch( Exception e )
        {
            throw new CascadingAssertionFailedError( "There was an error in the LogKitManagement test", e );
        }
        finally
        {
            assertTrue( "The test component could not be retrieved.", null != tc );
            release( tc );
        }

        try
        {
            tc = (LogKitTestComponent)lookup( LogKitTestComponent.ROLE + "/C" );
            tc.test( getLogEnabledLogger(), "Test log entry C" );
        }
        catch( Exception e )
        {
            throw new CascadingAssertionFailedError( "There was an error in the LogKitManagement test", e );
        }
        finally
        {
            assertTrue( "The test component could not be retrieved.", null != tc );
            release( tc );
        }
    }
}
