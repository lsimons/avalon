/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.source.impl.validity;

import org.apache.excalibur.source.SourceValidity;

/**
 * A validation object which is always valid.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/06/04 08:42:13 $
 */
public final class NOPValidity
    implements SourceValidity
{

    public static final SourceValidity SHARED_INSTANCE = new NOPValidity();

    /**
     * Check if the component is still valid.
     * If <code>0</code> is returned the isValid(SourceValidity) must be
     * called afterwards!
     * If -1 is returned, the component is not valid anymore and if +1
     * is returnd, the component is valid.
     */
    public int isValid()
    {
        return 1;
    }

    public boolean isValid( SourceValidity newValidity )
    {
        return newValidity instanceof NOPValidity;
    }

    public String toString()
    {
        return "NOPValidity";
    }

}
