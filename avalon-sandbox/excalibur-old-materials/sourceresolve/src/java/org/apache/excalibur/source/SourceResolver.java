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
import java.net.MalformedURLException;
import java.util.Map;

import org.apache.avalon.framework.component.Component;

/**
 * Base interface for resolving a source by system identifiers.
 * Instead of using the java.net.URL classes which prevent you
 * from adding your own custom protocols in a server environment,
 * you should use this resolver for all URLs.
 *
 * The resolver creates for each source a <code>Source</code>
 * object, which could then be asked for an <code>InputStream</code>
 * etc.
 *
 * When the <code>Source</code> object is no longer needed
 * it must be released using the resolver. This is very similar like
 * looking up components from a <code>ComponentLocator</code>.
 * In fact a source object can implement most lifecycle interfaces
 * like Composable, Initializable, Disposable etc.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.6 $ $Date: 2002/12/15 11:56:48 $
 */

public interface SourceResolver
    extends Component
{
    String ROLE = SourceResolver.class.getName();

    /**
     * Get a <code>Source</code> object.
     * This is a shortcut for <code>resolve(location, null, null)</code>
     * @throws SourceNotFoundException if the source cannot be found
     */
    Source resolveURI( String location )
        throws MalformedURLException, IOException, SourceException;

    /**
     * Get a <code>Source</code> object.
     * @param location - the URI to resolve. If this is relative it is either
     *                   resolved relative to the base parameter (if not null)
     *                   or relative to a base setting of the source resolver
     *                   itself.
     * @param base - a base URI for resolving relative locations. This
     *               is optional and can be <code>null</code>.
     * @param parameters - Additional parameters for the URI. The parameters
     *                     are specific to the used protocol.
     * @throws SourceNotFoundException if the source cannot be found
     */
    Source resolveURI( String location,
                       String base,
                       Map parameters )
        throws MalformedURLException, IOException, SourceException;

    /**
     * Releases a resolved resource
     */
    void release( Source source );
}

