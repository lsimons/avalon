/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.tcp;

import java.io.IOException;

import org.apache.excalibur.nbio.AsyncSelection;

/**
 * This interface describes all the handler methods for 
 * an asynchronous server socket.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface AsyncServerSocketHandler
{
    String ROLE = AsyncServerSocketHandler.class.getName();
    
    /**
     * Processes the passed in listen request event. 
     * (Listen Stage)
     * @since May 20, 2002
     * 
     * @param listenRequest
     *  The listen request to process
     */
    public void listen(ListenRequest listenRequest);

    /**
     * Processes the passed in suspend request event.
     * (Listen Stage)
     * @since May 20, 2002
     * 
     * @param suspendRequest
     *  The suspend request to process
     */
    void suspend(SuspendAcceptRequest suspendRequest);

    /**
     * Processes the passed in resume request event.
     * (Listen Stage)
     * @since May 20, 2002
     * 
     * @param resumeRequest
     *  The resume request to process
     */
    void resume(ResumeAcceptRequest resumeRequest);

    /**
     * Processes the passed in socket close request.
     * (Listen Stage)
     * @since May 20, 2002
     * 
     * @param closeRequest
     *  The socket close request to process
     */
    void close(ServerSocketCloseRequest closeRequest);

    /**
     * Processes the passed in selection key since the channel
     * is ready for accept operations. Throws an {@link IOException} 
     * if an error occurs during the accept operation.
     * (Listen Stage)
     * @since May 20, 2002
     * 
     * @param selectionKey
     *  The {@link SelectionKey} to process
     * @throws IOException 
     *  if an error occurs during the accept operation.
     */
    void accept(AsyncSelection selectionKey) throws IOException;
    
}
