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

package org.apache.avalon.composition.model.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.Permission;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.net.URL;

import org.apache.avalon.composition.provider.SecurityModel;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;


/**
 * <p>Implementation of the default security model.</p>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/02/25 18:55:40 $
 */
public class DefaultSecurityModel implements SecurityModel
{
    //-------------------------------------------------------------------
    // static
    //-------------------------------------------------------------------

    private static final Permission[] EMPTY_PERMISSIONS = new Permission[0];
    private static final Certificate[] EMPTY_CERTIFICATES = new Certificate[0];

    public static SecurityModel createSecurityModel( Configuration config )
      throws Exception
    {
        Configuration certs = config.getChild( "certificates" );
        Certificate[] certificates = createCertificates( certs );
        Configuration grant = config.getChild( "grant" );
        Permission[] permissions = createPermissions( grant );
        return new DefaultSecurityModel( certificates, permissions );
    }

    //-------------------------------------------------------------------
    // immutable state
    //-------------------------------------------------------------------

    private final Certificate[] m_certificates;
    private final Permission[] m_permissions;
    private final boolean m_enabled;
 
    //-------------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------------

   /**
    * Creation of a new security model.
    */
    public DefaultSecurityModel()
    {
        this( null, null, false );
    }

   /**
    * Creation of a new security model.
    * 
    * @param certificates the set of trusted certificates
    * @param permissions the default permissions
    */
    public DefaultSecurityModel( 
      Certificate[] certificates, Permission[] permissions )
    {
        this( certificates, permissions, true );
    }

   /**
    * Creation of a new security model.
    * 
    * @param certificates the set of trusted certificates
    * @param permissions the default permissions
    * @param flag if TRUE code security is enabled
    */
    public DefaultSecurityModel( 
      Certificate[] certificates, Permission[] permissions, boolean flag )
    {
        m_enabled = flag;
        if( null == permissions )
        {
            m_permissions = EMPTY_PERMISSIONS;
        }
        else
        {
            m_permissions = permissions;
        }

        if( null == certificates )
        {
            m_certificates = EMPTY_CERTIFICATES;
        }
        else
        {
            m_certificates = certificates;
        }
    }

    //-------------------------------------------------------------------
    // SecurityModel
    //-------------------------------------------------------------------

   /**
    * Return the enabled status of the code security policy.
    * @return the code security enabled status
    */
    public boolean isCodeSecurityEnabled()
    {
        return m_enabled;
    }

   /**
    * Return the set of default permissions.
    * 
    * @return the permissions
    */
    public Permission[] getDefaultPermissions()
    {
        return m_permissions;
    }

   /**
    * Return the set of trusted certificates.
    * 
    * @return the trusted certificates
    */
    public Certificate[] getTrustedCertificates()
    {
        return m_certificates;
    }

    //-------------------------------------------------------------------
    // internals
    //-------------------------------------------------------------------

    private static Certificate[] createCertificates( Configuration config ) 
      throws Exception
    {
        ArrayList list = new ArrayList();
        Configuration[] children = config.getChildren();
        for( int i=0; i<children.length; i++ )
        {
            Configuration child = children[i];
            String name = child.getName();
            if( name.equals( "pkcs7" ) )
            {
                Certificate[] certs = 
                  DefaultSecurityModel.createPKCS7( child );
                for( int j=0; j<certs.length; j++ )
                {
                    list.add( certs[j] );
                }
            }
            else if( name.equals( "x509" ) )
            {
                Certificate[] certs = 
                  DefaultSecurityModel.createX509( child );
                for( int j=0; j<certs.length; j++ )
                {
                    list.add( certs[j] );
                }
            }
            else
            {
                final String error =
                  "Unrecognized certificate type [" + name + "].";
                throw new ConfigurationException( error );
            }
        }
        return (Certificate[]) list.toArray( new Certificate[0] );
    }

   /**
    * Static utility method to construct a PKCS7 certificate set from a 
    * supplied configuration.
    *
    * @param config a configuration describing the PKCS7 certificate
    */
    private static Certificate[] createPKCS7( Configuration config ) throws Exception
    {
        String href = config.getAttribute( "href" );
        InputStream in = null;
        try
        {
            URL url = new URL( href );
            in = url.openStream();

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Collection certs = cf.generateCertificates(in);
            Certificate[] certificates = new Certificate[ certs.size() ];
            return (Certificate[]) certs.toArray( certificates );
        } 
        finally
        {
            if( in != null ) in.close();
        }
    }

   /**
    * Static utility method to construct a X509 certificate set for a 
    * supplied configuration.
    *
    * @param config a configuration describing the PKCS7 certificate
    */
    private static Certificate[] createX509( Configuration config ) 
      throws ConfigurationException, CertificateException, IOException
    {
        String href = config.getAttribute( "href", "" );
        String data = config.getValue();

        InputStream in = null;
        try
        {
            if( href == null || "".equals( href ) )
            {
                in = new ByteArrayInputStream( data.getBytes("UTF-8") );
            }
            else
            {
                URL url = new URL( href );
                in = url.openStream();
            }
            CertificateFactory cf = CertificateFactory.getInstance( "X.509" );
            Collection certs = cf.generateCertificates( in );
            Certificate[] certificates = new Certificate[ certs.size() ];
            return (Certificate[]) certs.toArray( certificates );
        } 
        finally
        {
            if( in != null ) in.close();
        }
    }

    private static Permission[] createPermissions( Configuration config ) throws Exception
    {
        Configuration[] children = config.getChildren( "permission" );
        Permission[] permissions = new Permission[ children.length ];
        for( int i=0; i<children.length; i++ )
        {
            permissions[i] = createPermission( children[i] );
        }
        return permissions;
    }

    private static Permission createPermission( Configuration config ) throws Exception
    {
        String classname = config.getAttribute( "class" );
        String name = config.getAttribute( "name", null );
        String actions = getActions( config );
        return createPermission( classname, name, actions );
    }

    private static String getActions( Configuration config ) throws ConfigurationException
    {
        Configuration[] actions = config.getChildren( "action" );
        if( actions.length == 0 ) return null;
        String result = "";
        for( int i=0 ; i < actions.length ; i ++ )
        {
            if( i > 0 )
            {
                result = result + "," + actions[i].getValue();
            }
            else
            {
                result = result + actions[i].getValue();
            }
        }
        return result;
    }

    /**
     * Utility method to create a Permission instance.
     *
     * @param classname Permission class
     * @param name The name associated with the permission.
     * @param action The action associated with the permission. Note that some
     *        Permissions doesn't support actions.
     * @throws InstantiationException if the class could not be instantiated.
     * @throws IllegalAccessException, if the class does not have a 
     *         public constructor
     * @throws ClassNotFoundException, if the class could not be reached by the
     *         classloader.
     * @throws ClassCastException, if the class is not a subclass of 
     *         java.security.Permission
     * @throws InvocationTargetException, if the constructor in the Permission
     *         class throws an exception.
     */
    private static Permission createPermission( String classname, String name, String action )
        throws InstantiationException, IllegalAccessException, ClassNotFoundException,
          ClassCastException, InvocationTargetException
    {
        if( classname == null )
        {
            throw new NullPointerException( "classname" );
        }

        ClassLoader trustedClassloader = DefaultSecurityModel.class.getClassLoader();
        
        Class clazz = trustedClassloader.loadClass( classname );
        Constructor[] constructors = clazz.getConstructors();
        if( name == null )
        {
            return (Permission) clazz.newInstance();   
        }
        else if( action == null )
        {
            Constructor cons = getConstructor( constructors, 1 );
            Object[] arg = new Object[] { name };
            return (Permission) cons.newInstance( arg );
        }
        else
        {
            Constructor cons = getConstructor( constructors, 2 );
            Object[] args = new Object[] { name, action };
            return (Permission) cons.newInstance( args );
        }
    }

    private static Constructor getConstructor( Constructor[] constructors, int noOfParameters )
    {
        for ( int i=0 ; i < constructors.length ; i++ )
        {
            Class[] params = constructors[i].getParameterTypes();
            if( params.length == noOfParameters )
                return constructors[i];
        }
        return null;
    }

}
