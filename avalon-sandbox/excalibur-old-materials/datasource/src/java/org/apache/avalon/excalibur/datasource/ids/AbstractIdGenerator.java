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
package org.apache.avalon.excalibur.datasource.ids;

import java.math.BigDecimal;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.4 $ $Date: 2003/04/29 03:02:03 $
 * @since 4.1
 */
public abstract class AbstractIdGenerator
    extends AbstractLogEnabled
    implements IdGenerator, ThreadSafe
{
    private static final BigDecimal BIG_DECIMAL_MAX_LONG = new BigDecimal( Long.MAX_VALUE );

    /**
     * Used to manage internal synchronization.
     */
    private Object m_semaphore = new Object();

    /**
     * Data type for the Id Pool.
     */
    private boolean m_useBigDecimals;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public AbstractIdGenerator()
    {
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Gets the next id as a Big Decimal.  This method will only be called
     *  when synchronized and when the data type is configured to be BigDecimal.
     *
     * @return the next id as a BigDecimal.
     *
     * @throws IdException if an Id could not be allocated for any reason.
     */
    protected abstract BigDecimal getNextBigDecimalIdInner()
        throws IdException;

    /**
     * Gets the next id as a long.  This method will only be called
     *  when synchronized and when the data type is configured to be long.
     *
     * @return the next id as a long.
     *
     * @throws IdException if an Id could not be allocated for any reason.
     */
    protected abstract long getNextLongIdInner()
        throws IdException;

    /**
     * By default, the IdGenerator will operate using a backend datatype of type long.  This
     *  is the most efficient, however it does not allow for Ids that are larger than
     *  Long.MAX_VALUE.  To allow very large Ids, it is necessary to make use of the BigDecimal
     *  data storage type.  This method should only be called durring initialization.
     *
     * @param useBigDecimals True to set BigDecimal as the internal data type.
     */
    protected final void setUseBigDecimals( boolean useBigDecimals )
    {
        m_useBigDecimals = useBigDecimals;
    }

    /**
     * Returns true if the internal data type is using BigDecimals, false if it is using longs.
     */
    protected final boolean isUsingBigDecimals()
    {
        return m_useBigDecimals;
    }

    /**
     * Gets the next Long Id constraining the value to be less than the specified maxId.
     *
     * @throws IdException if the next id is larger than the specified maxId.
     */
    protected final long getNextLongIdChecked( long maxId )
        throws IdException
    {
        long nextId;
        if( m_useBigDecimals )
        {
            // Use BigDecimal data type
            BigDecimal bd;
            synchronized( m_semaphore )
            {
                bd = getNextBigDecimalIdInner();
            }

            // Make sure that the Big Decimal value can be assigned to a long before continuing.
            if( bd.compareTo( BIG_DECIMAL_MAX_LONG ) > 0 )
            {
                String msg = "Unable to provide an id.  The next id would " +
                    "be greater than the id data type allows.";
                getLogger().error( msg );
                throw new IdException( msg );
            }
            nextId = bd.longValue();
        }
        else
        {
            // Use long data type
            synchronized( m_semaphore )
            {
                nextId = getNextLongIdInner();
            }
        }

        // Make sure that the id is valid for the requested data type.
        if( nextId > maxId )
        {
            String msg = "Unable to provide an id.  The next id would " +
                "be greater than the id data type allows.";
            getLogger().error( msg );
            throw new IdException( msg );
        }

        return nextId;
    }

    /*---------------------------------------------------------------
     * IdGenerator Methods
     *-------------------------------------------------------------*/
    /**
     * Returns the next Id from the pool.
     *
     * @return the next Id.
     */
    public final BigDecimal getNextBigDecimalId()
        throws IdException
    {
        BigDecimal bd;
        if( m_useBigDecimals )
        {
            // Use BigDecimal data type
            synchronized( m_semaphore )
            {
                bd = getNextBigDecimalIdInner();
            }
        }
        else
        {
            // Use long data type
            synchronized( m_semaphore )
            {
                bd = new BigDecimal( getNextLongIdInner() );
            }
        }

        return bd;
    }

    /**
     * Returns the next Id from the pool.
     *
     * @return the next Id.
     *
     * @throws IdException if the next id is outside of the range of valid longs.
     */
    public final long getNextLongId()
        throws IdException
    {
        return getNextLongIdChecked( Long.MAX_VALUE );
    }

    /**
     * Returns the next Id from the pool.
     *
     * @return the next Id.
     *
     * @throws IdException if the next id is outside of the range of valid integers.
     */
    public final int getNextIntegerId()
        throws IdException
    {
        return (int)getNextLongIdChecked( Integer.MAX_VALUE );
    }

    /**
     * Returns the next Id from the pool.
     *
     * @return the next Id.
     *
     * @throws IdException if the next id is outside of the range of valid shorts.
     */
    public final short getNextShortId()
        throws IdException
    {
        return (short)getNextLongIdChecked( Short.MAX_VALUE );
    }

    /**
     * Returns the next Id from the pool.
     *
     * @return the next Id.
     *
     * @throws IdException if the next id is outside of the range of valid bytes.
     */
    public final byte getNextByteId()
        throws IdException
    {
        return (byte)getNextLongIdChecked( Byte.MAX_VALUE );
    }
}

