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

import java.io.InputStream;
import java.io.IOException;
import java.io.Serializable;

import java.net.MalformedURLException;
import java.net.URL;

import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import java.util.Collection;

/**
 * Description of PKCS#7 Certificate file.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/02/23 13:00:31 $
 */
public final class PKCS7Directive implements Serializable
{
    private Certificate[] m_certificates;
    
    public PKCS7Directive( 
        final String href
    )
        throws IOException, CertificateException, MalformedURLException
    {
        InputStream in = null;
        try
        {
            URL url = new URL( href );
            in = url.openStream();

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Collection certs = cf.generateCertificates(in);
            m_certificates = new Certificate[ certs.size() ];
            certs.toArray( m_certificates );
        } finally
        {
            if( in != null )
                in.close();
        }
    }

    /**
     * Return the Certificates.
     *
     * @return the Certificate array
     */
    public Certificate[] getCertificates()
    {
        return m_certificates;
    }
}
 
