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


import java.util.Collection;

/**
 * A traversable source is a source that can have children and
 * a parent, like a file system.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @author <a href="mailto:sylvain@apache.org">Sylvain Wallez</a>
 * @version CVS $Revision: 1.2 $ $Date: 2003/11/22 11:30:40 $
 */
public interface TraversableSource extends Source {

    /**
     * Is this source a collection, i.e. it possibly has children ?
     * For a filesystem-based implementation, this would typically mean that
     * this source represents a directory and not a file.
     * 
     * @return true if the source exists and is traversable.
     */
    boolean isCollection();
    
    /**
     * Get the children of this source if this source is traversable.
     * <p>
     * <em>Note:</em> only those sources actually fetched from the
     * collection need to be released using the {@link SourceResolver}.
     * 
     * @see #isCollection()
     * @return a collection of {@link Source}s (actually most probably <code>TraversableSource</code>s).
     * @throws SourceException this source is not traversable, or if some problem occurs.
     */
    Collection getChildren() throws SourceException;
    
    /**
     * Get a child of this source, given its name. Note that the returned source
     * may not actually physically exist, and that this must be checked using
     * {@link Source#exists()}.
     * 
     * @param name the child name.
     * @return the child source.
     * @throws SourceException if this source is not traversable or if some other
     *         error occurs.
     */
    Source getChild(String name) throws SourceException;
    
    /**
     * Return the name of this source relative to its parent.
     *
     * @return the name
     */
    String getName();
    
    /**
     * Get the parent of this source as a {@link Source} object.
     * 
     * @return the parent source, or <code>null</code> if this source has no parent.
     * @throws SourceException if some problem occurs.
     */
    Source getParent() throws SourceException;
}
