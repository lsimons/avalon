/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket;

import org.apache.excalibur.nbio.AsyncSelection;

/**
 * This interface describes all the handler methods for 
 * an asynchronous socket.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface AsyncSocketHandlerBase
{
    /**
     * Process the accepted read request in the form of
     * a socket request.
     * (Reading Stage)
     * @since May 21, 2002
     * 
     * @param start
     *  the read request to process
     * @throws IOException
     *  to forward any IOException
     */
    void startRead(ReadRequest start);
    
    /**
     * Reads whatever possible using the attached state
     * of the socket. First tries to read any the clogged 
     * queue element from the state again.
     * (Reading Stage)
     * @since Aug 26, 2002
     * 
     * @param key
     *  The AsyncSelection key with the state attached
     */
    void read(AsyncSelection selectionKey);

    /**
     * Processes the passed in close request  on the 
     * specified socket.
     * (Writing Stage)
     * (Reading Stage)
     * @since Aug 27, 2002
     * 
     * @param close
     *  The close request to process.
     * @param sockState
     *  The socket state to be used.
     */
    void close(CloseRequest close);

    /**
     * Writes whatever possible using the attached state
     * of the socket.
     * (Writing Stage)
     * @since Aug 26, 2002
     * 
     * @param key
     *  The AsyncSelection key with the state attached
     */
    void write(AsyncSelection[] key);

    /**
     * Processes the passed in flush request on the 
     * specified socket.
     * (Writing Stage)
     * @since Aug 27, 2002
     * 
     * @param write
     *  The write request to process.
     * @param sockState
     *  The socket state to be used.
     */
    void flush(FlushRequest flush);
    
    /**
     * Processes the passed in write request on the 
     * specified socket.
     * (Writing Stage)
     * @since Aug 27, 2002
     * 
     * @param write
     *  The write request to process.
     * @param sockState
     *  The socket state to be used.
     */
    void startWrite(WriteRequest write);
}
