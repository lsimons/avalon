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
package org.apache.excalibur.xfc;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.NullLogger;

/**
 * Class for performing conversions between input and output
 * {@link Module} implementations.
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Id: Converter.java,v 1.2 2002/11/12 19:55:26 donaldp Exp $
 */
public class Converter extends AbstractLogEnabled
{
    // internals
    private String m_inputCtx;
    private String m_outputCtx;

    private Module m_input;
    private Module m_output;

    /**
     * Creates a new <code>Converter</code> instance.
     *
     * @param logger a <code>Logger</code> instance
     */
    public Converter( final Logger logger )
    {
        enableLogging( logger == null ? new NullLogger() : logger );
    }

    /**
     * Sets the input {@link Module}.
     *
     * @param module a <code>Class</code> instance
     * @exception Exception if an error occurs
     */
    public void setInputModule( final Class module )
        throws Exception
    {
        m_input = (Module)module.newInstance();
        m_input.enableLogging( getLogger().getChildLogger( module.getName() ) );
    }

    /**
     * Sets the output {@link Module}.
     *
     * @param module a <code>Class</code> instance
     * @exception Exception if an error occurs
     */
    public void setOutputModule( final Class module )
        throws Exception
    {
        m_output = (Module)module.newInstance();
        m_output.enableLogging( getLogger().getChildLogger( module.getName() ) );
    }

    /**
     * Sets the input {@link Module} Context.
     *
     * @param context a <code>String</code> value
     */
    public void setInputModuleContext( final String context )
    {
        m_inputCtx = context;
    }

    /**
     * Sets the output {@link Module} Context.
     *
     * @param context a <code>String</code> value
     */
    public void setOutputModuleContext( final String context )
    {
        m_outputCtx = context;
    }

    /**
     * Performs conversion.
     *
     * @exception Exception if an error occurs
     */
    public void convert()
        throws Exception
    {
        m_output.serialize( m_input.generate( m_inputCtx ), m_outputCtx );
    }
}
