/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

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

package org.apache.avalon.phoenix.test.data;

import java.util.Map;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * A test component.
 *
 * @author Peter Donald
 * @version $Revision: 1.9 $ $Date: 2003/12/05 15:14:39 $
 */
public class Component4
    implements Serviceable
{
    private static final String KEY = Service2.ROLE + "{}";

    public void service( final ServiceManager manager )
        throws ServiceException
    {
        final Map services =
            (Map)manager.lookup( KEY );
        System.out.println( "Passed the following services: " +
                            services );

        final int size = services.size();
        if( 3 != size )
        {
            final String message =
                "Expected to get 3 services but got " + size;
            throw new ServiceException( KEY, message );
        }

        checkService( "c2a", services );
        checkService( "c2b", services );
        checkService( "fred", services );

        checkReadOnly( services );
    }

    private void checkReadOnly( final Map services )
        throws ServiceException
    {
        try
        {
            services.put( "s", services.get( "fred" ) );
        }
        catch( Exception e )
        {
            return;
        }

        throw new ServiceException( KEY,
                                    "Was able to modify map " +
                                    "retrieved from ServiceManager" );
    }

    private void checkService( final String name,
                               final Map services )
        throws ServiceException
    {
        final Object service1 = services.get( name );
        if( null == service1 )
        {
            final String message =
                "Expected to get service " + name;
            throw new ServiceException( name, message );
        }
        else if( !( service1 instanceof Service2 ) )
        {
            final String message =
                "Expected to service " + name +
                " to be of type Service2 but was " +
                "of type: " + service1.getClass().getName();
            throw new ServiceException( KEY, message );
        }
    }
}
