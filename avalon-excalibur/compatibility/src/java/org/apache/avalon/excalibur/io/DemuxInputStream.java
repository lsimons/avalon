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
import java.io.InputStream;

/**
 * Data written to this stream is forwarded to a stream that has been associated
 * with this thread.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.6 $ $Date: 2004/04/26 10:23:06 $
 */
public final class DemuxInputStream
        extends InputStream
{
    private final InheritableThreadLocal m_streams = new InheritableThreadLocal();

    /**
     * Bind the specified stream to the current thread.
     *
     * @param input the stream to bind
     */
    public InputStream bindStream( final InputStream input )
    {
        final InputStream oldValue = getStream();
        m_streams.set( input );
        return oldValue;
    }

    /**
     * Closes stream associated with current thread.
     *
     * @throws IOException if an error occurs
     */
    public void close()
            throws IOException
    {
        final InputStream input = getStream();
        if( null != input )
        {
            input.close();
        }
    }

    /**
     * Read byte from stream associated with current thread.
     *
     * @return the byte read from stream
     * @throws IOException if an error occurs
     */
    public int read()
            throws IOException
    {
        final InputStream input = getStream();
        if( null != input )
        {
            return input.read();
        }
        else
        {
            return -1;
        }
    }

    /**
     * Utility method to retrieve stream bound to current thread (if any).
     */
    private InputStream getStream()
    {
        return (InputStream)m_streams.get();
    }
}
