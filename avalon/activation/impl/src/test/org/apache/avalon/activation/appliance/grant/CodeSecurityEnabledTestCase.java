/* 
 * Copyright 2004 Apache Software Foundation
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

package org.apache.avalon.activation.appliance.grant;

import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.Block;
import org.apache.avalon.activation.appliance.impl.DefaultBlock;
import org.apache.avalon.activation.appliance.AbstractTestCase;
import org.apache.avalon.util.exception.ExceptionHelper;

import org.apache.avalon.framework.activity.Disposable;

import org.apache.avalon.activation.appliance.grant.components.TestService;

public class CodeSecurityEnabledTestCase extends AbstractTestCase
{
   //-------------------------------------------------------
   // state
   //-------------------------------------------------------

   private Block m_block;
   private Appliance m_appliance;

   //-------------------------------------------------------
   // constructor
   //-------------------------------------------------------

    public CodeSecurityEnabledTestCase( )
    {
        this( "secure" );
    }

    public CodeSecurityEnabledTestCase( String name )
    {
        super( name, true );
    }

   //-------------------------------------------------------
   // setup
   //-------------------------------------------------------

   /**
    * Setup the model using a source balock in the conf 
    * directory.
    * @exception Exception if things don't work out
    */
    public void setUp() throws Exception
    {
        super.setUp( "secure.xml" );
        m_model.assemble();
        m_block = new DefaultBlock( m_model );
        m_block.commission();
        m_appliance = m_block.locate( "/Component1/test" );
    }

    public void tearDown()
    {
        m_appliance = null;
        try
        {
            m_block.decommission();
            ((Disposable)m_block).dispose();
        }
        catch( Throwable e )
        {
            // ignore
        }
        m_block = null;
    }

    private TestService getTestService() throws Exception
    {
        return (TestService) m_appliance.resolve();
    }

    private void releaseTestService( TestService service )
    {
        m_appliance.release( service );
    }

   //-------------------------------------------------------
   // test
   //-------------------------------------------------------

   /**
    * Create, assembly, deploy and decommission the block 
    * defined by getPath().
    */
    public void testInterfaceMethods() throws Exception
    {
        TestService test = getTestService();
        try
        {
            test.createDirectory(); 
            
// TODO::::            
//            fail( "CodeSecurityTest primary failure: This operation should not be allowed." );
        }
        catch( SecurityException e )
        {
            // ignore, expected
        }
        catch( Throwable e )
        {
            releaseTestService( test );
            final String error = "CodeSecurityTest primary failure.";
            final String message = ExceptionHelper.packException( error, e, true );
            getLogger().error( message );
            throw new Exception( message );
        }

        try
        {
            test.deleteDirectory(); 
// TODO::::            
//            fail( "CodeSecurityTest secondary failure: This operation should not be allowed." );
        }
        catch( SecurityException e )
        {
            // ignore, expected
        }
        catch( Throwable e )
        {
            releaseTestService( test );
            final String error = "CodeSecurityTest primary failure.";
            final String message = ExceptionHelper.packException( error, e, true );
            getLogger().error( message );
            throw new Exception( message );
        }
        
        try
        {
            // This should succeed since there is a read permission for
            // system properties in the security policy.
            String ver = test.getJavaVersion();
        }
        catch( Throwable e )
        {
            releaseTestService( test );
            final String error = "CodeSecurityTest secondary failure.";
            final String message = ExceptionHelper.packException( error, e, true );
            getLogger().error( message );
            throw new Exception( message );
        }
    
        try
        {
            test.setJavaVersion( "1.0.2" ); 
// TODO::::            
//            fail( "CodeSecurityTest failure: This operation should not be allowed." );
        }
        catch( SecurityException e )
        {
            // ignore, expected
        }
        catch( Throwable e )
        {
            releaseTestService( test );
            final String error = "CodeSecurityTest primary failure.";
            final String message = ExceptionHelper.packException( error, e, true );
            getLogger().error( message );
            throw new Exception( message );
        }

        releaseTestService( test );

    }
}
