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

import org.apache.avalon.excalibur.datasource.DataSourceComponent;

/**
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.5 $ $Date: 2003/04/29 03:03:29 $
 * @since 4.1
 */
public interface HashedDataSourceCluster
    extends DataSourceComponent
{
    /**
     * The name of the role for convenience
     */
    String ROLE = "org.apache.avalon.excalibur.datasource.cluster.HashedDataSourceCluster";

    /**
     * Returns the number of DataSources in the cluster.
     *
     * @return size of the cluster.
     */
    int getClusterSize();

    /**
     * Gets a Connection to a database given a hash object.
     *
     * @param hashObject Object whose hashCode will be used to select which of the Clusted
     *        DataSources will be provide a Connection.
     *
     * @throws org.apache.avalon.excalibur.datasource.NoValidConnectionException when there is no valid Connection wrapper
     *         available in the classloader or when the index is not valid.
     *
     * @throws org.apache.avalon.excalibur.datasource.NoAvailableConnectionException when there are no more available
     *         Connections in the pool.
     */
    Connection getConnectionForHashObject( Object hashObject ) throws SQLException;

    /**
     * Gets a Connection to a database given a hash code.
     *
     * @param hashCode HashCode which will be used to select which of the Clusted
     *        DataSources will be provide a Connection.
     *
     * @throws org.apache.avalon.excalibur.datasource.NoValidConnectionException when there is no valid Connection wrapper
     *         available in the classloader or when the index is not valid.
     *
     * @throws org.apache.avalon.excalibur.datasource.NoAvailableConnectionException when there are no more available
     *         Connections in the pool.
     */
    Connection getConnectionForHashCode( int hashCode ) throws SQLException;

    /**
     * Gets a Connection to a database given an index.
     *
     * @param index Index of the DataSource for which a connection is to be returned.
     *
     * @throws org.apache.avalon.excalibur.datasource.NoValidConnectionException when there is no valid Connection wrapper
     *         available in the classloader or when the index is not valid.
     *
     * @throws org.apache.avalon.excalibur.datasource.NoAvailableConnectionException when there are no more available
     *         Connections in the pool.
     */
    Connection getConnectionForIndex( int index ) throws SQLException;

    /**
     * Gets the index which will be resolved for a given hashCode.  This can be used
     *  by user code to optimize the use of DataSource Clusters.
     *
     * @param hashObject Object whose hashCode will be used to select which of the Clusted
     *        DataSources will be provide a Connection.
     */
    int getIndexForHashObject( Object hashObject );

    /**
     * Gets the index which will be resolved for a given hashCode.  This can be used
     *  by user code to optimize the use of DataSource Clusters.
     *
     * @param hashCode HashCode which will be used to select which of the Clusted
     *        DataSources will be provide a Connection.
     */
    int getIndexForHashCode( int hashCode );
}

