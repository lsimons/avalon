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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * The AbstractDataSourceBlockIdGenerator allocates blocks of ids from a DataSource
 *  and then provides them as needed.  This is useful in reducing communication with
 *  the DataSource.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.4 $ $Date: 2003/04/29 03:02:03 $
 * @since 4.1
 */
public abstract class AbstractDataSourceBlockIdGenerator
    extends AbstractDataSourceIdGenerator
{
    /**
     * The first id in a batch of Ids loaded in from the DataSource.
     */
    private BigDecimal m_firstBigDecimal;

    /**
     * The first id in a batch of Ids loaded in from the DataSource.
     */
    private long m_firstLong;

    /**
     * The number of ids loaded in each block.
     */
    private int m_blockSize;

    /**
     * The number of ids which have been allocated from the current block.
     */
    private int m_allocated;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public AbstractDataSourceBlockIdGenerator()
    {
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Allocates a block, of the given size, of ids from the database.
     *
     * @param blockSize number of Ids which are to be allocated.
     *
     * @return The first id in the allocated block.
     *
     * @throws IdException if there it was not possible to allocate a block of ids.
     */
    protected abstract BigDecimal allocateBigDecimalIdBlock( int blockSize )
        throws IdException;

    /**
     * Allocates a block, of the given size, of ids from the database.
     *
     * @param blockSize number of Ids which are to be allocated.
     *
     * @return The first id in the allocated block.
     *
     * @throws IdException if there it was not possible to allocate a block of ids.
     */
    protected abstract long allocateLongIdBlock( int blockSize )
        throws IdException;

    /*---------------------------------------------------------------
     * AbstractIdGenerator Methods
     *-------------------------------------------------------------*/
    /**
     * Gets the next id as a Big Decimal.  This method will only be called
     *  when synchronized and when the data type is configured to be BigDecimal.
     *
     * @return the next id as a BigDecimal.
     *
     * @throws IdException if an Id could not be allocated for any reason.
     */
    protected BigDecimal getNextBigDecimalIdInner()
        throws IdException
    {
        if( m_allocated >= m_blockSize )
        {
            // Need to allocate a new batch of ids
            try
            {
                m_firstBigDecimal = allocateBigDecimalIdBlock( m_blockSize );

                // Reset the allocated count
                m_allocated = 0;
            }
            catch( IdException e )
            {
                // Set the allocated count to signal that there are not any ids available.
                m_allocated = Integer.MAX_VALUE;
                throw e;
            }
        }

        // We know that at least one id is available.
        // Get an id out of the currently allocated block.
        BigDecimal id = m_firstBigDecimal.add( new BigDecimal( m_allocated ) );
        m_allocated++;

        return id;
    }

    /**
     * Gets the next id as a long.  This method will only be called
     *  when synchronized and when the data type is configured to be long.
     *
     * @return the next id as a long.
     *
     * @throws IdException if an Id could not be allocated for any reason.
     */
    protected long getNextLongIdInner()
        throws IdException
    {
        if( m_allocated >= m_blockSize )
        {
            // Need to allocate a new batch of ids
            try
            {
                m_firstLong = allocateLongIdBlock( m_blockSize );

                // Reset the allocated count
                m_allocated = 0;
            }
            catch( IdException e )
            {
                // Set the allocated count to signal that there are not any ids available.
                m_allocated = Integer.MAX_VALUE;
                throw e;
            }
        }

        // We know that at least one id is available.
        // Get an id out of the currently allocated block.
        long id = m_firstLong + m_allocated;
        if( id < 0 )
        {
            // The value wrapped
            String msg = "No more Ids are available, the maximum long value has been reached.";
            getLogger().error( msg );
            throw new IdException( msg );
        }
        m_allocated++;

        return id;
    }

    /*---------------------------------------------------------------
     * Configurable Methods
     *-------------------------------------------------------------*/
    /**
     * Called by the Container to configure the component.
     *
     * @param configuration configuration info used to setup the component.
     *
     * @throws ConfigurationException if there are any problems with the configuration.
     */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        super.configure( configuration );

        // Obtain the block size.
        m_blockSize = configuration.getAttributeAsInteger( "block-size", 10 );
    }

    /*---------------------------------------------------------------
     * Initializable Methods
     *-------------------------------------------------------------*/
    /**
     * Called by the Container to initialize the component.
     *
     * @throws Exception if there were any problems durring initialization.
     */
    public void initialize()
        throws Exception
    {
        super.initialize();

        // Set the state so that the first request for an id will load in a block of ids.
        m_allocated = Integer.MAX_VALUE;
    }
}

