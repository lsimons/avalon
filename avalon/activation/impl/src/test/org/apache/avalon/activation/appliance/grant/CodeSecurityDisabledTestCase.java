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

package org.apache.avalon.activation.appliance.grant;

import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.Block;
import org.apache.avalon.activation.appliance.impl.DefaultBlock;
import org.apache.avalon.activation.appliance.AbstractTestCase;
import org.apache.avalon.util.exception.ExceptionHelper;

import org.apache.avalon.activation.appliance.grant.components.TestService;

public class CodeSecurityDisabledTestCase extends AbstractTestCase
{
   //-------------------------------------------------------
   // constructor
   //-------------------------------------------------------

    public CodeSecurityDisabledTestCase( )
    {
        this( "secure" );
    }

    public CodeSecurityDisabledTestCase( String name )
    {
        super( name, false );
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
    }

   //-------------------------------------------------------
   // test
   //-------------------------------------------------------

   /**
    * Create, assembly, deploy and decommission the block 
    * defined by getPath().
    */
    public void testCodeSecurity() throws Exception
    {
        TestService test = setupTestService();

        try
        {
            test.doPrimary(); // test something in component
        }
        catch( Throwable e )
        {
            final String error = "CodeSecurityTest primary failure.";
            final String message = ExceptionHelper.packException( error, e, true );
            getLogger().error( message );
            throw new Exception( message );
        }

        try
        {
            test.doSecondary(); // test something in component
        }
        catch( Throwable e )
        {
            final String error = "CodeSecurityTest secondary failure.";
            final String message = ExceptionHelper.packException( error, e, true );
            getLogger().error( message );
            throw new Exception( message );
        }

    }

    private TestService setupTestService() throws Exception
    {
        m_model.assemble();
        Block block = new DefaultBlock( m_model );
        block.deploy();
        Appliance appliance = block.locate( "/test" );
        Object test = appliance.resolve();
        return (TestService) appliance.resolve();
    }

}
