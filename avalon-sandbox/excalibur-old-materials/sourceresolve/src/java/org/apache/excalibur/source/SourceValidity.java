/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.source;

/**
 * A Validity object contains all information to check if a Source object is
 * still valid.
 * There are two possibilities: The validity object has all information
 * to check by itself how long it is valid (e.g. given an expires date).
 * The other possibility needs another (newer) validity object to compare
 * against (e.g. to test a last modification date).
 * To avoid testing, what the actual implementation of the validity
 * object supports, the invocation order is to first call {@link #isValid()} and only if
 * this results in <code>0</code>, then to call {@link #isValid}.
 * But remember to call the second isValid(SourceValidity) when <code>0</code>
 * is returned by the first invocation!
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.3 $ $Date: 2002/06/23 11:26:13 $
 */
public interface SourceValidity
    extends java.io.Serializable
{
    /**
     * Check if the component is still valid.
     * If <code>0</code> is returned the isValid(SourceValidity) must be
     * called afterwards!
     * If -1 is returned, the component is not valid anymore and if +1
     * is returnd, the component is valid.
     */
    int isValid();

    /**
     * Check if the component is still valid.
     * This is only true, if the incoming Validity is of the same
     * type and has the same values.
     * The invocation order is that the isValid method of the
     * old Validity object is called with the new one as a parameter
     */
    boolean isValid( SourceValidity newValidity );
}
