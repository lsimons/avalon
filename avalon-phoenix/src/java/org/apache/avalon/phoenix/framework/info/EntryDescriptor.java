/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

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

package org.apache.avalon.phoenix.framework.info;

import java.io.Serializable;

/**
 * A descriptor that describes a value that must be placed
 * in components Context. It contains information about;
 * <ul>
 *   <li>key: the key that component uses to look up entry</li>
 *   <li>type: the class/interface of the entry</li>
 *   <li>isOptional: true if entry is optional rather than required</li>
 * </ul>
 *
 * <p>See the <a href="../../../../../context.html">Entries</a> document
 * for a list of widely recognized entry values and a recomended
 * naming scheme for other entrys.</p>
 *
 * <p>Also associated with each entry is a set of arbitrary
 * Attributes that can be used to store extra information
 * about entry. See {@link ComponentDescriptor} for example
 * of how to declare the container specific Attributes.</p>
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003/03/22 12:07:13 $
 */
public final class EntryDescriptor
    extends FeatureDescriptor
    implements Serializable
{
    /**
     * Emprty set of entrys.
     */
    public static final EntryDescriptor[] EMPTY_SET = new EntryDescriptor[ 0 ];

    /**
     * The name the component uses to lookup entry.
     */
    private final String m_key;

    /**
     * The class/interface of the Entry.
     */
    private final String m_type;

    /**
     * True if entry is optional, false otherwise.
     */
    private final boolean m_optional;

    /**
     * Construct an Entry.
     */
    public EntryDescriptor( final String key,
                            final String type,
                            final boolean optional,
                            final Attribute[] attribute )
    {
        super( attribute );
        if( null == key )
        {
            throw new NullPointerException( "key" );
        }
        if( null == type )
        {
            throw new NullPointerException( "type" );
        }

        m_key = key;
        m_type = type;
        m_optional = optional;
    }

    /**
     * Return the key that Component uses to lookup entry.
     *
     * @return the key that Component uses to lookup entry.
     */
    public String getKey()
    {
        return m_key;
    }

    /**
     * Return the key type of value that is stored in Context.
     *
     * @return the key type of value that is stored in Context.
     */
    public String getType()
    {
        return m_type;
    }

    /**
     * Return true if entry is optional, false otherwise.
     *
     * @return true if entry is optional, false otherwise.
     */
    public boolean isOptional()
    {
        return m_optional;
    }
}
