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

package org.apache.avalon.composition.model.test;

import java.security.Permission;


import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.DependencyModel;
import org.apache.avalon.composition.model.AbstractTestCase;
import org.apache.avalon.util.exception.ExceptionHelper;

public class SecurityTestCase extends AbstractTestCase
{      
   //-------------------------------------------------------
   // constructor
   //-------------------------------------------------------

    public SecurityTestCase()
    {
        super( "secure-block.xml" );
    }

   //-------------------------------------------------------
   // tests
   //-------------------------------------------------------

   /**
    * Validate the composition model.
    */
    public void testAssembly() throws Exception
    {
        m_model.assemble();
        Permission[] permissions = m_model.getClassLoaderModel().getSecurityPermissions();
        assertEquals( "Not all permissions were read.", 3, permissions.length );
        Permission[] p = new Permission[3];
        
        p[0] = new java.util.PropertyPermission( "org.apache", "read,  write" );
        p[1] = new java.lang.RuntimePermission( "getClassLoader" );
        p[2] = new java.security.AllPermission();
        for( int i=0 ; i < 3 ; i++ )
            assertEquals( "Permission is not the expected.", p[i], permissions[i] );
    }
    
    public void testSecureExecutionFlag()
        throws Exception
    {
        m_model.assemble();
        boolean secure = m_model.isSecureExecutionEnabled();
        assertEquals( "Secure Execution Flag not working.", true, secure );
    }
}
