/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.log.Priority;
import org.apache.log.format.ExtendedPatternFormatter;
import org.apache.log.format.PatternFormatter;
import org.apache.log.format.RawFormatter;
import org.apache.log.output.AbstractOutputTarget;
import org.apache.log.output.MemoryTarget;
import org.apache.log.output.io.FileTarget;
import org.apache.log.output.io.SafeFileTarget;
import org.apache.log.output.io.StreamTarget;
import org.apache.log.output.io.WriterTarget;
import org.apache.testlet.AbstractTestlet;
import org.apache.log.output.db.*;

/**
 * Test suite for the DB output target.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class DBTargetTestlet
    extends AbstractTestlet
{
    public DBTargetTestlet()
        throws Exception
    {
        Class.forName( "org.postgresql.Driver" );
    }

    public void testBasicTarget()
        throws Exception
    {
        final DefaultDataSource dataSource = 
            new DefaultDataSource( "jdbc:postgresql:avalon-logkit", "avalon", "" );

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
        final DefaultDataSource dataSource = 
            new DefaultDataSource( "jdbc:postgresql:avalon-logkit", "avalon", "" );

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
        logger.setLogTargets( new LogTarget[] { target } );
        return logger;
    }
}
