/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.log.output.test;

import junit.framework.TestCase;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.log.output.db.ColumnInfo;
import org.apache.log.output.db.ColumnType;
import org.apache.log.output.db.DefaultDataSource;
import org.apache.log.output.db.DefaultJDBCTarget;
import org.apache.log.output.db.NormalizedJDBCTarget;

/**
 * Test suite for the DB output target.
 *
 * @author Peter Donald
 */
public final class DBTargetTestCase
    extends TestCase
{
    private String m_connectString;
    private String m_userName;
    private String m_userPassword;
    private boolean m_doDBTest;

    public DBTargetTestCase( final String name )
        throws Exception
    {
        super( name );
    }

    public void setUp() throws Exception
    {
        String driverName = System.getProperty("test.db.driver");
        m_connectString = System.getProperty("test.db.jdbc", "");
        m_userName = System.getProperty( "test.db.user", "" );
        m_userPassword = System.getProperty( "test.db.pword", "" );
        m_doDBTest = System.getProperty( "test.db.run", "false" ).equalsIgnoreCase("true");

        if (m_doDBTest)
        {
            Class.forName(driverName);
        }
        else
        {
            System.out.println("[WARNING] Database Testing is not being done");
            System.out.println();
            System.out.println("To enable database testing, please provide the");
            System.out.println("following properties:");
            System.out.println();
            System.out.println( "test.db.driver -> Class name for the JDBC driver" );
            System.out.println( "test.db.jdbc   -> JDBC connect string" );
            System.out.println( "test.db.user   -> User ID" );
            System.out.println( "test.db.pword  -> User password" );
            System.out.println( "test.db.run    -> \"true\"" );
        }
    }

    public void testBasicTarget()
        throws Exception
    {
        if (! m_doDBTest) return;

        final DefaultDataSource dataSource =
            new DefaultDataSource( m_connectString, m_userName, m_userPassword );

        final ColumnInfo[] columns =
            {
                new ColumnInfo( "TIME", ColumnType.TIME, null ),
                new ColumnInfo( "PRIORITY", ColumnType.PRIORITY, null ),
                new ColumnInfo( "CATEGORY", ColumnType.CATEGORY, null ),
                new ColumnInfo( "HOSTNAME", ColumnType.STATIC, "helm.realityforge.net" ),
                new ColumnInfo( "MESSAGE", ColumnType.MESSAGE, null )
            };

        final DefaultJDBCTarget target =
            new DefaultJDBCTarget( dataSource, "log_entrys", columns );

        final Logger logger = getNewLogger( target );
        logger.debug( "Hello" );
    }

    public void testNumericConstants()
        throws Exception
    {
        if ( !m_doDBTest ) return;

        final DefaultDataSource dataSource =
                new DefaultDataSource( m_connectString, m_userName, m_userPassword );

        final ColumnInfo[] columns =
            {
                new ColumnInfo( "TIME", ColumnType.TIME, null ),
                new ColumnInfo( "PRIORITY", ColumnType.PRIORITY, null ),
                new ColumnInfo( "CATEGORY", ColumnType.CATEGORY, null ),
                new ColumnInfo( "HOSTNAME", ColumnType.STATIC, "helm.realityforge.net" ),
                new ColumnInfo( "MESSAGE", ColumnType.MESSAGE, null )
            };

        final NormalizedJDBCTarget target =
            new NormalizedJDBCTarget( dataSource, "log_entrys2", columns );

        final Logger logger = getNewLogger( target );
        logger.debug( "Hello" );
        logger.info( "Hello info" );
        logger.error( "Hello error" );
        logger.fatalError( "Hello fatalError" );
    }

    private Logger getNewLogger( final LogTarget target )
    {
        final Hierarchy hierarchy = new Hierarchy();
        final Logger logger = hierarchy.getLoggerFor( "myCategory" );
        logger.setLogTargets( new LogTarget[]{target} );
        return logger;
    }
}
