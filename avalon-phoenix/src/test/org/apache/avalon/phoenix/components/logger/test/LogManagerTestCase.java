/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

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

package org.apache.avalon.phoenix.components.logger.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import junit.framework.TestCase;
import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.phoenix.components.logger.DefaultLogManager;
import org.apache.avalon.phoenix.interfaces.LogManager;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.avalon.phoenix.BlockContext;

/**
 *  An basic test case for the LogManager.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.10 $ $Date: 2003/03/22 12:07:17 $
 */
public class LogManagerTestCase
    extends TestCase
{
    public static final String DEFAULT_LOGFILE = "logs/default.log";
    public static final String BLOCK_LOGFILE = "logs/myBlock.log";

    private File m_baseDirectory;

    public LogManagerTestCase( final String name )
    {
        super( name );
    }

    protected void setUp() throws Exception
    {
        m_baseDirectory = new File( "test" );
        m_baseDirectory.mkdirs();

        //Because log4j does not guarentee dir creation ;(
        final File logDir = new File( m_baseDirectory, "logs" );
        logDir.mkdirs();
    }

    private SarMetaData createSarMetaData( final String subdir )
    {
        final BlockMetaData[] blocks = new BlockMetaData[ 0 ];
        final BlockListenerMetaData[] listeners = new BlockListenerMetaData[ 0 ];
        final File homeDirectory = new File( m_baseDirectory, subdir );
        return new SarMetaData( "test",
                                homeDirectory,
                                blocks,
                                listeners );
    }

    private LogManager createLogManager()
    {
        final DefaultLogManager logManager = new DefaultLogManager();
        ContainerUtil.enableLogging( logManager, new ConsoleLogger() );
        return logManager;
    }

    public void testBasic()
        throws Exception
    {
        runtTestForConfigFile( 1 );
    }

    public void testExcaliburLogger()
        throws Exception
    {
        runtTestForConfigFile( 2 );
    }

    public void testLog4jVersion()
        throws Exception
    {
        runtTestForConfigFile( 3 );
    }

    private void runtTestForConfigFile( final int index ) throws Exception
    {
        final Logger hierarchy = createHierarchy( index );
        runLoggerTest( hierarchy, DEFAULT_LOGFILE, index );

        final Logger childLogger = hierarchy.getChildLogger( "myBlock" );
        runLoggerTest( childLogger, BLOCK_LOGFILE, index );
    }

    private void runLoggerTest( final Logger logger,
                                final String logfile,
                                final int index )
    {
        final long before = getFileSize( index, logfile );
        logger.warn( "Danger Will Robinson, Danger!" );
        final long after = getFileSize( index, logfile );

        assertFileGrew( logfile, before, after );
    }

    private void assertFileGrew( final String logfile, long before, long after )
    {
        assertTrue( "Did " + logfile + " file grow?, Before: " + before + ", After: " + after,
                    before < after );
    }

    private long getFileSize( final int index, final String filename )
    {
        final File base = getBaseDir( index );
        final File file = new File( base, filename );
        return file.length();
    }

    private File getBaseDir( final int index )
    {
        final String baseDir = getBaseDirName( index );
        return new File( m_baseDirectory, baseDir );
    }

    private Logger createHierarchy( final int index )
        throws Exception
    {
        final Configuration logs = loadConfig( "config" + index + ".xml" );
        final LogManager logManager = createLogManager();
        final SarMetaData sarMetaData = createSarMetaData( getBaseDirName( index ) );

        cleanHomeDirectory( sarMetaData );

        //make sure directory is created else log4j will fail.
        if( 3 == index )
        {
            final File file =
                new File( getBaseDir( index ).getAbsolutePath() + "/logs" );
            file.mkdirs();
        }

        final DefaultContext context = new DefaultContext();
        context.put( BlockContext.APP_NAME, sarMetaData.getName() );
        context.put( BlockContext.APP_HOME_DIR, sarMetaData.getHomeDirectory() );
        context.put( "classloader", getClass().getClassLoader() );

        return logManager.createHierarchy( logs, context );
    }

    private String getBaseDirName( final int index )
    {
        return "test" + index;
    }

    private void cleanHomeDirectory( final SarMetaData sarMetaData )
        throws IOException
    {
        final File homeDirectory = sarMetaData.getHomeDirectory();
        FileUtil.deleteDirectory( homeDirectory );
        homeDirectory.mkdirs();
    }

    private Configuration loadConfig( final String config )
        throws Exception
    {
        final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        final InputStream resource =
            getClass().getResourceAsStream( config );
        return builder.build( resource );
    }
}
