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

package org.apache.avalon.phoenix.containerkit.factory;

/**
 * This interface defines the mechanism via which a
 * component or its associated {@link org.apache.avalon.phoenix.containerkit.factory.ComponentBundle} can
 * be created.
 *
 * <p>Usually the component or ComponentBundle will just be loaded
 * from a particular ClassLoader. However if a developer wanted
 * to dynamically assemble applications they could implement
 * a custom factory that created components via non-standard
 * mechanisms (say by wrapping remote, CORBA, or other style
 * objects).</p>
 *
 * <p>The methods take a <code>implementationKey</code> parameter
 * and usually this represents the class name of the component.
 * However in alternative component systems this may designate
 * objects via different mechanisms.</p>
 *
 * @author Peter Donald
 * @version $Revision: 1.4 $ $Date: 2003/12/05 15:14:37 $
 */
public interface ComponentFactory
{
    String ROLE = ComponentFactory.class.getName();

    /**
     * Create a {@link ComponentBundle} for component
     * specified by implementationKey.
     *
     * @param implementationKey the key indicating type of component (usually classname)
     * @return the ComponentBundle for component
     * @throws Exception if unable to create Info object
     */
    ComponentBundle createBundle( String implementationKey )
        throws Exception;

    /**
     * Create an instance of component with specified
     * implementationKey.
     *
     * @param implementationKey the key indicating type of component (usually classname)
     * @return an instance of component
     * @throws Exception if unable to create component
     */
    Object createComponent( String implementationKey )
        throws Exception;
}
