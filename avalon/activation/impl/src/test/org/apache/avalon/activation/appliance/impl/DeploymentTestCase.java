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

package org.apache.avalon.activation.appliance.impl;
 
import java.lang.reflect.InvocationTargetException;

import org.apache.avalon.activation.appliance.DeploymentException;
import org.apache.avalon.activation.appliance.FatalDeploymentException;

import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.runtime.Commissionable;

import junit.framework.TestCase;

import org.apache.avalon.framework.logger.ConsoleLogger;

public class DeploymentTestCase extends TestCase
{
    private Deployer m_deployer;
    
    public void setUp()
    {
        ConsoleLogger logger = new ConsoleLogger( ConsoleLogger.LEVEL_INFO );
        m_deployer = new Deployer( logger );
    }

    public void tearDown()
        throws Exception
    {
        m_deployer.dispose();
        m_deployer = null;
    }
        
    public void testNormalDeploy()
        throws Exception
    {
        Commissionable d = new DummyDeployable();
        DeploymentModel model = new DummyDeploymentModel( d, 100 );
        m_deployer.deploy( model );
    }

    public void testInterruptableDeploy()
        throws Exception
    {
        try
        {
            InterruptableDeployable d = new InterruptableDeployable();
            DeploymentModel model = new DummyDeploymentModel( d, 100 );
            m_deployer.deploy( model );

            fail( "The Deployment didn't fail with a DeploymentException." );
        } 
        catch( FatalDeploymentException e )
        {
            fail( "The Exception thrown was a FatalDeploymentException." );
        } catch( DeploymentException e )
        {
            // testcase success
        }
    }
    
    public void testUninterruptableDeploy()
        throws Exception
    {
        UninterruptableDeployable d = new UninterruptableDeployable();
        DeploymentModel model = new DummyDeploymentModel( d, 100 );
        try
        {
            m_deployer.deploy( model );
            fail( "The Deployment didn't fail with a FatalDeploymentException." );
        } catch( FatalDeploymentException e )
        {
            // testcase success
        } catch( DeploymentException e )
        {
            fail( "The Exception thrown was a DeploymentException, when a FatalDeploymentException was expected." );
        } finally
        {
            d.stop();
        }
    }
    
    public void testCustomExceptionDeploy()
        throws Exception
    {
        try
        {
            Commissionable d = new CustomExceptionDeployable();
            DeploymentModel model = new DummyDeploymentModel( d, 100 );
            m_deployer.deploy( model );
            fail( "The Deployment didn't fail with a DeploymentException." );
        } catch( CustomException e )
        {
            // success
        } catch( FatalDeploymentException e )
        {
            fail( "The Exception thrown was a FatalDeploymentException." );
        } catch( DeploymentException e )
        {
            fail( "The Exception thrown was a DeploymentException." );
        }
    }
    
    public void testCustomErrorDeploy()
        throws Exception
    {
        try
        {
            Commissionable d = new CustomErrorDeployable();
            DeploymentModel model = new DummyDeploymentModel( d, 100 );
            m_deployer.deploy( model );
            fail( "The Deployment didn't fail with a DeploymentException." );
        } catch( CustomError e )
        {
            // success
        } catch( FatalDeploymentException e )
        {
            fail( "The Exception thrown was a FatalDeploymentException." );
        } catch( DeploymentException e )
        {
            fail( "The Exception thrown was a DeploymentException." );
        }
    }
}
