/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

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

package org.apache.avalon.activation.appliance;

import java.io.File;

import junit.framework.TestCase;

import org.apache.avalon.activation.appliance.impl.DefaultServiceContext;
import org.apache.avalon.composition.data.ContainmentProfile;
import org.apache.avalon.composition.data.builder.XMLContainmentProfileCreator;
import org.apache.avalon.composition.logging.LoggingManager;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.composition.model.impl.DefaultSystemContext;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.excalibur.event.command.CommandManager;
import org.apache.excalibur.event.command.TPCThreadManager;
import org.apache.excalibur.mpool.DefaultPoolManager;
import org.apache.excalibur.mpool.PoolManager;


public abstract class AbstractTestCase extends TestCase
{
   //-------------------------------------------------------
   // state
   //-------------------------------------------------------

    protected Logger m_logger;

    protected ServiceContext m_context;

    protected ContainmentModel m_model;

    protected SystemContext m_system;

   //-------------------------------------------------------
   // constructor
   //-------------------------------------------------------

    public AbstractTestCase( )
    {
        this( "data" );
    }

    public AbstractTestCase( String name )
    {
        super( name );
    }

   //-------------------------------------------------------
   // setup
   //-------------------------------------------------------

    public abstract String getPath();

   /**
    * The setup process covers the establishment of the base
    * directory (from which relative references for extension directories
    * and fileset base directories are resolved), a file repository (not
    * used in this test case at this time), and a class loader model from 
    * which a classpath will be established.
    *
    * @exception Exception if things don't work out
    */
    public void setUp() throws Exception
    {
        File base = new File( getTestDir(), "test-classes" );

        //
        // WARNING: ALMOST EVIL
        // Next couple of lines are using a convinience operation
        // on DefaultSystemContext to create the system context.  This 
        // is temporary and will be replaced when a clean configurable 
        // system context factory is in place
        //

        File local = new File( base, "repository" );
        SystemContext system = 
              DefaultSystemContext.createSystemContext( 
                base, local, ConsoleLogger.LEVEL_INFO );
        m_logger = system.getLogger();

        //
        // load the meta data using the profile returned from getPath()
        // and establish a containment model for the unit test
        //

        ContainmentProfile profile = setUpProfile( new File( base, getPath() ) );
        m_model = system.getFactory().createContainmentModel( profile );

        // 
        // create the service context now even thought
        // its not needed until we start playing with appliances
        // and blocks
        //

        PoolManager pool = createPoolManager();
        DefaultServiceContext context = new DefaultServiceContext();
        context.put( PoolManager.ROLE, pool );
        context.put( LoggingManager.KEY, system.getLoggingManager() );
        m_context = context;

    }

    protected ContainmentProfile setUpProfile( File file )
      throws Exception
    {
        XMLContainmentProfileCreator creator = new XMLContainmentProfileCreator();
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration config = builder.buildFromFile( file );
        return creator.createContainmentProfile( config );
    }

    protected static File getTestDir()
    {
        return new File( System.getProperty( "basedir" ), "target" );
    }

    private PoolManager createPoolManager() throws Exception
    {
        try
        {
            //
            // Set up the ThreadManager that the CommandManager uses
            //

            TPCThreadManager threadManager = new TPCThreadManager();
            threadManager.enableLogging( getLogger().getChildLogger( "threads" ) );
            Parameters params = new Parameters();
            params.setParameter( "threads-per-processor", "2" );
            params.setParameter( "sleep-time", "1000" );
            params.setParameter( "block-timeout", "250" );
            threadManager.parameterize( params );
            threadManager.initialize();

            //
            // Set up the CommandManager that the PoolManager uses.
            //

            CommandManager commandManager = new CommandManager();
            threadManager.register( commandManager );

            //
            // Set up the PoolManager that the pooled lifecycle helper needs
            //

            DefaultPoolManager poolManager =
                    new DefaultPoolManager( commandManager.getCommandSink() );
            return poolManager;
        } 
        catch( Throwable e )
        {
            final String error =
                    "Internal error during establishment of the default pool manager. Cause: ";
            throw new Exception( error + e.toString() );
        }
    }

    protected Logger getLogger()
    {
        return m_logger;
    }

}
