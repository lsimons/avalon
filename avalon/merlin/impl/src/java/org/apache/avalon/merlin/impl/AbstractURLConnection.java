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
 * Abstract artifact URL protocol handler.  
 * @since 3.3
 */
public abstract class AbstractURLConnection extends URLConnection
{
    /**
     * Creation of a new handler. 
     * @param url the url to establish a connection with
     */
     AbstractURLConnection( URL url ) 
       throws IOException
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
     }

    /**
     * Return the Artifact specified by this URL.
     * @param classes a set of classes (ignored)
     * @return the artifact instance
     * @see org.apache.avalon.repository.Artifact
     */
     public Object getContent( Class[] classes ) throws IOException
     {
         return getContent();
     }

    /**
     * Null implementation of the conect protocol.
     */
     public void connect()
     {
         // nothing to do
     }

    /**
     * Return the Artifact specified by this URL.
     * @param type the artifact type (e.g. "jar", "block", "xml", etc.)
     * @return the artifact instance
     * @see org.apache.avalon.repository.Artifact
     */
     protected Object getContent( String type ) throws IOException
     {
         try
         {
             final String path = getURL().getPath();
             final int i = path.lastIndexOf( "/" );
             final String group = path.substring( 0, i );
             final String name = path.substring( i+1 );
             final String version = getVersion( url );
             return Artifact.createArtifact( group, name, version, type );
         }
         catch( Throwable e )
         {
             final String error = 
               "Unexpected exception while resolving url [" + super.getURL() + "].";
             throw new CascadingIOException( error );
         }
     }

    /**
     * Utility method to return the version field with the url query.
     * @param url the url containing the query
     * @return the version value if declared else null
     */
     protected String getVersion( URL url )
     {
         return getQueryField( url, "version", null );
     } 

     /**
     * Utility method to return the value of a field within the url query.
     * @param url the url containing the query
     * @param field the query field name
     * @param fallback the default value if not query parameter available
     * @return the value of the query field
     */
     protected String getQueryField( URL url, String field, String fallback )
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
