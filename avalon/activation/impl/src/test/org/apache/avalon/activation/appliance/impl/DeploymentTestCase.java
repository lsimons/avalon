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

package org.apache.avalon.activation.appliance.impl;
 
import org.apache.avalon.activation.appliance.Deployable;
import org.apache.avalon.activation.appliance.DeploymentException;
import org.apache.avalon.activation.appliance.FatalDeploymentException;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.apache.avalon.framework.logger.ConsoleLogger;

public class DeploymentTestCase extends TestCase
{
    private Deployer m_Deployer;
    
    public void setUp()
    {
        ConsoleLogger logger = new ConsoleLogger( ConsoleLogger.LEVEL_INFO );
        m_Deployer = new Deployer( logger );
    }

    public void tearDown()
        throws Exception
    {
        m_Deployer.dispose();
        m_Deployer = null;
    }
        
    public void testNormalDeploy()
        throws Exception
    {
        DummyDeployable d = new DummyDeployable();
        m_Deployer.deploy( d, 100 );
    }

    public void testInterruptableDeploy()
        throws Exception
    {
        try
        {
            InterruptableDeployable d = new InterruptableDeployable();
            m_Deployer.deploy( d, 100 );
            fail( "The Deployment didn't fail with a DeploymentException." );
        } catch( FatalDeploymentException e )
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
        try
        {
            m_Deployer.deploy( d, 100 );
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
            Deployable d = new CustomExceptionDeployable();
            m_Deployer.deploy( d, 100 );
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
            Deployable d = new CustomErrorDeployable();
            m_Deployer.deploy( d, 100 );
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
