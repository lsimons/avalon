/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

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
package org.apache.avalon.excalibur.logger.util;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.container.ContainerUtil;
import java.util.ArrayList;

/**
 * This class broadcasts Avalon lifestyle events to several
 * destination objects, somewhat like Unix 'tee' command
 * directing its input both to file and to its output.
 *
 * The current implementation is incomplete and handles
 * only LogEnabled, Contextutalizable, Configurable and Disposable
 * interfaces.
 *
 * @author <a href="http://cvs.apache.org/~atagunov">Anton Tagunov</a>
 * @version CVS $Revision: 1.2 $ $Date: 2003/06/11 12:17:04 $
 * @since 4.0
 */

public class AvalonTee implements
        LogEnabled,
        Contextualizable,
        Configurable,
        Startable, 
        Disposable
{
    /* The objects we direct our events to */
    private ArrayList m_listeners = new ArrayList( 10 );
    /**
     * The number of these objects. This variable is here
     * is to save a line of code in every reactor method
     * rather then to optimize speed.
     */
    private int m_len = 0;

    /* Has adding new tees been prohibited? */
    private boolean m_readOnly = false;

    /**
     * Disallow adding more tees.
     */
    public void makeReadOnly()
    {
        m_readOnly = true;
    }

    /**
     * Adds an object to the list of objects receiving events.
     * @param obj the object to add; can not be null.
     */
    public void addTee( final Object obj )
    {
        if ( m_readOnly )
        {
            throw new IllegalStateException( "makeReadOnly() already invoked" );
        }

        if ( obj == null ) throw new NullPointerException( "obj" );
        if ( m_listeners.contains( obj ) )
        {
            // should we complain? better not, probably 
        }
        else
        {
            // adds to the end of the array
            m_listeners.add( obj );
            m_len = m_listeners.size();
        }
    }

    public void enableLogging( final Logger logger )
    {
        for( int i = 0; i < m_len; ++i )
        {
            ContainerUtil.enableLogging( m_listeners.get( i ), logger );
        }
    }

    public void contextualize( final Context context ) throws ContextException
    {
        for( int i = 0; i < m_len; ++i )
        {
            ContainerUtil.contextualize( m_listeners.get( i ), context );
        }
    }

    public void configure( final Configuration config ) throws ConfigurationException
    {
        for( int i = 0; i < m_len; ++i )
        {
            ContainerUtil.configure( m_listeners.get( i ), config );
        }
    }

    public void start() throws Exception
    {
        for( int i = 0; i < m_len; ++i )
        {
            ContainerUtil.start( m_listeners.get( i ) );
        }
    }

    public void stop() throws Exception
    {
        for( int i = 0; i < m_len; ++i )
        {
            ContainerUtil.stop( m_listeners.get( i ) );
        }
    }

    public void dispose()
    {
        for( int i = 0; i < m_len; ++i )
        {
            ContainerUtil.dispose( m_listeners.get( i ) );
        }
    }
}
