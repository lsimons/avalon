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

import org.apache.avalon.composition.model.DeploymentModel;

import org.apache.avalon.meta.info.ReferenceDescriptor;

import org.apache.avalon.util.exception.ExceptionHelper;

import org.apache.avalon.composition.model.test.dynamics.Widget;
import org.apache.avalon.composition.model.test.dynamics.Gizmo;

public class DynamicsTestCase extends AbstractTestCase
{      
   //-------------------------------------------------------
   // constructor
   //-------------------------------------------------------

    public DynamicsTestCase()
    {
        super();
    }

    public void setUp() throws Exception
    {
        m_model = super.setUp( "dynamics.xml" );
    }

   //-------------------------------------------------------
   // tests
   //-------------------------------------------------------

   /**
    * Validate resolution of a widget.
    */
    public void testDynamicWidget() throws Exception
    {
        try
        {
            m_model.assemble();
            String spec = Widget.class.getName();
            ReferenceDescriptor ref = new ReferenceDescriptor( spec );
            DeploymentModel widget = 
            m_model.getModel( ref );
        }
        catch( Throwable e )
        {
            final String message = "Assembly test failure";
            final String error = ExceptionHelper.packException( message, e, true );
            System.err.println( error );
            fail( error );
        }
    }

   /**
    * Validate resolution of a gizmo.
    */
    public void testDynamicGizmo() throws Exception
    {
        try
        {
            m_model.assemble();
            String spec = Gizmo.class.getName();
            ReferenceDescriptor ref = new ReferenceDescriptor( spec );
            DeploymentModel widget = 
            m_model.getModel( ref );
        }
        catch( Throwable e )
        {
            final String message = "Assembly test failure";
            final String error = ExceptionHelper.packException( message, e, true );
            System.err.println( error );
            fail( error );
        }
    }
}
