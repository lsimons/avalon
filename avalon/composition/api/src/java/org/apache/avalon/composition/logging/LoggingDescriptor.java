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

package org.apache.avalon.composition.logging;

import org.apache.avalon.composition.data.CategoryDirective;
import org.apache.avalon.composition.data.CategoriesDirective;


/**
 * Description of a top level logging system.
 *
 * <p><b>XML</b></p>
 * <p>A logging element declares the top level defaults for priority and target name, a set of
 * targets to which logging events shall be directed.
 * The logging element declares the application wide default logging priority.
 * A target element enables defintion of a logging file to which log entries will
 * be directed.  The target name attribute is the name referenced by category elements
 * defined within the loggers element. The priority attribute may container one of the values
 * <code>DEBUG</code>, <code>INFO</code>, <code>WARN</code> or <code>ERROR</code>.
 * The target must contain a single file element with the attribute <code>location</code>
 * the corresponds to the name of the logging file.</p>
 *
 * <pre>
 *    <font color="gray"><i>&lt;!--
 *    Definition of a logging system.
 *    --&gt;</i></font>
 *
 *    &lt;logging name="" priority="<font color="darkred">INFO</font>" target="<font color="darkred">kernel</font>"&gt;
 *      &lt;category name="logging" priority="<font color="darkred">WARN</font>"/&gt;
 *      &lt;target name="<font color="darkred">kernel</font>"&gt;
 *        &lt;file location="<font color="darkred">kernel.log</font>" /&gt;
 *      &lt;/target&gt;
 *    &lt;/logging&gt;
 * </pre>
 *
 * @see TargetDescriptor
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 09:31:13 $
 */
public final class LoggingDescriptor extends CategoriesDirective
{

    /**
     * The dependencies keyed by role name.
     */
    private final TargetDescriptor[] m_targets;

    /**
     * Create a LoggingDescriptor instance.
     */
    public LoggingDescriptor()
    {
        this( "", null, null, new CategoryDirective[0], new TargetDescriptor[0] );
    }

    /**
     * Create a LoggingDescriptor instance.
     *
     * @param root the root logger category name
     * @param priority the default logging priority
     * @param target the default logging target
     * @param categories the system categories
     * @param targets the set of logging targets
     */
    public LoggingDescriptor( 
            final String root,
            final String priority,
            final String target,
            final CategoryDirective[] categories,
            final TargetDescriptor[] targets )
    {
        super( root, priority, target, categories );
        if( targets == null )
        {
            m_targets = new TargetDescriptor[0];
        }
        else
        {
            m_targets = targets;
        }
    }

    /**
     * Return the set of logging target descriptors.
     *
     * @return the target descriptors
     */
    public TargetDescriptor[] getTargetDescriptors()
    {
        return m_targets;
    }
}
