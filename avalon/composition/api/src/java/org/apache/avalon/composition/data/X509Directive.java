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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.Serializable;

import java.net.URL;
import java.net.MalformedURLException;

import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import java.util.Collection;

/**
 * Description of X.509 Certificate directive.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/02/23 13:00:31 $
 */
public final class X509Directive implements Serializable
{
    private Certificate[] m_certificates;
    
    public X509Directive( 
        String href,
        String stream
    )
        throws CertificateException, IOException, MalformedURLException
    {
        InputStream in = null;
        try
        {
            if( href == null || "".equals( href ) )
            {
                in = new ByteArrayInputStream( stream.getBytes("UTF-8") );
            }
            else
            {
                URL url = new URL( href );
                in = url.openStream();
            }

            CertificateFactory cf = CertificateFactory.getInstance( "X.509" );
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
