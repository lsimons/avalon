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

/**
 * This class is used to provide explicit information to assembler
 * and administrator about the Component. It includes information
 * such as;
 *
 * <ul>
 *   <li>a symbolic name</li>
 *   <li>classname</li>
 * </ul>
 *
 * <p>The ComponentDescriptor also includes an arbitrary set
 * of Attribute about the component. Usually these are container
 * specific Attributes that store information relevent to a particular
 * requirement. The Attribute names should be stored with keys based
 * on package name of container. ie You could use the following</p>
 *
 * <pre>
 * public class CocoonKeys
 * {
 *     private final static String PACKAGE =
 *         CocoonKeys.class.getPackage().getName();
 *
 *     //Is object Multi-thread safe, sharable between components
 *     public final static String LIFESTYLE = PACKAGE + ".Lifestyle";
 *
 *     //Is object scoped per-request, per-session, per-page etc
 *     public final static String SCOPE = PACKAGE + ".Scope";
 * }
 *
 * ...
 *
 * ComponentDescriptor cd = ...;
 * Attribute lifestyle = cd.getAttribute( LIFESTYLE );
 * Attribute scope = cd.getAttribute( SCOPE );
 * </pre>
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003/03/22 12:07:13 $
 */
public final class ComponentDescriptor
    extends FeatureDescriptor
{
    /**
     * The implementation key for component (usually classname).
     */
    private final String m_implementationKey;

    public ComponentDescriptor( final String implementationKey,
                                final Attribute[] attribute )
    {
        super( attribute );
        if( null == implementationKey )
        {
            throw new NullPointerException( "implementationKey" );
        }

        m_implementationKey = implementationKey;
    }

    /**
     * Return the implementation key for component (usually classname).
     *
     * @return the implementation key for component (usually classname).
     */
    public String getImplementationKey()
    {
        return m_implementationKey;
    }
}