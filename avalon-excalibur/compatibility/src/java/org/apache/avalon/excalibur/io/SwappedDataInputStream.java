/* 
 * Copyright 2002-2004 The Apache Software Foundation
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
package org.apache.avalon.excalibur.io;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * DataInput for systems relying on little endian data formats.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.6 $ $Date: 2004/04/26 10:23:06 $
 * @since 4.0
 */
public class SwappedDataInputStream
        implements DataInput
{
    //The underlying input stream
    private InputStream m_input;

    public SwappedDataInputStream( final InputStream input )
    {
        m_input = input;
    }

    public boolean readBoolean()
            throws IOException, EOFException
    {
        return (0 == readByte());
    }

    public byte readByte()
            throws IOException, EOFException
    {
        return (byte)m_input.read();
    }

    public char readChar()
            throws IOException, EOFException
    {
        return (char)readShort();
    }

    public double readDouble()
            throws IOException, EOFException
    {
        return EndianUtil.readSwappedDouble( m_input );
    }

    public float readFloat()
            throws IOException, EOFException
    {
        return EndianUtil.readSwappedFloat( m_input );
    }

    public void readFully( final byte[] data )
            throws IOException, EOFException
    {
        readFully( data, 0, data.length );
    }

    public void readFully( final byte[] data, final int offset, final int length )
            throws IOException, EOFException
    {
        int remaining = length;

        while( remaining > 0 )
        {
            final int location = offset + (length - remaining);
            final int count = read( data, location, remaining );

            if( -1 == count )
            {
                throw new EOFException();
            }

            remaining -= count;
        }
    }

    public int readInt()
            throws IOException, EOFException
    {
        return EndianUtil.readSwappedInteger( m_input );
    }

    public String readLine()
            throws IOException, EOFException
    {
        throw new IOException( "Operation not supported" );
    }

    public long readLong()
            throws IOException, EOFException
    {
        return EndianUtil.readSwappedLong( m_input );
    }

    public short readShort()
            throws IOException, EOFException
    {
        return EndianUtil.readSwappedShort( m_input );
    }

    public int readUnsignedByte()
            throws IOException, EOFException
    {
        return m_input.read();
    }

    public int readUnsignedShort()
            throws IOException, EOFException
    {
        return EndianUtil.readSwappedUnsignedShort( m_input );
    }

    public String readUTF()
            throws IOException, EOFException
    {
        throw new IOException( "Operation not supported" );
    }

    public int skipBytes( final int count )
            throws IOException, EOFException
    {
        return (int)m_input.skip( count );
    }

    public int available()
            throws IOException, EOFException
    {
        return m_input.available();
    }

    public void close()
            throws IOException, EOFException
    {
        m_input.close();
    }

    public int read()
            throws IOException, EOFException
    {
        return m_input.read();
    }

    public int read( final byte[] data )
            throws IOException, EOFException
    {
        return read( data, 0, data.length );
    }

    public int read( final byte[] data, final int offset, final int length )
            throws IOException, EOFException
    {
        return m_input.read( data, offset, length );
    }

    public long skip( final long count )
            throws IOException, EOFException
    {
        return m_input.skip( count );
    }

    public void mark( final int readLimit )
    {
        m_input.mark( readLimit );
    }

    public boolean markSupported()
    {
        return m_input.markSupported();
    }

    public void reset()
            throws IOException
    {
        m_input.reset();
    }
}
