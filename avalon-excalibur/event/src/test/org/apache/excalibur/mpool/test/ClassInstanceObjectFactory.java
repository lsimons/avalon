/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

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
package org.apache.excalibur.mpool.test;

import java.util.HashMap;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.Logger;
import org.apache.excalibur.mpool.ObjectFactory;

/**
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/08/07 22:44:26 $
 * @since 4.1
 */
public class ClassInstanceObjectFactory
    implements ObjectFactory, org.apache.avalon.excalibur.pool.ObjectFactory
{
    private HashMap m_instances = new HashMap();
    private Logger m_logger;
    private Class m_clazz;
    private int m_id;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a reproducable log of activity in the provided StringBuffer
     */
    public ClassInstanceObjectFactory( Class clazz, Logger logger )
    {
        m_clazz = clazz;
        m_logger = logger;
        m_id = 1;
    }

    /*---------------------------------------------------------------
     * ObjectFactory Methods
     *-------------------------------------------------------------*/
    public Object newInstance() throws Exception
    {
        Object object = m_clazz.newInstance();
        Integer id = new Integer( m_id++ );

        m_instances.put( object, id );

        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "ClassInstanceObjectFactory.newInstance()  id:" + id );
        }

        return object;
    }

    public Class getCreatedClass()
    {
        return m_clazz;
    }

    public void dispose( Object object ) throws Exception
    {
        if( object instanceof Disposable )
        {
            ( (Disposable)object ).dispose();
        }
        Integer id = (Integer)m_instances.remove( object );

        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "ClassInstanceObjectFactory.decommission(a "
                            + object.getClass().getName() + ")  id:" + id );
        }
    }

    public void decommission( Object object ) throws Exception
    {
        dispose( object );
    }
}

