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
import java.util.Collection;
import java.util.Iterator;

/**
 * Description of a source. This interface provides a simple interface
 * for accessing a source of data.
 *
 * When the <code>Source</code> object is no longer needed
 * it must be released using the resolver. This is very similar like
 * looking up components from a <code>ComponentLocator</code>.
 * In fact a source object can implement most lifecycle interfaces
 * like Composable, Initializable, Disposable etc.
 *
 * Thee data content can be constant or change over time.
 * Using the getInputStream() method you get always the upto-date content.
 * When you're done with using the source object, you have to release it.
 * If you want to track changes of the source object, this interface
 * offers you some support for it by providing a SourceValidity object.
 *
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
 *
 * If it is still the same Source than the first time, you
 * have to call discardValidity() in order to discard the stored validity
 * in the Source object. If it is a new Source object,
 * calling discardValidity() should do no harm.
 * After that an upto-date validity object can retrieved by calling
 * getValidity(). This can be used to test if the content is still valid
 * as discribed in the source validity documentation.
 * If the content is still valid, the cache knows what to do, if not,
 * the new content can be get using getInputStream().
 * So either after a call to getValidity() or the getInputStream the
 * validity object must be the same until discardValidity is called!
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.11 $ $Date: 2003/01/08 21:33:51 $
 */
public interface Source
{
    /**
     * Return an <code>InputStream</code> object to read from the source.
     * This is the data at the point of invocation of this method,
     * so if this is Modifiable, you might get different content
     * from two different invocations.
     */
    InputStream getInputStream()
        throws IOException, SourceException;

    /**
     * Return the unique identifier for this source
     */
    String getSystemId();

    /**
     * Return the protocol identifier.
     */
    String getProtocol();
    
    /**
     *  Get the Validity object. This can either wrap the last modification
     *  date or the expires information or...
     *  If it is currently not possible to calculate such an information
     *  <code>null</code> is returned.
     */
    SourceValidity getValidity();

    /**
     * Refresh the content of this object after the underlying data
     * content has changed.
     */
    void discardValidity();

    /**
     * The mime-type of the content described by this object.
     * If the source is not able to determine the mime-type by itself
     * this can be <code>null</code>.
     */
    String getMimeType();

    /**
     * Return the content length of the content or -1 if the length is
     * unknown
     */
    long getContentLength();

    /**
     * Get the last modification date.
     * @return The last modification in milliseconds since January 1, 1970 GMT
     *         or 0 if it is unknown
     */
    long getLastModified();

    /**
     * Get the value of a parameter.
     * Using this it is possible to get custom information provided by the
     * source implementation, like an expires date, HTTP headers etc.
     */
    String getParameter( String name );

    /**
     * Get the value of a parameter.
     * Using this it is possible to get custom information provided by the
     * source implementation, like an expires date, HTTP headers etc.
     */
    long getParameterAsLong( String name );

    /**
     * Get parameter names
     * Using this it is possible to get custom information provided by the
     * source implementation, like an expires date, HTTP headers etc.
     */
    Iterator getParameterNames();
    
    /**
     * Does this source point to a directory?
     */
    boolean isDirectory();
    
    /**
     * Return the URIs of the children
     * The returned URIs are relative to the URI of the parent
     * (this object)
     */
    Collection getChildrenLocations();
}
