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
package org.apache.avalon.excalibur.pool.test;

import junit.framework.TestCase;

import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.excalibur.pool.ResourceLimitingPool;
import org.apache.avalon.excalibur.testcase.BufferedLogger;

/**
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.6 $ $Date: 2003/06/16 06:25:36 $
 * @since 4.1
 */
public final class ResourceLimitingPoolTestCase extends TestCase
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public ResourceLimitingPoolTestCase()
    {
        this( "ResourceLimitingPool Test Case" );
    }

    public ResourceLimitingPoolTestCase( final String name )
    {
        super( name );
    }

    /*---------------------------------------------------------------
     * TestCases
     *-------------------------------------------------------------*/
    public void testCreateDestroy()
    {
        BufferedLogger logger = new BufferedLogger();
        ClassInstanceObjectFactory factory =
            new ClassInstanceObjectFactory( PoolableTestObject.class, logger );
        ResourceLimitingPool pool = new ResourceLimitingPool( factory, 0, false, false, 0, 0 );

        pool.enableLogging( logger );
        pool.dispose();

        // Make sure the logger output check out.
        assertEquals(
            logger.toString(),
            ""
        );
    }

    public void testSingleGetPut() throws Exception
    {
        BufferedLogger logger = new BufferedLogger();
        ClassInstanceObjectFactory factory =
            new ClassInstanceObjectFactory( PoolableTestObject.class, logger );
        ResourceLimitingPool pool = new ResourceLimitingPool( factory, 0, false, false, 0, 0 );

        pool.enableLogging( logger );

        assertEquals( "1) Pool Ready Size", 0, pool.getReadySize() );
        assertEquals( "1) Pool Size", 0, pool.getSize() );

        Poolable p = pool.get();

        assertEquals( "2) Pool Ready Size", 0, pool.getReadySize() );
        assertEquals( "2) Pool Size", 1, pool.getSize() );

        pool.put( p );

        assertEquals( "3) Pool Ready Size", 1, pool.getReadySize() );
        assertEquals( "3) Pool Size", 1, pool.getSize() );

        pool.dispose();

        // Make sure the logger output check out.
        assertEquals( "Logger output",
                      "DEBUG - ClassInstanceObjectFactory.newInstance()  id:1\n" +
                      "DEBUG - Created a new org.apache.avalon.excalibur.pool.test.PoolableTestObject from the object factory.\n" +
                      "DEBUG - Got a org.apache.avalon.excalibur.pool.test.PoolableTestObject from the pool.\n" +
                      "DEBUG - Put a org.apache.avalon.excalibur.pool.test.PoolableTestObject back into the pool.\n" +
                      "DEBUG - ClassInstanceObjectFactory.decommission(a org.apache.avalon.excalibur.pool.test.PoolableTestObject)  id:1\n",
                      logger.toString()
        );
    }

    public void testSingleGetPutPoolCheck() throws Exception
    {
        BufferedLogger logger = new BufferedLogger();
        ClassInstanceObjectFactory factory =
            new ClassInstanceObjectFactory( PoolableTestObject.class, logger );
        ResourceLimitingPool pool = new ResourceLimitingPool( factory, 0, false, false, 0, 0 );

        pool.enableLogging( logger );

        assertEquals( "1) Pool Ready Size", 0, pool.getReadySize() );
        assertEquals( "1) Pool Size", 0, pool.getSize() );

        Poolable p1 = pool.get();

        assertEquals( "2) Pool Ready Size", 0, pool.getReadySize() );
        assertEquals( "2) Pool Size", 1, pool.getSize() );

        pool.put( p1 );

        assertEquals( "3) Pool Ready Size", 1, pool.getReadySize() );
        assertEquals( "3) Pool Size", 1, pool.getSize() );

        Poolable p2 = pool.get();

        assertEquals( "4) Pool Ready Size", 0, pool.getReadySize() );
        assertEquals( "4) Pool Size", 1, pool.getSize() );

        assertEquals( "Pooled Object reuse check", p1, p2 );

        pool.put( p2 );

        assertEquals( "5) Pool Ready Size", 1, pool.getReadySize() );
        assertEquals( "5) Pool Size", 1, pool.getSize() );

        pool.dispose();

        // Make sure the logger output check out.
        assertEquals( "Logger output",
                      "DEBUG - ClassInstanceObjectFactory.newInstance()  id:1\n" +
                      "DEBUG - Created a new org.apache.avalon.excalibur.pool.test.PoolableTestObject from the object factory.\n" +
                      "DEBUG - Got a org.apache.avalon.excalibur.pool.test.PoolableTestObject from the pool.\n" +
                      "DEBUG - Put a org.apache.avalon.excalibur.pool.test.PoolableTestObject back into the pool.\n" +
                      "DEBUG - Got a org.apache.avalon.excalibur.pool.test.PoolableTestObject from the pool.\n" +
                      "DEBUG - Put a org.apache.avalon.excalibur.pool.test.PoolableTestObject back into the pool.\n" +
                      "DEBUG - ClassInstanceObjectFactory.decommission(a org.apache.avalon.excalibur.pool.test.PoolableTestObject)  id:1\n",
                      logger.toString()
        );
    }

    public void testMultipleGetPut() throws Exception
    {
        BufferedLogger logger = new BufferedLogger();
        ClassInstanceObjectFactory factory =
            new ClassInstanceObjectFactory( PoolableTestObject.class, logger );
        ResourceLimitingPool pool = new ResourceLimitingPool( factory, 0, false, false, 0, 0 );

        pool.enableLogging( logger );

        assertEquals( "1) Pool Ready Size", 0, pool.getReadySize() );
        assertEquals( "1) Pool Size", 0, pool.getSize() );

        Poolable p1 = pool.get();

        assertEquals( "2) Pool Ready Size", 0, pool.getReadySize() );
        assertEquals( "2) Pool Size", 1, pool.getSize() );

        Poolable p2 = pool.get();

        assertEquals( "3) Pool Ready Size", 0, pool.getReadySize() );
        assertEquals( "3) Pool Size", 2, pool.getSize() );

        pool.put( p1 );

        assertEquals( "4) Pool Ready Size", 1, pool.getReadySize() );
        assertEquals( "4) Pool Size", 2, pool.getSize() );

        pool.put( p2 );

        assertEquals( "5) Pool Ready Size", 2, pool.getReadySize() );
        assertEquals( "5) Pool Size", 2, pool.getSize() );

        pool.dispose();

        // Make sure the logger output check out.
        assertEquals( "Logger output",
                      "DEBUG - ClassInstanceObjectFactory.newInstance()  id:1\n" +
                      "DEBUG - Created a new org.apache.avalon.excalibur.pool.test.PoolableTestObject from the object factory.\n" +
                      "DEBUG - Got a org.apache.avalon.excalibur.pool.test.PoolableTestObject from the pool.\n" +
                      "DEBUG - ClassInstanceObjectFactory.newInstance()  id:2\n" +
                      "DEBUG - Created a new org.apache.avalon.excalibur.pool.test.PoolableTestObject from the object factory.\n" +
                      "DEBUG - Got a org.apache.avalon.excalibur.pool.test.PoolableTestObject from the pool.\n" +
                      "DEBUG - Put a org.apache.avalon.excalibur.pool.test.PoolableTestObject back into the pool.\n" +
                      "DEBUG - Put a org.apache.avalon.excalibur.pool.test.PoolableTestObject back into the pool.\n" +
                      "DEBUG - ClassInstanceObjectFactory.decommission(a org.apache.avalon.excalibur.pool.test.PoolableTestObject)  id:1\n" +
                      "DEBUG - ClassInstanceObjectFactory.decommission(a org.apache.avalon.excalibur.pool.test.PoolableTestObject)  id:2\n",
                      logger.toString()
        );
    }
    
    public void testFailingGets() throws Exception
    {
        BufferedLogger logger = new BufferedLogger();
        ClassInstanceObjectFactory factory =
            new ClassInstanceObjectFactory( FailingPoolableTestObject.class, logger );
        ResourceLimitingPool pool = new ResourceLimitingPool( factory, 3, true, true, 5000, 0 );

        pool.enableLogging( logger );
        Poolable p1;
        
        assertEquals( "1) Pool Ready Size", 0, pool.getReadySize() );
        assertEquals( "1) Pool Size", 0, pool.getSize() );
        
        try
        {
            p1 = pool.get();
            fail( "1) call to get should have failed." );
        }
        catch ( IllegalStateException e )
        {
            // Expected
        }
        
        assertEquals( "2) Pool Ready Size", 0, pool.getReadySize() );
        assertEquals( "2) Pool Size", 0, pool.getSize() );
        
        try
        {
            p1 = pool.get();
            fail( "2) call to get should have failed." );
        }
        catch ( IllegalStateException e )
        {
            // Expected
        }
        
        assertEquals( "3) Pool Ready Size", 0, pool.getReadySize() );
        assertEquals( "3) Pool Size", 0, pool.getSize() );
        
        try
        {
            p1 = pool.get();
            fail( "3) call to get should have failed." );
        }
        catch ( IllegalStateException e )
        {
            // Expected
        }
        
        assertEquals( "4) Pool Ready Size", 0, pool.getReadySize() );
        assertEquals( "4) Pool Size", 0, pool.getSize() );
        
        try
        {
            p1 = pool.get();
            fail( "4) call to get should have failed." );
        }
        catch ( IllegalStateException e )
        {
            // Expected
        }
        
        logger.debug( "OK" );
        
        pool.dispose();
        
        // Make sure the logger output check out.
        assertEquals( "Logger output",
                      "DEBUG - OK\n",
                      logger.toString()
        );
    }
}

