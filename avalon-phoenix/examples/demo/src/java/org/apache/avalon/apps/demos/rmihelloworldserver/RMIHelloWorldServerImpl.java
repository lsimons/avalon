/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002,2003 The Apache Software Foundation. All rights
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
 * 4. The names "Avalon", and "Apache Software Foundation"
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
package org.apache.avalon.apps.demos.rmihelloworldserver;

import java.rmi.RemoteException;
import org.apache.avalon.cornerstone.services.rmification.RMIfication;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * @phoenix:block
 * @phoenix:service name="org.apache.avalon.apps.demos.rmihelloworldserver.RMIHelloWorldServer"
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class RMIHelloWorldServerImpl
    extends AbstractLogEnabled
    implements Serviceable, Configurable, Initializable, RMIHelloWorldServer
{
    private RMIfication m_rmification;
    private String m_publicationName;

    /**
     *
     * @phoenix:dependency name="org.apache.avalon.cornerstone.services.rmification.RMIfication"
     *
     */
    public void service( final ServiceManager serviceManager )
        throws ServiceException
    {
        m_rmification = (RMIfication)serviceManager.lookup( RMIfication.ROLE );
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_publicationName = configuration.getChild( "pub-name" ).getValue();
    }

    public void initialize()
        throws Exception
    {
        m_rmification.publish( this, m_publicationName );
    }

    public void dispose()
    {
        try
        {
            m_rmification.unpublish( m_publicationName );
        }
        catch( final Exception e )
        {
            getLogger().error( "Fail to unpublish service", e );
        }
    }

    public String sayHello( final String yourName )
        throws RemoteException
    {
        return "Hello " + yourName + ".";
    }
}
