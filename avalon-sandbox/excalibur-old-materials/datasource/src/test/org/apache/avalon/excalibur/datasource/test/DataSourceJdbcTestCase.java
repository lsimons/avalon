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
package org.apache.avalon.excalibur.datasource.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import org.apache.avalon.excalibur.testcase.CascadingAssertionFailedError;
import org.apache.avalon.excalibur.testcase.ExcaliburTestCase;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;

import EDU.oswego.cs.dl.util.concurrent.CyclicBarrier;

/**
 * Test the DataSource Component.  I don't know how to make this generic,
 * so I'll throw some bones out there, and hope someone can set this up
 * better.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class DataSourceJdbcTestCase
    extends ExcaliburTestCase
{
    protected boolean isSuccessful;
    protected CyclicBarrier barrier;
    protected int connectionCount;

    public DataSourceJdbcTestCase( String name )
    {
        super( name );
    }

    public void testOverAllocation()
    {
        DataSourceComponent ds = null;
        this.isSuccessful = false;
        LinkedList connectionList = new LinkedList();

        try
        {
            ds = (DataSourceComponent)manager.lookup( DataSourceComponent.ROLE );

            for( int i = 0; i < 10; i++ )
            {
                connectionList.add( ds.getConnection() );
            }
            getLogger().info( "Testing overallocation of connections.  Should see a warning next." );
            connectionList.add( ds.getConnection() );
        }
        catch( SQLException se )
        {
            this.isSuccessful = true;
            getLogger().info( "The test was successful" );
        }
        catch( ComponentException ce )
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "There was an error in the OverAllocation test", ce );
            }

            throw new CascadingAssertionFailedError( "There was an error in the OverAllocation test", ce );
        }
        finally
        {
            assertTrue( "The DataSourceComponent could not be retrieved.", null != ds );

            Iterator connections = connectionList.iterator();

            while( connections.hasNext() )
            {
                try
                {
                    ( (Connection)connections.next() ).close();
                }
                catch( SQLException se )
                {
                    // ignore
                }
            }

            connectionList.clear();

            manager.release( (Component)ds );
        }

        assertTrue( "Exception was not thrown when too many datasource components were retrieved.", this.isSuccessful );
    }

    public void testNormalUse()
    {
        DataSourceComponent ds = null;
        this.isSuccessful = true;

        try
        {
            ds = (DataSourceComponent)manager.lookup( DataSourceComponent.ROLE );

            this.connectionCount = 0;
            this.barrier = new CyclicBarrier( 11 );

            for( int i = 0; i < 10; i++ )
            {
                ( new Thread( new ConnectionThread( this, ds ) ) ).start();
            }

            try
            {
                this.barrier.barrier();
            }
            catch( Exception ie )
            {
                // Ignore
            }

            getLogger().info( "The normal use test passed with " + this.connectionCount + " requests and 10 concurrent threads running" );
        }
        catch( ComponentException ce )
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "There was an error in the normal use test", ce );
            }

            throw new CascadingAssertionFailedError( "There was an error in the normal use test", ce );
        }
        finally
        {
            assertTrue( "The DataSourceComponent could not be retrieved.", null != ds );

            manager.release( (Component)ds );
        }

        assertTrue( "Normal use test failed", this.isSuccessful );
    }

    static class ConnectionThread
        implements Runnable
    {
        protected DataSourceComponent datasource;
        protected DataSourceJdbcTestCase testcase;

        ConnectionThread( DataSourceJdbcTestCase testcase,
                          final DataSourceComponent datasource )
        {
            this.datasource = datasource;
            this.testcase = testcase;
        }

        public void run()
        {
            long end = System.currentTimeMillis() + 5000; // run for 5 seconds
            Random rnd = new Random();

            while( System.currentTimeMillis() < end && this.testcase.isSuccessful )
            {
                try
                {
                    Connection con = this.datasource.getConnection();
                    Thread.sleep( (long)rnd.nextInt( 100 ) ); // sleep for up to 100ms
                    con.close();
                    this.testcase.connectionCount++;
                }
                catch( final SQLException se )
                {
                    this.testcase.isSuccessful = false;
                    this.testcase.getLogger().info( "Failed to get Connection, test failed", se );
                }
                catch( final InterruptedException ie )
                {
                    // Ignore
                }
            }

            try
            {
                this.testcase.barrier.barrier();
            }
            catch( final InterruptedException ie )
            {
                // Ignore
            }
        }
    }
}

