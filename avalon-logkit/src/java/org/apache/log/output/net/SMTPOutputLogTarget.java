/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
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
 * @version CVS $Id: SMTPOutputLogTarget.java,v 1.2 2002/10/02 01:55:08 donaldp Exp $
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

