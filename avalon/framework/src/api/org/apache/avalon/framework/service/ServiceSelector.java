/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:
 
 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
 
 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.
 
 4. The names "Jakarta", "Apache Avalon", "Avalon Excalibur", "Avalon
    Framework" and "Apache Software Foundation"  must not be used to endorse
    or promote products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.
 
 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.
 
 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Stefano Mazzocchi  <stefano@apache.org>. For more  information on the Apache 
 Software Foundation, please see <http://www.apache.org/>.
 
*/
package org.apache.avalon.framework.service;

/**
 * A <code>ServiceSelector</code> selects <code>Object</code>s based on a
 * supplied policy.  The contract is that all the <code>Object</code>s implement the
 * same role.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version 1.0
 * @see org.apache.avalon.framework.service.Serviceable
 * @see org.apache.avalon.framework.service.ServiceSelector
 *
 */
public interface ServiceSelector
{
    /**
     * Select the <code>Object</code> associated with the given policy.
     * For instance, If the <code>ServiceSelector</code> has a
     * <code>Generator</code> stored and referenced by a URL, I would use the
     * following call:
     *
     * <pre>
     * try
     * {
     *     Generator input;
     *     input = (Generator)selector.select( new URL("foo://demo/url") );
     * }
     * catch (...)
     * {
     *     ...
     * }
     * </pre>
     *
     * @param policy A criteria against which a <code>Object</code> is selected.
     *
     * @return an <code>Object</code> value
     * @throws ComponentException If the requested <code>Object</code> cannot be supplied
     */
    Object select( Object policy )
        throws ServiceException;

    /**
     * Check to see if a <code>Object</code> exists relative to the supplied policy.
     *
     * @param policy a <code>Object</code> containing the selection criteria
     * @return True if the component is available, False if it not.
     */
    boolean isSelectable( Object policy );

    /**
     * Return the <code>Object</code> when you are finished with it.  This
     * allows the <code>ServiceSelector</code> to handle the End-Of-Life Lifecycle
     * events associated with the <code>Object</code>.  Please note, that no
     * Exception should be thrown at this point.  This is to allow easy use of the
     * ServiceSelector system without having to trap Exceptions on a release.
     *
     * @param object The <code>Object</code> we are releasing.
     */
    void release( Object object );


}
