/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
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
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.avalon.composition.data;

import java.io.Serializable;

/**
 * An inport directive used within a context directive to request a container scoped values.
 *
 * <p><b>XML</b></p>
 * <p>An import statement declares that a context value must be supplied by the container,
 * using the container scoped value of the <code>name</code> attribute, and that the value should be
 * supplied as a context entry keyed under the value of the <code>key</code> attribute.</p>
 * <pre>
 *
 *    <font color="gray">
 *    &lt;--
 *    Declare the import of the value of "urn:avalon:home" as a keyed context
 *    entry using the key "home".
 *    --&gt;</font>
 *
 *  <font color="gray">&lt;context&gt;</font>
 *    <font color="gray">&lt;entry key="home">&gt;</font>
 *      &lt;import key="<font color="darkred">urn:avalon:home</font>"/&gt;
 *    <font color="gray">&lt;/entry&gt;</font>
 *  <font color="gray">&lt;/context&gt;</font>
 * </pre>
 *
 * @see ContextDirective
 * @see EntryDirective
 * @see Parameter
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 09:31:07 $
 */
public class ImportDirective extends EntryDirective
{
    /**
     * The container scoped key.
     */
    private final String m_import;

    /**
     * Creation of a new entry directive.
     * @param key the context entry key
     * @param containerKey the container scoped key value to import
     */
    public ImportDirective( final String key, final String containerKey )
    {
        super( key );
        if( null == containerKey )
        {
            throw new NullPointerException( "containerKey" );
        }
        m_import = containerKey;
    }

    /**
     * Return the container scoped key that defines the object to be imported.
     *
     * @return the contain scoped key
     */
    public String getImportKey()
    {
        return m_import;
    }
}
