package org.apache.avalon.cornerstone.blocks.masterstore.xml;

import org.apache.avalon.cornerstone.blocks.masterstore.AbstractFileRepository;

import java.io.InputStream;
import java.io.OutputStream;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;

/**
 * @author Paul Hammant
 * @version $Revision: 1.8 $
 */
public abstract class AbstractXMLFilePersistentObjectRepository extends AbstractFileRepository {

    /**
     * Get the object associated to the given unique key.
     */
    public synchronized Object get( final String key )
    {
        try
        {
            final InputStream inputStream = getInputStream( key );

            try
            {
                final XMLDecoder decoder = new XMLDecoder( inputStream );
                final Object object = decoder.readObject();
                if( DEBUG )
                {
                    monitor.returningKey(XMLFilePersistentObjectRepository.class, key);
                }
                return object;
            }
            finally
            {
                inputStream.close();
            }
        }
        catch( final Exception e )
        {
            throw new RuntimeException( "Exception caught while retrieving an object: " + e );
        }
    }

    public synchronized Object get( final String key, final ClassLoader classLoader )
    {
        try
        {
            final InputStream inputStream = getInputStream( key );
            final ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader( classLoader );
            try
            {
                final XMLDecoder decoder = new XMLDecoder( inputStream );
                final Object object = decoder.readObject();
                if( DEBUG )
                {
                    monitor.returningObjectForKey(XMLFilePersistentObjectRepository.class, object, key);
                }
                return object;
            }
            finally
            {
                Thread.currentThread().setContextClassLoader( oldCL );
                inputStream.close();
            }
        }
        catch( final Exception e )
        {
            e.printStackTrace();
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
                //System.out.println("Putting key!:" + key + " " + value + " " + value.getClass().getName());
                final XMLEncoder encoder = new XMLEncoder( outputStream );
                encoder.writeObject( value );
                encoder.flush();
                if( DEBUG ) monitor.storingObjectForKey(XMLFilePersistentObjectRepository.class, value, key);
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
