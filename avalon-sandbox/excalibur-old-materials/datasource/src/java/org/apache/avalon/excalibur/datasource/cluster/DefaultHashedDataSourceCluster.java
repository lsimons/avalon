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
package org.apache.avalon.excalibur.datasource.cluster;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.avalon.excalibur.datasource.NoValidConnectionException;

/**
 * The DefaultHashedDataSourceCluster requires that the user specify an object or a hashCode
 *  which will be used consistantly select a member DataSource form a cluster for each connection
 *  request.  Calls to getConnection() will throw an exception.  Components which make use of
 *  this class must call either the getConnectionForHashObject( Object hashObject) or the
 *  getConnectionForHashCode( int hashCode ) methods instead.
 * <p>
 * This form of Clustering is useful in cases where data can be reliably accessed in a repeatable
 *  manner.  For example a web site's visitor information could be accessed by using a String
 *  containing the visitor's username as a hashObject.  This would allow visitor information to be
 *  spread across several database servers.
 * <p>
 * The Configuration for a 2 database cluster is like this:
 *
 * <pre>
 *   &lt;datasources&gt;
 *     &lt;hashed-cluster name="mydb-cluster" size="2"&gt;
 *       &lt;dbpool index="0"&gt;mydb-0&lt;/dbpool&gt;
 *       &lt;dbpool index="1"&gt;mydb-1&lt;/dbpool&gt;
 *     &lt;/hashed-cluster&gt;
 *   &lt;/datasources&gt;
 *   &lt;cluster-datasources&gt;
 *     &lt;jdbc name="mydb-0"&gt;
 *       &lt;pool-controller min="1" max="10"/&gt;
 *       &lt;auto-commit&gt;true&lt;/auto-commit&gt;
 *       &lt;driver&gt;com.database.jdbc.JdbcDriver&lt;/driver&gt;
 *       &lt;dburl&gt;jdbc:driver://host0/mydb&lt;/dburl&gt;
 *       &lt;user&gt;username&lt;/user&gt;
 *       &lt;password&gt;password&lt;/password&gt;
 *     &lt;/jdbc&gt;
 *     &lt;jdbc name="mydb-1"&gt;
 *       &lt;pool-controller min="1" max="10"/&gt;
 *       &lt;auto-commit&gt;true&lt;/auto-commit&gt;
 *       &lt;driver&gt;com.database.jdbc.JdbcDriver&lt;/driver&gt;
 *       &lt;dburl&gt;jdbc:driver://host1/mydb&lt;/dburl&gt;
 *       &lt;user&gt;username&lt;/user&gt;
 *       &lt;password&gt;password&lt;/password&gt;
 *     &lt;/jdbc&gt;
 *   &lt;/cluster-datasources&gt;
 * </pre>
 *
 * With the following roles declaration:
 *
 * <pre>
 *   &lt;role name="org.apache.avalon.excalibur.datasource.DataSourceComponentSelector"
 *     shorthand="datasources"
 *     default-class="org.apache.avalon.excalibur.component.ExcaliburComponentSelector"&gt;
 *     &lt;hint shorthand="jdbc" class="org.apache.avalon.excalibur.datasource.JdbcDataSource"/&gt;
 *     &lt;hint shorthand="j2ee" class="org.apache.avalon.excalibur.datasource.J2eeDataSource"/&gt;
 *     &lt;hint shorthand="hashed-cluster"
 *       class="org.apache.avalon.excalibur.datasource.cluster.DefaultHashedDataSourceCluster"/&gt;
 *   &lt;/role&gt;
 *   &lt;role name="org.apache.avalon.excalibur.datasource.DataSourceComponentClusterSelector"
 *       shorthand="cluster-datasources"
 *       default-class="org.apache.avalon.excalibur.component.ExcaliburComponentSelector"&gt;
 *     &lt;hint shorthand="jdbc" class="org.apache.avalon.excalibur.datasource.JdbcDataSource"/&gt;
 *     &lt;hint shorthand="j2ee" class="org.apache.avalon.excalibur.datasource.J2eeDataSource"/&gt;
 *   &lt;/role&gt;
 * </pre>
 *
 * A hashed-cluster definition enforces that the configuration specify a size.  This size must
 *  equal the number of datasources referenced as being members of the cluster.  Any datasource can
 *  be a member of the cluster.
 * <p>
 * The hashed-cluster can be obtained in the same manner as a non-clustered datasource.  The only
 *  difference is in how it is used.  The HashedDataSourceCluster requires that the caller provide
 *  an object or a hashCode to use when requesting a connection.
 * <p>
 * The following code demonstrates a change that can be made to database enabled components so that
 *  they will be able to work with both HashedDataSourceCluster DataSources and regular
 *  DataSources.
 * <p>
 * old:
 * <pre>
 *   Connection connection = m_dataSource.getConnection();
 * </pre>
 *
 * new:
 * <pre>
 *   Connection connection;
 *   if ( m_dataSource instanceof HashedDataSourceCluster )
 *   {
 *     connection = ((HashedDataSourceCluster)m_dataSource).getConnectionForHashObject( hashObject );
 *   }
 *   else
 *   {
 *     connection = m_dataSource.getConnection();
 *   }
 * </pre>
 * 
 * @avalon.component
 * @avalon.service type=org.apache.avalon.excalibur.datasource.DataSourceComponent
 * @avalon.service type=HashedDataSourceCluster
 * @x-avalon.info name=hash-db-cluster
 * @x-avalon.lifestyle type=singleton
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.6 $ $Date: 2003/05/20 20:44:43 $
 * @since 4.1
 */
public class DefaultHashedDataSourceCluster
    extends AbstractDataSourceCluster
    implements HashedDataSourceCluster
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public DefaultHashedDataSourceCluster()
    {
    }

    /*---------------------------------------------------------------
     * DataSourceComponent Methods
     *-------------------------------------------------------------*/
    /**
     * Not supported in this component.  Will throw a NoValidConnectionException.
     */
    public Connection getConnection() throws SQLException
    {
        throw new NoValidConnectionException(
            "getConnection() should not be called for a " + getClass().getName() + ".  " +
            "Please verify your configuration." );
    }

    /*---------------------------------------------------------------
     * HashedDataSourceCluster Methods
     *-------------------------------------------------------------*/
    // public int getClusterSize()
    //   declared in AbstractDataSourceCluster

    /**
     * Gets a Connection to a database given a hash object.
     *
     * @param hashObject Object whose hashCode will be used to select which of the Clusted
     *        DataSources will be provide a Connection.
     *
     * @throws NoValidConnectionException when there is no valid Connection wrapper
     *         available in the classloader or when the index is not valid.
     *
     * @throws NoValidConnectionException when there are no more available
     *         Connections in the pool.
     */
    public Connection getConnectionForHashObject( Object hashObject ) throws SQLException
    {
        return getConnectionForIndex( getIndexForHashObject( hashObject ) );
    }

    /**
     * Gets a Connection to a database given a hash code.
     *
     * @param hashCode HashCode which will be used to select which of the Clusted
     *        DataSources will be provide a Connection.
     *
     * @throws NoValidConnectionException when there is no valid Connection wrapper
     *         available in the classloader or when the index is not valid.
     *
     * @throws NoValidConnectionException when there are no more available
     *         Connections in the pool.
     */
    public Connection getConnectionForHashCode( int hashCode ) throws SQLException
    {
        return getConnectionForIndex( getIndexForHashCode( hashCode ) );
    }

    // public Connection getConnectionForIndex( int index ) throws SQLException
    //   declared in AbstractDataSourceCluster

    /**
     * Gets the index which will be resolved for a given hashCode.  This can be used
     *  by user code to optimize the use of DataSource Clusters.
     * <p>
     * Subclasses can override this method to get different behavior.
     * <p>
     * By default the index = getIndexForHashCode( hashObject.hashCode() )
     *
     * @param hashObject Object whose hashCode will be used to select which of the Clusted
     *        DataSources will be provide a Connection.
     */
    public int getIndexForHashObject( Object hashObject )
    {
        return getIndexForHashCode( hashObject.hashCode() );
    }

    /**
     * Gets the index which will be resolved for a given hashCode.  This can be used
     *  by user code to optimize the use of DataSource Clusters.
     * <p>
     * Subclasses can override this method to get different behavior.
     * <p>
     * By default the index = hashCode % getClusterSize()
     *
     * @param hashCode HashCode which will be used to select which of the Clusted
     *        DataSources will be provide a Connection.
     */
    public int getIndexForHashCode( int hashCode )
    {
        // DEVELOPER WARNING:
        // If you change the way the hashCode is calculated, you WILL BREAK
        //  things for existing users, so please do so only after much thought.

        // Hash code may be negative, Make them all positive by using the unsigned int value.
        long lHashCode = ( (long)hashCode ) & 0xffffffffL;

        return (int)( lHashCode % getClusterSize() );
    }
}

