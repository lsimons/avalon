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
 * The <code>Mode</code> class declares the EXPLICIT, PACKAGED or IMPLICIT mode of creation of a profile.
 *
 * @see DeploymentProfile
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/01/13 11:41:24 $
 */
public class Mode implements Serializable
{
    /**
     * Constant indicating that the profile was implicitly created.
     */
    public static final int IMPLICIT_VALUE = 0;

    /**
     * Constant indicating that the profile was created based on a profile packaged with the type.
     */
    public static final int PACKAGED_VALUE = 1;

    /**
     * Constant indicating that the profile was explicitly declared under an assembly directive.
     */
    public static final int EXPLICIT_VALUE = 2;

    /**
     * Constant indicating that the profile was implicitly created.
     */
    public static final Mode IMPLICIT = new Mode( IMPLICIT_VALUE );

    /**
     * Constant indicating that the profile was created based on a profile packaged with the type.
     */
    public static final Mode PACKAGED = new Mode( PACKAGED_VALUE );

    /**
     * Constant indicating that the profile was explicitly declared under an assembly directive.
     */
    public static final Mode EXPLICIT = new Mode( EXPLICIT_VALUE );

   /**
    * Returns a string representation of a mode value.
    * @param mode the mode value
    * @return the string representation
    */
    public static String modeToString( int mode )
    {
        if( mode == IMPLICIT_VALUE )
        {
            return "IMPLICIT";
        }
        else if( mode == PACKAGED_VALUE )
        {
            return "PACKAGED";
        }
        else if( mode == EXPLICIT_VALUE )
        {
            return "EXPLICIT";
        }
        else
        {
            return "?";
        }
    }

    /**
     * The creation mode.
     */
    private final int m_mode;

   /**
    * Creation of a new mode value.
    * @param mode the int value of the mode.
    */
    public Mode( int mode )
    {
        m_mode = mode;
    }

    /**
     * The supplied argument.
     * @return the mode value
     */
    public int getValue()
    {
        return m_mode;
    }

   /**
    * Return a string representatio of the mode.
    * @return String the string value
    */
    public String toString()
    {
        return modeToString( getValue() );
    }

   /**
    * Compare a supplied object for equality.
    * @param object the other object
    * @return TRUE if the supplied mode is equivalent to this mode.
    */
    public boolean equals( Object object )
    {
        if( object instanceof Mode )
        {
            return ((Mode)object).getValue() == getValue();
        }
        return false;
    }
}
