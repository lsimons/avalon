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

package org.apache.avalon.merlin.impl;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.MalformedURLException;
import java.util.StringTokenizer;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.activation.appliance.CascadingIOException;

/**
 * Repository URL protocol handler.  
 */
public class ArtifactURLConnection extends URLConnection
{
    /**
     * the url
     */
     private URL m_url;

    /**
     * Creation of a new handler. 
     * @param context the initial context
     * @exception NullPointerException if the supplied repository argument is null
     */
     ArtifactURLConnection( URL url ) 
       throws NullPointerException, IOException
     {
         super( url );

         String path = url.getPath();
         int i = path.lastIndexOf( "/" );
         if( i<0 )
         {
             final String error = 
               "Artifact specification does not contain a [group]/[name] seperator.";
             throw new MalformedURLException( error );
         }
         m_url = url;
     }

     public void connect()
     {
         // nothing to do
     }

    /**
     * Return the Artifact specified by this URL.
     * @return the artifact instance
     */
     public Object getContent() throws IOException
     {
         try
         {
             final String path = m_url.getPath();
             final int i = path.lastIndexOf( "/" );
             final String group = path.substring( 0, i );
             final String name = path.substring( i+1 );
             final String type = getType( url );
             final String version = getVersion( url );
             return Artifact.createArtifact( group, name, version, type );
         }
         catch( Throwable e )
         {
             final String error = 
               "Unexpected exception while resolving url [" + m_url + "].";
             throw new CascadingIOException( error );
         }
     }

    /**
     * Return the Artifact specified by this URL.
     * @param classes a set of classes (ignored)
     * @return the artifact instance
     */
     public Object getContent( Class[] classes ) throws IOException
     {
         return getContent();
     }

     private String getVersion( URL url )
     {
         return getQueryField( url, "version", null );
     } 

     private String getType( URL url )
     {
         return getQueryField( url, "type", "jar" );
     } 

     private String getQueryField( URL url, String field, String fallback )
     {
         String query = url.getQuery();
         if( null != query ) 
         {
             StringTokenizer tokenizer = new StringTokenizer( query, "&" );
             while( tokenizer.hasMoreElements() )
             {
                 String token = tokenizer.nextToken();
                 if( token.startsWith( field + "=" ) )
                 {
                     return token.substring( (field.length() + 1) );
                 }
             }
         }
         return fallback;
     }
}
