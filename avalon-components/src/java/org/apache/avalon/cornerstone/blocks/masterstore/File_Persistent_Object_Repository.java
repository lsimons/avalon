/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
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
 
 4. The names "Jakarta", "Apache Avalon", "Avalon Cornerstone", "Avalon
    Framework" and "Apache Software Foundation"  must not be used to endorse
    or promote products derived  from this  software without  prior written
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

package org.apache.avalon.cornerstone.blocks.masterstore;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import org.apache.avalon.cornerstone.services.store.ObjectRepository;
import org.apache.avalon.excalibur.io.ClassLoaderObjectInputStream;

/**
 * This is a simple implementation of persistent object store using
 * object serialization on the file system.
 *
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:paul_hammant@yahoo.com">Paul Hammant</a>
 */
public class File_Persistent_Object_Repository
    extends AbstractFileRepository
    implements ObjectRepository
{
    protected String getExtensionDecorator()
    {
        return ".FileObjectStore";
    }

    /**
     * Get the object associated to the given unique key.
     */
    public synchronized Object get( final String key )
    {
        try
        {
            final InputStream inputStream = getInputStream( key );

            if( inputStream == null )
                  throw new NullPointerException("Null input stream returned for key: " + key );
            try
            {
                final ObjectInputStream stream = new ObjectInputStream( inputStream );

                if( stream == null )
                  throw new NullPointerException("Null stream returned for key: " + key );

                final Object object = stream.readObject();
                if( DEBUG )
                {
                    getLogger().debug( "returning object " + object + " for key " + key );
                }
                return object;
            }
            finally
            {
                inputStream.close();
            }
        }
        catch( final Throwable e )
        {
            throw new RuntimeException( 
              "Exception caught while retrieving an object, cause: " + e.toString() );
        }
    }

    public synchronized Object get( final String key, final ClassLoader classLoader )
    {
        try
        {
            final InputStream inputStream = getInputStream( key );

            if( inputStream == null )
                  throw new NullPointerException("Null input stream returned for key: " + key );

            try
            {
                final ObjectInputStream stream = new ClassLoaderObjectInputStream( classLoader, inputStream );

                if( stream == null )
                  throw new NullPointerException("Null stream returned for key: " + key );

                final Object object = stream.readObject();

                if( DEBUG )
                {
                    getLogger().debug( "returning object " + object + " for key " + key );
                }
                return object;
            }
            finally
            {
                inputStream.close();
            }
        }
        catch( final Throwable e )
        {
            throw new RuntimeException( "Exception caught while retrieving an object: " + e );
        }

    }

    /**
     * Store the given object and associates it to the given key
     */
    public synchronized void put( final String key, final Object value )
    {
        try
        {
            final OutputStream outputStream = getOutputStream( key );

            try
            {
                final ObjectOutputStream stream = new ObjectOutputStream( outputStream );
                stream.writeObject( value );
                if( DEBUG ) getLogger().debug( "storing object " + value + " for key " + key );
            }
            finally
            {
                outputStream.close();
            }
        }
        catch( final Exception e )
        {
            throw new RuntimeException( "Exception caught while storing an object: " + e );
        }
    }

}
