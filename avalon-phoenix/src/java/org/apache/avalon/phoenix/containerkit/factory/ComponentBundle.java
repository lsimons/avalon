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

import java.io.InputStream;
import org.apache.avalon.phoenix.framework.info.ComponentInfo;

/**
 * The ComponentBundle gives access to the sum total of all the
 * metadata and resources about a component. This includes all
 * the resources associated with a particular component and the
 * associated {@link ComponentInfo}.
 *
 * <p>Additional resources that may be associated with a component
 * include but are not limited to;</p>
 *
 * <ul>
 *   <li>Resource property files for i18n of {@link ComponentInfo}</li>
 *   <li>XML schema or DTD that is used when validating a components
 *       configuration, such as in Phoenix.</li>
 *   <li>Descriptor used to define management interface of
 *       component.</li>
 *   <li>Prototype used to define a component profile.</li>
 * </ul>
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.5 $ $Date: 2003/04/06 03:17:59 $
 */
public interface ComponentBundle
{
    /**
     * Return the {@link ComponentInfo} that describes the
     * component.
     *
     * @return the {@link ComponentInfo} that describes the component.
     */
    ComponentInfo getComponentInfo();

    /**
     * Return an input stream for a resource associated with
     * component. The resource name can be relative or absolute.
     * Absolute names being with a '/' character.
     *
     * <p>When the component is implemented via a Java class (as
     * opposed to a remote SOAP/RMI/JMX/other service), the resources
     * are loaded from the same <code>ClassLoader</code> as
     * implementation. The resources are loaded relative to the
     * implementaion classes package and are often named after the
     * classname of the component.</p>
     *
     * <p>For example, a component <code>com.biz.Foo</code> may have
     * resources such as <code>com/biz/Foo-schema.xsd</code>,
     * <code>com/biz/Foo.mxinfo</code> or
     * <code>com/biz/Foo-profile.xml</code>.</p>
     *
     * @return the input stream for associated resource, or null
     *         if no such resource
     */
    InputStream getResourceAsStream( String resource );
}