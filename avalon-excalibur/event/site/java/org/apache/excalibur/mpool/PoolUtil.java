/*
 * Created by IntelliJ IDEA.
 * User: bloritsch
 * Date: Sep 25, 2002
 * Time: 11:47:49 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.apache.excalibur.mpool;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class PoolUtil
{
    private final static Object[] EMPTY = new Object[] {};
    private final static Class[] EMPTY_ARGS = new Class[] {};

    private PoolUtil() {}

    public static Object recycle( final Object obj )
    {
        try
        {
            Class klass = obj.getClass();
            Method recycle = klass.getMethod( "recycle", EMPTY_ARGS );

            if ( Modifier.isPublic( recycle.getModifiers() ) &&
                    recycle.getReturnType().equals( void.class ) )
            {
                recycle.invoke( obj, EMPTY );
            }
        }
        catch (Exception e)
        {
            // Not a recyclable object--don't worry about it
        }

        return obj;
    }
}
