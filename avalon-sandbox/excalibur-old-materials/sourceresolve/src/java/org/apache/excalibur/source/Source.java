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

import java.io.IOException;
import java.io.InputStream;

/**
 * This interface provides a simple interface for accessing a source of data.
 * <p>
 * When the <code>Source</code> object is no longer needed
 * it must be released using the {@link SourceResolver}. This is very similar to
 * looking up components from a <code>ServiceSelector</code>.
 * In fact a source object can implement most lifecycle interfaces
 * like Composable, Initializable, Disposable etc.
 * <p>
 * The data content can be constant or change over time.
 * Using the {@link #getInputStream()} method you get always the up-to-date content.
 * <p>
 * If you want to track changes of the source object, this interface
 * offers you some support for it by providing a SourceValidity object.
 * <p>
 * How does the caching work?
 * The first time you get a Source object, you simply ask
 * it for it's content via getInputStream() and then get the validity
 * object by invoking getValidity. (Further calls to getValidity always
 * return the same object! This is not updated!)
 * The caching algorithm can now store this validity object together
 * with the system identifier of the source.
 * The next time, the caching algorithm wants to check if the cached
 * content is still valid. It has a validity object already to check
 * against.
 * <p>
 * If it is still the same Source than the first time, you
 * have to call refresh() in order to discard the stored validity
 * in the Source object. If it is a new Source object,
 * calling refresh() should do no harm.
 * After that an up-to-date validity object can retrieved by calling
 * getValidity(). This can be used to test if the content is still valid
 * as discribed in the source validity documentation.
 * If the content is still valid, the cache knows what to do, if not,
 * the new content can be get using getInputStream().
 * So either after a call to getValidity() or the getInputStream the
 * validity object must be the same until refresh is called!
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @author <a href="mailto:sylvain@apache.org">Sylvain Wallez</a>
 * @version CVS $Revision: 1.15 $ $Date: 2003/07/02 13:20:40 $
 */
public interface Source
{
    /**
     * Does this source exist ?
     * 
     * @return true if the source exists
     */
    boolean exists();
    
    /**
     * Return an <code>InputStream</code> to read from the source.
     * This is the data at the point of invocation of this method,
     * so if this is Modifiable, you might get different content
     * from two different invocations.
     * 
     * @return the <code>InputStream</code> to read data from (never <code>null</code>).
     * @throws IOException if some I/O problem occurs.
     * @throws SourceNotFoundException if the source doesn't exist.
     */
    InputStream getInputStream()
        throws IOException, SourceNotFoundException;

    /**
     * Get the absolute URI for this source.
     * 
     * @return the source URI.
     */
    String getURI();

    /**
     * Return the URI scheme identifier, i.e. the part preceding the fist ':' in the URI
     * (see <a href="http://www.ietf.org/rfc/rfc2396.txt">RFC 2396</a>).
     * <p>
     * This scheme can be used to get the {@link SourceFactory} responsible for this object.
     * 
     * @return the URI scheme.
     */
    String getScheme();
    
    /**
     * Get the Validity object. This can either wrap the last modification date or
     * some expiry information or anything else describing this object's validity.
     * <p>
     * If it is currently not possible to calculate such an information,
     * <code>null</code> is returned.
     * 
     * @return the validity, or <code>null</code>.
     */
    SourceValidity getValidity();

    /**
     * Refresh the content of this object after the underlying data content has changed.
     * <p>
     * Some implementations may cache some values to speedup sucessive calls. Refreshing
     * ensures you get the latest information.
     */
    void refresh();

    /**
     * Get the mime-type of the content described by this object.
     * If the source is not able to determine the mime-type by itself
     * this can be <code>null</code>.
     * 
     * @return the source's mime-type or <code>null</code>.
     */
    String getMimeType();

    /**
     * Get the content length of this source's content or -1 if the length is
     * unknown.
     * 
     * @return the source's content length or -1.
     */
    long getContentLength();

    /**
     * Get the last modification date of this source. The date is
     * measured in milliseconds since the epoch (00:00:00 GMT, January 1, 1970),
     * and is <code>0</code> if it's unknown.
     * 
     * @return the last modification date or <code>0</code>.
     */
    long getLastModified();
    
}
