/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2002 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.log.output.net;

import java.util.Date;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.log.format.Formatter;
import org.apache.log.output.AbstractOutputTarget;

/**
 * Logkit output target that logs data via SMTP (ie. email, email gateways).
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Id: SMTPOutputLogTarget.java,v 1.3 2003/02/03 17:40:16 bloritsch Exp $
 * @since 1.1.0
 */
public class SMTPOutputLogTarget extends AbstractOutputTarget
{
    // Mail session
    private final Session m_session;

    // Message to be sent
    private Message m_message;

    // Address to sent mail to
    private final Address[] m_toAddresses;

    // Address to mail is to be listed as sent from
    private final Address m_fromAddress;

    // Mail subject
    private final String m_subject;

    // Current size of mail, in units of log events
    private int m_msgSize;

    // Maximum size of mail, in units of log events
    private final int m_maxMsgSize;

    // Buffer containing current mail
    private StringBuffer m_buffer;

    /**
     * SMTPOutputLogTarget constructor, creates a logkit output target
     * capable of logging to SMTP (ie. email, email gateway) targets.
     *
     * @param session mail session to be used
     * @param toAddresses addresses logs should be sent to
     * @param fromAddress address logs say they come from
     * @param subject subject line logs should use
     * @param maxMsgSize maximum size of any log mail, in units of log events
     * @param formatter log formatter to use
     */
    public SMTPOutputLogTarget(
        final Session session,
        final Address[] toAddresses,
        final Address fromAddress,
        final String subject,
        final int maxMsgSize,
        final Formatter formatter
    )
    {
        super( formatter );

        // setup log target
        m_maxMsgSize = maxMsgSize;
        m_toAddresses = toAddresses;
        m_fromAddress = fromAddress;
        m_subject = subject;
        m_session = session;

        // ready for business
        open();
    }

    /**
     * Method to write data to the log target. Logging data is stored in
     * an internal buffer until the size limit is reached. When this happens
     * the data is sent to the SMTP target, and the buffer is reset for
     * subsequent events.
     *
     * @param data logging data to be written to target
     */
    protected void write( final String data )
    {
        try
        {
            // ensure we have a message object available
            if ( m_message == null )
            {
                m_message = new MimeMessage( m_session );
                m_message.setFrom( m_fromAddress );
                m_message.setRecipients( Message.RecipientType.TO, m_toAddresses );
                m_message.setSubject( m_subject );
                m_message.setSentDate( new Date() );
                m_msgSize = 0;
                m_buffer = new StringBuffer();
            }

            // add the data to the buffer, separated by a newline
            m_buffer.append( data );
            m_buffer.append( '\n' );
            ++m_msgSize;

            // send mail if message size has reached it's size limit
            if ( m_msgSize >= m_maxMsgSize )
                send();
        }
        catch ( MessagingException e )
        {
            getErrorHandler().error( "Error creating message", e, null );
        }
    }

    /**
     * Closes this log target. Sends currently buffered message, if existing.
     */
    public synchronized void close()
    {
        super.close();
        send();
    }

    /**
     * Method to enable/disable debugging on the mail session.
     *
     * @param flag true to enable debugging, false to disable it
     */
    public void setDebug( boolean flag )
    {
        m_session.setDebug( flag );
    }

    /**
     * Helper method to send the currently buffered message,
     * if existing.
     */
    private void send()
    {
        try
        {
            if ( m_message != null && m_buffer != null )
            {
                m_message.setText( m_buffer.toString() );
                Transport.send( m_message );
                m_message = null;
            }
        }
        catch ( MessagingException e )
        {
            getErrorHandler().error( "Error sending message", e, null );
        }
    }
}

