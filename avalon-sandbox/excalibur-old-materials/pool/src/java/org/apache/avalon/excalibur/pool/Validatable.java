/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.pool;

/**
 * Used to define an object which can be validated by a ValidatedResourceLimitingPool.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 05:09:04 $
 * @since 4.1
 */
public interface Validatable
    extends Poolable
{
    /**
     * Called when an object is retrieved from a ValidatedResourceLimitingPool for reuse.
     *
     * @return true if the object is ok.  false will cause the object to be discarded.
     */
    boolean validate();
}

