/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.excalibur.source;

import java.io.Serializable;

/**
 * A <code>SourceValidity</code> object contains all information to check if a Source
 * object is still valid.
 * <p>
 * There are two possibilities:
 * <ul>
 * <li>The validity object has all information to check by itself if it is valid
 *     (e.g. given an expires date).</li>
 * <li>The validity object possibility needs another (newer) validity object to compare
 *     against (e.g. to test a last modification date).</li>
 * </ul>
 * To avoid testing what the actual implementation of the validity object supports,
 * the invocation order is to first call {@link #isValid()} and only if this result
 * is <code>0</code> (i.e. "don't know"), then to call {@link #isValid(SourceValidity)}.
 * <p>
 * Remember to call {@link #isValid(SourceValidity)} when {@link #isValid()} returned
 * <code>0</code> !
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 12:46:57 $
 */
public interface SourceValidity
    extends Serializable
{
    final int VALID   = +1;
    final int INVALID = -1;
    /** @deprecated because it has been misspelled, use UNKNOWN of course */
    final int UNKNWON = 0;
    final int UNKNOWN = 0;
    
    /**
     * Check if the component is still valid. The possible results are :
     * <ul>
     * <li><code>-1</code>: invalid. The component isn't valid anymore.</li>
     * <li><code>0</code>: don't know. This validity should be checked against a new
     *     validity object using {@link #isValid(SourceValidity)}.</li>
     * <li><code>1</code>: valid. The component is still valid.</li>
     * </ul>
     */
    int isValid();

    /**
     * Check if the component is still valid. This is only true if the incoming Validity
     * is of the same type and has the "same" values.
     * <p>
     * The invocation order is that the isValid
     * method of the old Validity object is called with the new one as a
     * parameter.
     * @return -1 is returned, if the validity object is not valid anymore
     *          +1 is returned, if the validity object is still valid
     *          0  is returned, if the validity check could not be performed.
     *             In this case, the new validity object is not usable. Examples
     *             for this are: when the validity objects have different types,
     *             or when one validity object for any reason is not able to
     *             get the required information.
     */
    int isValid( SourceValidity newValidity );
}
