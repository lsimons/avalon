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

package org.apache.avalon.composition.data;

import java.io.Serializable;

import java.security.cert.Certificate;

/**
 * Description of the Certificates description.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:08 $
 */
public final class CertsDirective implements Serializable
{
    private X509Directive[] m_X509Certs;
    private PKCS7Directive[] m_Pkcs7Certs;
    
    CertsDirective()
    {
        this( null, null );
    }
    
    public CertsDirective(
        final X509Directive[] x509certs,
        final PKCS7Directive[] pkcs7certs
    )
    {   
        if( x509certs == null )
            m_X509Certs = new X509Directive[0];
        else
            m_X509Certs = x509certs;
            
        if( pkcs7certs == null )
            m_Pkcs7Certs = new PKCS7Directive[0];
        else
            m_Pkcs7Certs = pkcs7certs;
    }
    
    public Certificate[] getCertificates()
    {
        int size = m_Pkcs7Certs.length + m_X509Certs.length;
        Certificate[] result = new Certificate[ size ];
        int counter = 0;
        for( int i=0 ; i < m_X509Certs.length ; i++ )
        {
            Certificate[] certs = m_X509Certs[i].getCertificates();
            for( int j=0 ; j < certs.length ; j++ )
                result[ counter++ ] = certs[j];
        }
        
        for( int i=0 ; i < m_Pkcs7Certs.length ; i++ )
        {
            Certificate[] certs = m_Pkcs7Certs[i].getCertificates();
            for( int j=0 ; j < certs.length ; j++ )
                result[ counter++ ] = certs[j];
        }
        return result;
    }
}
 
