package org.apache.avalon.phoenix;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * Subclasses of <code>AstractChainedInvocable</code> form chain of
 * block invocation interceptors.
 * 
 * <p>The idea is to be able to extend block's behaviour without changing 
 * block's code. One of usages of such interceptors would be 
 * <code>SecurityInterceptor</code> which verifies if a caller is allowed to 
 * perform requested operation on a block.</p>
 *
 * @author ifedorenko
 */
public abstract class AstractChainedInvocable extends AbstractLogEnabled
    implements InvocationHandler
{

    private transient Object m_object;

    private InvocationHandler m_chained;

    public final void setObject( Object object )
    {
        m_object = object;
    }

    public final void setChained( InvocationHandler chained )
    {
        m_chained = chained;
    }

    /**
     * @see java.lang.reflect.InvocationHandler#invoke(Object, Method, Object[])
     */
    public Object invoke( final Object proxy,
                          final Method method,
                          final Object[] args )
        throws Throwable
    {
        return ( m_chained != null )
                ? m_chained.invoke( proxy, method, args )
                : invokeObject( proxy, method, args );
    }

    /**
     * Invoke the specified method on underlying object.
     * This is called by proxy object.
     *
     * @param proxy the proxy object
     * @param method the method invoked on proxy object
     * @param args the arguments supplied to method
     * @return the return value of method
     * @throws Throwable if an error occurs
     */
    private Object invokeObject( final Object proxy,
                                 final Method method,
                                 final Object[] args )
        throws Throwable
    {
        if( null != m_object )
        {
            try
            {
                return method.invoke( m_object, args );
            }
            catch( final InvocationTargetException ite )
            {
                throw ite.getTargetException();
            }
        }
        else
        {
            throw new IllegalStateException( "Using a stale object reference "
                                             + "to call a disposed Block." );
        }
    }
}
