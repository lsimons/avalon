package org.apache.avalon.excalibur.datasource;

import java.lang.reflect.InvocationHandler;
import java.sql.Connection;

/**
 * A JDBC connection that has a proxy around it in order to dynamicly implement JDBC2/3 needs
 * to get a handle to its proxied self so that the proxied version can be returned to the pool.
 *
 * @author <a href="proyal@apache.org">peter royal</a>
 */
public interface ProxiedJdbcConnection extends InvocationHandler
{
    void setProxiedConnection( Object proxy );

    Connection getConnection();
}
