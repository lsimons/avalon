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

package org.apache.avalon.phoenix.containerkit.kernel;

import org.apache.avalon.phoenix.containerkit.profile.ComponentProfile;

/**
 * This is the structure that components are contained within when
 * loaded into a container.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2003/03/22 12:07:11 $
 */
public class ComponentEntry
{
    /**
     * The {@link ComponentProfile} that describes
     * this component.
     */
    private final ComponentProfile m_profile;
    /**
     * The instance of this component.
     */
    private Object m_object;

    /**
     * Creation of a new <code>ComponentEntry</code> instance.
     *
     * @param profile the {@link ComponentProfile} instance defining the component.
     */
    public ComponentEntry( final ComponentProfile profile )
    {
        if( null == profile )
        {
            throw new NullPointerException( "profile" );
        }
        m_profile = profile;
    }

    /**
     * Returns the {@link ComponentProfile} for this component.
     *
     * @return the {@link ComponentProfile} for this component.
     */
    public ComponentProfile getProfile()
    {
        return m_profile;
    }

    /**
     * Returns the the object associated with this entry.
     * @return the entry object
     */
    public Object getObject()
    {
        return m_object;
    }

    /**
     * Set the object assoaiated to this entry.
     * @param object the object to associate with the entry
     */
    public void setObject( final Object object )
    {
        m_object = object;
    }

    /**
     * Returns TRUE is the object for this entry has been set.
     * @return the active status of this entry
     */
    public boolean isActive()
    {
        return ( null != getObject() );
    }
}
