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

import org.apache.avalon.activation.appliance.impl.AbstractBlock;
import org.apache.avalon.composition.util.ExceptionHelper;
import org.apache.avalon.framework.activity.Disposable;

public class RuntimeTestCase extends AbstractTestCase
{
   //-------------------------------------------------------
   // constructor
   //-------------------------------------------------------

    public RuntimeTestCase( )
    {
        this( "model" );
    }

    public RuntimeTestCase( String name )
    {
        super( name );
    }

   //-------------------------------------------------------
   // setup
   //-------------------------------------------------------

   public String getPath()
   {
      return "block.xml";
   }

   //-------------------------------------------------------
   // tests
   //-------------------------------------------------------

   /**
    * Validate the composition model.
    */
    public void testDeploymentCycle() throws Exception
    {
        try
        {
            executeDeploymentCycle();
        }
        catch( Throwable e )
        {
            final String error = "Test failure.";
            final String message = ExceptionHelper.packException( error, e, true );
            getLogger().error( message );
            throw new Exception( message );
        }
    }

   /**
    * Validate the composition model.
    */
    public void executeDeploymentCycle() throws Exception
    {

        //
        // 1. create the root block using the service context
        //    and the root containment model
        //

        getLogger().debug( "creating root block" );
        Block block = AbstractBlock.createRootBlock( m_context, m_model );
        getLogger().debug( "block: " + block );

        //
        // 2. assemble the block during which all dependencies
        //    are resolved (deployment and runtime)
        //

        if( block instanceof Composite )
        {
            ((Composite)block).assemble();
        }

        //
        // 3. deploy the block during which any 'activate on startup'
        //    components are created which in turn my cause activation
        //    of lazy components
        //
        
        block.deploy();

        //
        // 4-5. suspend and resume the root block (not implemented yet)
        //
        // 6. decommission the block during which all managed appliances
        //    are decommissioned resulting in the decommissioning of all
        //    appliance instances
        //

        block.decommission();

        //
        // 7. disassemble the block during which reference between 
        //    appliances established at assembly time are discarded
        //

        if( block instanceof Composite )
        {
            ((Composite)block).disassemble();
        }

        //
        // 8. dispose of the appliance during which all subsidiary 
        //    appliances are disposed of in an orderly fashion
        //

        if( block instanceof Disposable )
        {
            ((Disposable)block).dispose();
        }

        assertTrue( true );
    }

}
