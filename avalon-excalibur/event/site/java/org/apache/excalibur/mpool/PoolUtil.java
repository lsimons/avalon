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
package org.apache.excalibur.mpool;

import java.lang.reflect.Method;


/**
 * The PoolUtil class performs the reflection magic that is necessary to work
 * with the legacy Recyclable interface in the
 * <a href="http://jakarta.apache.org/avalon/excalibur/pool">Pool</a> package.
 * It also works with the new Resettable interface in MPool.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.3 $ $Date: 2002/09/26 00:34:17 $
 */
public final class PoolUtil
{
    private final static Object[] EMPTY = new Object[] {};
    private final static Class[] EMPTY_ARGS = new Class[] {};

    private PoolUtil() {}

    /**
     * This method will either call "reset" on Resettable objects,
     * or it will call "recycle" on Recyclable objects.
     *
     * @param obj  The object you want recycled.
     * @return the same object
     */
    public static Object recycle( final Object obj )
    {
        if ( obj instanceof Resettable )
        {
            ( (Resettable) obj).reset();
        }
        else
        {
            try
            {
                Class klass = obj.getClass();
                Class recyclable = klass.getClassLoader().loadClass( "org.apache.avalon.excalibur.pool.Recyclable" );

                if ( recyclable.isAssignableFrom( klass ) )
                {
                    recycleLegacy( obj );
                }
            }
            catch (Exception e)
            {
                // No recyclable interface
            }
        }

        return obj;
    }

    private static void recycleLegacy( final Object obj ) throws Exception
    {
        Class klass = obj.getClass();
        Method recycle = klass.getMethod( "recycle", EMPTY_ARGS );
        recycle.invoke( obj, EMPTY );
    }
}
