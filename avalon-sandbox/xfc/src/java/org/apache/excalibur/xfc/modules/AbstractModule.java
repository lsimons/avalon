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

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

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
package org.apache.excalibur.xfc.modules;

import java.util.Properties;

import org.apache.avalon.framework.Version;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

import org.apache.excalibur.xfc.Module;
import org.apache.excalibur.xfc.model.Model;

/**
 * Abstract {@link Module} class which provides common operations/constants
 * to prospective subclasses.
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Id: AbstractModule.java,v 1.3 2002/10/14 16:17:50 crafterm Exp $
 */
public abstract class AbstractModule
    extends AbstractLogEnabled
    implements Module
{
    // normalized component handler names
    protected static final String     TRANSIENT = "transient";
    protected static final String     THREAD    = "thread";
    protected static final String     POOLED    = "pooled";
    protected static final String     SINGLETON = "singleton";

    protected static final char       CONTEXT_SEPARATOR = ':';

    protected static final String     COMPONENT = "component";

    protected final DefaultConfigurationSerializer m_serializer;
    protected final DefaultConfigurationBuilder m_builder;

    /**
     * Creates a new {@link AbstractModule} instance.
     */
    public AbstractModule()
    {
        m_builder = new DefaultConfigurationBuilder();
        m_serializer = new DefaultConfigurationSerializer();

        // enable pretty printing of output
        m_serializer.setIndent( true );
    }

    /**
     * Abstract method for generating a {@link Model} from an
     * input context
     *
     * @param context a <code>String</code> value
     * @return a {@link Model} value
     * @exception Exception if an error occurs
     */
    public abstract Model generate( final String context )
        throws Exception;

    /**
     * Abstract method for serializing a given {@link Model} to
     * an output context.
     *
     * @param model a {@link Model} value
     * @param context a <code>String</code> value
     * @exception Exception if an error occurs
     */
    public abstract void serialize( final Model model, final String context )
        throws Exception;
}
