/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.logger.factory;

import javax.mail.Session;
import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.ContextException;
import org.apache.log.LogTarget;
import org.apache.log.format.Formatter;
import org.apache.log.output.net.SMTPOutputLogTarget;

/**
 * SMTPTargetFactory class.
 *
 * <p>
 * This factory creates SMTPOutputLogTarget's. It uses the
 * context-key attribute to locate the required JavaMail Session from
 * the Context object passed to this factory.  The default context-key
 * is <code>session-context</code>.
 * </p>
 *
 * <p>
 * <pre>
 * &lt;smtp id="target-id" context-key="context-key-to-session-object"&gt;
 *   &lt;format type="raw|pattern|extended"&gt;pattern to be used if needed&lt;/format&gt;
 *   &lt;to&gt;address-1@host&lt;/to&gt;
 *   &lt;to&gt;address-N@host&lt;/to&gt;
 *   &lt;from&gt;address@host&lt;/from&gt;
 *   &lt;subject&gt;subject line&lt;/subject&gt;
 *   &lt;maximum-size&gt;number&lt;/maximum-size&gt;
 * &lt;/smtp&gt;
 * </pre>
 *
 * <dl>
 *  <dt>&lt;format&gt;</dt>
 *  <dd>
 *   The type attribute of the pattern element denotes the type of
 *   Formatter to be used and according to it the pattern to use for.
 *   This elements defaults to:
 *   <p>
 *    %7.7{priority} %5.5{time}   [%8.8{category}] (%{context}): %{message}\\n%{throwable}
 *   </p>
 *  </dd>
 * </dl>
 * <p>
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/05/23 19:42:55 $
 * @since 4.1
 */
public class SMTPTargetFactory
    extends AbstractTargetFactory
{
    /**
     * Creates an SMTPOutputLogTarget based on a Configuration
     *
     * @param configuration a <code>Configuration</code> instance
     * @return <code>LogTarget</code> instance
     * @exception ConfigurationException if an error occurs
     */
    public final LogTarget createTarget( final Configuration config )
        throws ConfigurationException
    {
        if( m_context == null )
        {
            throw new ConfigurationException( "Context not available" );
        }

        try
        {
            return new SMTPOutputLogTarget(
                getSession(),
                getToAddresses( config ),
                getFromAddress( config ),
                getSubject( config ),
                getMaxSize( config ),
                getFormatter( config )
            );
        }
        catch( final ContextException ce )
        {
            throw new ConfigurationException( "Cannot find Session object in " +
                                              "application context", ce );
        }
        catch( final AddressException ae )
        {
            throw new ConfigurationException( "Cannot create address", ae );
        }
    }

    /**
     * Helper method to obtain a formatter for this factory.
     *
     * @param config a <code>Configuration</code> instance
     * @return a <code>Formatter</code> instance
     */
    protected Formatter getFormatter( final Configuration config )
    {
        final Configuration confFormat = config.getChild( "format" );

        if( null != confFormat )
        {
            final FormatterFactory formatterFactory = new FormatterFactory();
            return formatterFactory.createFormatter( confFormat );
        }

        return null;
    }

    /**
     * Helper method to obtain the JavaMail <code>Session</code> object
     * from this factories context object. Override this method if you
     * need to obtain the JavaMail session using some other way.
     *
     * @return JavaMail <code>Session</code> instance
     * @exception ContextException if an error occurs
     */
    protected Session getSession()
        throws ContextException
    {
        final String contextkey =
            m_configuration.getAttribute( "context-key", "session-context" );
        return (Session) m_context.get( contextkey );
    }

    /**
     * Helper method to obtain the subject line to use from the given
     * configuration object.
     *
     * @param config a <code>Configuration</code> instance
     * @return subject line
     */
    private String getSubject( Configuration config )
        throws ConfigurationException
    {
        return config.getChild( "subject" ).getValue();
    }

    /**
     * Helper method to obtain the maximum size any particular SMTP
     * message can be from a given configuration object.
     *
     * @param config a <code>Configuration</code> instance
     * @return maximum SMTP mail size
     */
    private int getMaxSize( Configuration config )
        throws ConfigurationException
    {
        return config.getChild( "maximum-size" ).getValueAsInteger();
    }

    /**
     * Helper method to obtain the <i>to</i> address/es from the 
     * given configuration.
     *
     * @param config <code>Configuration</code> instance
     * @return an array of <code>Address</code> objects
     * @exception ConfigurationException if a configuration error occurs
     * @exception AddressException if a addressing error occurs
     */
    private Address[] getToAddresses( final Configuration config )
        throws ConfigurationException, AddressException
    {
        final Configuration[] toAddresses = config.getChildren( "to" );
        final Address[] addresses = new Address[toAddresses.length];

        for (int i = 0; i < toAddresses.length; ++i)
        {
            addresses[i] = createAddress( toAddresses[i].getValue() );
        }

        return addresses;
    }

    /**
     * Helper method to obtain the <i>from</i> address from
     * the given configuration.
     *
     * @param config a <code>Configuration</code> instance
     * @return an <code>Address</code> object
     * @exception ConfigurationException if a configuration error occurs
     * @exception AddressException if a addressing error occurs
     */
    private Address getFromAddress( final Configuration config )
        throws ConfigurationException, AddressException
    {
        return createAddress( config.getChild( "from" ).getValue() );
    }

    /**
     * Helper factory method to create a new <code>Address</code>
     * object. Override this method in a subclass if you wish to
     * create other Address types rather than
     * <code>InternetAddress</code> (eg. <code>NewsAddress</code>)
     *
     * @param address address string from configuration
     * @return an <code>Address</code> object
     * @exception AddressException if an error occurs
     */
    protected Address createAddress( final String address )
        throws AddressException
    {
        return new InternetAddress( address );
    }
}
