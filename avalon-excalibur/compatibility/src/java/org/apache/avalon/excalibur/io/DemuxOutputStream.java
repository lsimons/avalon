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

import java.io.IOException;
import java.io.OutputStream;

/**
 * Data written to this stream is forwarded to a stream that has been associated
 * with this thread.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.6 $ $Date: 2004/04/26 10:23:06 $
 */
public final class DemuxOutputStream
        extends OutputStream
{
    private final InheritableThreadLocal m_streams = new InheritableThreadLocal();

    /**
     * Bind the specified stream to the current thread.
     *
     * @param output the stream to bind
     */
    public OutputStream bindStream( final OutputStream output )
    {
        final OutputStream stream = getStream();
        m_streams.set( output );
        return stream;
    }

    /**
     * Closes stream associated with current thread.
     *
     * @throws IOException if an error occurs
     */
    public void close()
            throws IOException
    {
        final OutputStream output = getStream();
        if( null != output )
        {
            output.close();
        }
    }

    /**
     * Flushes stream associated with current thread.
     *
     * @throws IOException if an error occurs
     */
    public void flush()
            throws IOException
    {
        final OutputStream output = getStream();
        if( null != output )
        {
            output.flush();
        }
    }

    /**
     * Writes byte to stream associated with current thread.
     *
     * @param ch the byte to write to stream
     * @throws IOException if an error occurs
     */
    public void write( final int ch )
            throws IOException
    {
        final OutputStream output = getStream();
        if( null != output )
        {
            output.write( ch );
        }
    }

    /**
     * Utility method to retrieve stream bound to current thread (if any).
     */
    private OutputStream getStream()
    {
        return (OutputStream)m_streams.get();
    }
}
