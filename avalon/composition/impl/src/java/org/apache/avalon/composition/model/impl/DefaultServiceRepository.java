/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.avalon.composition.model.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.avalon.composition.model.ServiceUnknownException;
import org.apache.avalon.composition.model.ServiceRepository;
import org.apache.avalon.framework.Version;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.meta.info.ReferenceDescriptor;
import org.apache.avalon.meta.info.Service;

/**
 * A service repository provides support for the retrival
 * of service defintions.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2003/10/28 20:21:00 $
 */
public class DefaultServiceRepository implements ServiceRepository
{
    //==============================================================
    // immutable state
    //==============================================================

    /**
     * The logging channel.
     */
    private final Logger m_logger;

    /**
     * The parent service manager (may be null)
     */
    private final ServiceRepository m_parent;

    /**
     * List of service entries.
     */
    private final List m_services;

    //==============================================================
    // constructor
    //==============================================================

    /**
     * Creation of a new root service manager.
     * @param logger the logging channel
     * @param services the list of available services
     * @exception NullPointerException if the services list is null
     */
    DefaultServiceRepository( 
      final Logger logger, final List services ) throws NullPointerException
    {
        this( logger, null, services );
    }

    /**
     * Creation of a new service manager.
     * @param logger the logging channel
     * @param parent the parent type manager
     * @param services the list of available services
     * @exception NullPointerException if the services list is null
     */
    DefaultServiceRepository( 
      final Logger logger, final ServiceRepository parent, final List services ) 
      throws NullPointerException
    {
        if( services == null )
        {
            throw new NullPointerException( "services" );
        }
        m_parent = parent;
        m_services = services;
        m_logger = logger;
    }

    //==============================================================
    // implemetation
    //==============================================================

    /**
     * Locate a {@link Service} instances associated with the
     * supplied classname and version. If a service defintion is not
     * found locally, the implementation redirects the request to
     * the parent service manager.
     *
     * @param classname the service class name
     * @param version the service version
     * @return the service matching the supplied classname and version.
     * @exception UnknownServiceException if a matching service cannot be found
     */
    public Service getService( final String classname, final Version version ) 
      throws ServiceUnknownException
    {
        return getService( new ReferenceDescriptor( classname, version ) );
    }

    /**
     * Locate a {@link Service} instances associated with the
     * supplied referecne descriptor. If a service defintion is not
     * found locally, the implementation redirects the request to
     * the parent service manager.
     *
     * @param reference the reference descriptor
     * @return the service matching the supplied descriptor.
     * @exception UnknownServiceException if a matching service cannot be found
     */
    public Service getService( final ReferenceDescriptor reference ) 
      throws ServiceUnknownException
    {
        Service service = getLocalService( reference );
        if( service == null )
        {
            if( m_parent != null )
            {
                return m_parent.getService( reference );
            } 
            else
            {
                final String error = "Unknown service defintion: " + reference;
                throw new ServiceUnknownException( error );
            }
        }
        return service;
    }

    private Service getLocalService( final ReferenceDescriptor reference )
    {
        Iterator iterator = m_services.iterator();
        while( iterator.hasNext() )
        {
            Service service = (Service) iterator.next();
            if( service.equals( reference ) )
            {
                return service;
            }
        }
        return null;
    }

    protected Logger getLogger()
    {
        return m_logger;
    }
}
