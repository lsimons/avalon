/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.logger.test;

import junit.swingui.TestRunner;
import org.apache.avalon.excalibur.testcase.CascadingAssertionFailedError;
import org.apache.avalon.excalibur.testcase.ExcaliburTestCase;

/**
 * LogKitManagementTest.
 *
 * @author <a href="mailto:giacomo@apache,org">Giacomo Pati</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 02:34:16 $
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

        // Set the priority for default log output.
        m_logPriority = org.apache.log.Priority.INFO;
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
