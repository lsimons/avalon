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

package org.apache.avalon.composition.data.builder;

import java.io.IOException;
import java.util.ArrayList;
import java.security.cert.CertificateException;

import org.apache.avalon.logging.data.CategoriesDirective;

import org.apache.avalon.composition.data.GrantDirective;
import org.apache.avalon.composition.data.CertsDirective;
import org.apache.avalon.composition.data.PermissionDirective;
import org.apache.avalon.composition.data.PKCS7Directive;
import org.apache.avalon.composition.data.TargetDirective;
import org.apache.avalon.composition.data.Targets;
import org.apache.avalon.composition.data.X509Directive;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * Handles internalization of an XML based description of a {@link Targets}
 * instance from a Configuration object.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.7 $ $Date: 2004/02/29 22:25:26 $
 */
public class XMLTargetsCreator extends XMLComponentProfileCreator 
{
   /**
    * Create a set of target directives from the confiugration.
    * @param config the targets configuration
    */
    public Targets createTargets( Configuration config )
      throws Exception
    {
        Configuration[] children = config.getChildren( "target" );
        TargetDirective[] targets = new TargetDirective[ children.length ];
        for( int i=0; i<children.length; i++ )
        {
            targets[i] = createTargetDirective( children[i] );
        }
        return new Targets( targets );
    }

    /**
     * Create a {@link TargetDirective} from a configuration
     *
     * @param config the configuration
     * @return the target directive
     * @exception Exception if a error occurs during profile creation
     */
    private TargetDirective createTargetDirective( Configuration config )
      throws Exception
    {
        //
        // get the address of the object we are overriding
        //

        String name = config.getAttribute( "name", null ); // legacy
        if( name == null )
        {
            name = config.getAttribute( "path" );
        }

        //
        // get the applicable secuirity profile
        //

        Configuration security = config.getChild( "security" );
        String profile = security.getAttribute( "profile", null );

        //
        // get the assigned categories
        //

        CategoriesDirective categories = 
          getCategoriesDirective( 
            config.getChild( "categories", false ), name );

        //
        // get the overriding configuration
        //

        final Configuration conf = config.getChild( "configuration", false );

        //
        // and create the target directive
        //

        return new TargetDirective( name, conf, categories, profile );
    }
}
