/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.source;

import java.io.IOException;
import java.io.InputStream;
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
 * @version CVS $Revision: 1.7 $ $Date: 2002/07/06 03:55:06 $
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
     * Return the unique identifer for this source
     */
    String getSystemId();

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
}
