/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.datasource.ids;

import java.math.BigDecimal;
import org.apache.avalon.framework.component.Component;

/**
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/22 03:04:27 $
 * @since 4.1
 */
public interface IdGenerator
    extends Component
{
    /**
     * The name of the role for convenience
     */
    String ROLE = "org.apache.avalon.excalibur.datasource.ids.IdGenerator";

    /**
     * Returns the next Id from the pool.
     *
     * @return the next Id.
     */
    BigDecimal getNextBigDecimalId()
        throws IdException;

    /**
     * Returns the next Id from the pool.
     *
     * @return the next Id.
     *
     * @throws IllegalStateException if the next id is outside of the range of valid longs.
     */
    long getNextLongId()
        throws IdException;

    /**
     * Returns the next Id from the pool.
     *
     * @return the next Id.
     *
     * @throws IllegalStateException if the next id is outside of the range of valid integers.
     */
    int getNextIntegerId()
        throws IdException;

    /**
     * Returns the next Id from the pool.
     *
     * @return the next Id.
     *
     * @throws IllegalStateException if the next id is outside of the range of valid shorts.
     */
    short getNextShortId()
        throws IdException;

    /**
     * Returns the next Id from the pool.
     *
     * @return the next Id.
     *
     * @throws IllegalStateException if the next id is outside of the range of valid bytes.
     */
    byte getNextByteId()
        throws IdException;
}
