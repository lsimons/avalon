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
package org.apache.avalon.excalibur.logger.factory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.avalon.excalibur.logger.LogTargetFactory;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.log.LogTarget;
import org.apache.log.output.db.ColumnInfo;
import org.apache.log.output.db.ColumnType;
import org.apache.log.output.db.DefaultJDBCTarget;
import org.apache.log.output.db.NormalizedJDBCTarget;

/**
 * Factory for JDBCLogTarget-s. The configuration looks like this:
 *
 * <pre>
 *  &lt;jdbc id="database"&gt;
 *      &lt;datasource&gt;java:/LogTargetDataSource&lt;/datasource&gt;
 *      &lt;normalized&gt;true&lt;/normalized&gt;
 *      &lt;table name="LOG"&gt;
 *          &lt;category&gt;CATEGORY&lt;/category&gt;
 *          &lt;priority&gt;PRIORITY&lt;/priority&gt;
 *          &lt;message&gt;MESSAGE&lt;/message&gt;
 *          &lt;time&gt;TIME&lt;/time&gt;
 *          &lt;rtime&gt;RTIME&lt;/rtime&gt;
 *          &lt;throwable&gt;THROWABLE&lt;/throwable&gt;
 *          &lt;hostname&gt;HOSTNAME&lt;/hostname&gt;
 *          &lt;static aux="-"&gt;STATIC&lt;/static&gt;
 *          &lt;context aux="principal"&gt;PRINCIPAL&lt;/context&gt;
 *          &lt;context aux="ipaddress"&gt;IPADDRESS&lt;/context&gt;
 *          &lt;context aux="username"&gt;USERNAME&lt;/context&gt;
 *      &lt;/table&gt;
 *  &lt;/jdbc&gt;
 * </pre>
 *
 * @author <a href="mailto:mirceatoma@home.com">Mircea Toma</a>;
 * @version CVS $Revision: 1.3 $ $Date: 2002/11/22 00:49:48 $
 */
public class JDBCTargetFactory implements LogTargetFactory
{
    public LogTarget createTarget( Configuration configuration )
        throws ConfigurationException
    {
        final String dataSourceName =
            configuration.getChild( "datasource", true ).getValue();

        final boolean normalized =
            configuration.getChild( "normalized", true ).getValueAsBoolean( false );

        final Configuration tableConfiguration =
            configuration.getChild( "table" );

        final String table = tableConfiguration.getAttribute( "name" );

        final Configuration[] conf = tableConfiguration.getChildren();
        final ColumnInfo[] columns = new ColumnInfo[ conf.length ];

        for( int i = 0; i < conf.length; i++ )
        {
            final String name = conf[ i ].getValue();
            final int type = ColumnType.getTypeIdFor( conf[ i ].getName() );
            final String aux = conf[ i ].getAttribute( "aux", null );

            columns[ i ] = new ColumnInfo( name, type, aux );
        }

        final DataSource dataSource;

        try
        {
            Context ctx = new InitialContext();
            dataSource = (DataSource)ctx.lookup( dataSourceName );
        }
        catch( final NamingException ne )
        {
            throw new ConfigurationException( "Cannot lookup data source", ne );
        }

        final LogTarget logTarget;
        if( normalized )
        {
            logTarget = new NormalizedJDBCTarget( dataSource, table, columns );
        }
        else
        {
            logTarget = new DefaultJDBCTarget( dataSource, table, columns );
        }

        return logTarget;
    }
}
