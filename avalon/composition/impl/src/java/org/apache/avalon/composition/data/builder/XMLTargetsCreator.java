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
 * @version $Revision: 1.6 $ $Date: 2004/02/25 22:54:09 $
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

        if( null != config.getChild( "grant", false ) )
        {
            //
            // this is a target for a container which can include
            // a single categories and a single grant statement
            //

            GrantDirective grants =
              createGrantDirective( config.getChild( "grant" ) );
            CategoriesDirective categories = 
              getCategoriesDirective( config.getChild( "categories", false ), name );
            return new TargetDirective( name, categories, grants );
        }
        else
        {
            //
            // this is potentially a component or containment target
            //

            final Configuration conf = config.getChild( "configuration", false );
              CategoriesDirective categories = 
               getCategoriesDirective( config.getChild( "categories", false ), name );
            return new TargetDirective( name, conf, categories );
        }
    }

    private GrantDirective createGrantDirective( Configuration config )
       throws ConfigurationException
    {
        ArrayList result = new ArrayList();
        Configuration[] permChildren = config.getChildren( "permission" );
        for( int i = 0; i < permChildren.length; i++ )
        {
            Configuration child = permChildren[i];
            PermissionDirective perm = createPermissionDirective( child );
            result.add( perm );
        }

        PermissionDirective[] pd = new PermissionDirective[ result.size() ];
        result.toArray( pd );
        
        Configuration certChild = config.getChild( "certificates" );
        CertsDirective certs = createCertsDirective( certChild );
        return new GrantDirective( pd, certs );
    }
    
    private CertsDirective createCertsDirective( Configuration conf )
       throws ConfigurationException
    {
        Configuration[] x509conf = conf.getChildren( "x509" );
        X509Directive[] x509 = new X509Directive[ x509conf.length ];
        for( int i=0 ; i < x509conf.length ; i++ )
        {
            String href = x509conf[i].getAttribute( "href", "" );
            String data = x509conf[i].getValue();
            try
            {
                x509[i] = new X509Directive( href, data );
            } catch( CertificateException e )
            {
                throw new ConfigurationException( "Invalid Certificate in " + x509conf[i], e );
            } catch( IOException e )
            {
                throw new ConfigurationException( "Can't access: " + href, e );
            }
        }
        
        Configuration[] pkcs7conf = conf.getChildren( "pkcs7" );
        PKCS7Directive[] pkcs7 = new PKCS7Directive[ pkcs7conf.length ];
        for( int i=0 ; i < pkcs7conf.length ; i++ )
        {
            String href = pkcs7conf[i].getAttribute( "href" );
            try
            {
                pkcs7[i] = new PKCS7Directive( href );
            } catch( CertificateException e )
            {
                throw new ConfigurationException( "Invalid Certificate in " + pkcs7conf[i], e );
            } catch( IOException e )
            {
                throw new ConfigurationException( "Can't access: " + href, e );
            }
        }
        return new CertsDirective( x509, pkcs7 );
    }
    
    private PermissionDirective createPermissionDirective( Configuration config )
       throws ConfigurationException
    {
        String classname = config.getAttribute( "class" );
        String name = config.getAttribute( "name", null );
        String result = "";
        Configuration[] actions = config.getChildren( "action" );
        for( int i=0 ; i < actions.length ; i ++ )
        {
            if( i > 0 )
                result = result + "," + actions[i].getValue();
            else
                result = result + actions[i].getValue();
        }
        try
        {
            return new PermissionDirective( classname, name, result );
        } catch( Exception e )
        {
            throw new ConfigurationException( "Unable to create the Permission Directive.", e );
        }
    }

}
