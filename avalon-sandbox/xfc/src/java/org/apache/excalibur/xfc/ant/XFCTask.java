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
package org.apache.excalibur.xfc.ant;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.excalibur.xfc.Converter;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * ANT Task based XFC entry point.
 *
 * <p>
 *  <pre>
 *   &lt;xfc&gt;
 *    &lt;input module="ecm" context="ecm.roles:ecm.xconf"/&gt;
 *    &lt;output module="fortress" context="fortress.roles:fortress.xconf"/&gt;
 *   &lt;/xfc&gt;
 *  </pre>
 * </p>
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Id: XFCTask.java,v 1.4 2002/11/12 20:07:50 donaldp Exp $
 */
public final class XFCTask
    extends Task
{
    private Logger m_logger = new ConsoleLogger( ConsoleLogger.LEVEL_WARN );
    private ModuleDefinition m_input;
    private ModuleDefinition m_output;

    /**
     * Method to set the input {@link ModuleDefinition} object.
     *
     * @param input a {@link ModuleDefinition} instance
     */
    public void addConfiguredInput( final ModuleDefinition input )
    {
        m_input = input;
    }

    /**
     * Method to set the output {@link ModuleDefinition} object
     *
     * @param output a {@link ModuleDefinition} instance
     */
    public void addConfiguredOutput( final ModuleDefinition output )
    {
        m_output = output;
    }

    /**
     * Executes the task, converts input format to output format
     *
     * @exception BuildException if an error occurs
     */
    public void execute()
        throws BuildException
    {
        validate();

        try
        {
            // create converter
            final Converter cv = new Converter( getLogger() );

            // set up input and output modules
            cv.setInputModule( getClass( m_input.getModule() ) );
            cv.setInputModuleContext( m_input.getContext() );

            cv.setOutputModule( getClass( m_output.getModule() ) );
            cv.setOutputModuleContext( m_output.getContext() );

            // convert
            cv.convert();

            // all done, good show
        }
        catch( Exception e )
        {
            final String message =
                "Error occured during XFC task conversion";
            throw new BuildException( message, e );
        }
    }

    /**
     * Method to validate that the input and output module definition
     * objects have been set and are valid.
     *
     * @exception BuildException if definitions are invalid
     */
    private void validate()
        throws BuildException
    {
        // check the input task
        if( m_input == null ||
            m_input.getModule() == null ||
            m_input.getContext() == null
        )
        {
            final String message =
                "XFC input task missing input criteria";
            throw new BuildException( message );
        }

        // check the output task
        if( m_output == null ||
            m_output.getModule() == null ||
            m_output.getContext() == null )
        {
            final String message =
                "XFC output task missing output criteria";
            throw new BuildException( message );
        }
    }

    /**
     * Obtain the {@link Class} object for the plugin module
     * specified. This method makes a simple check with some pre-defined
     * plugin's and returns their {@link Class} objects if specified.
     *
     * <p>
     *  If the specified plugin is not known, it's assumed to be a
     *  fully qualified class name of a custom plugin, and is loaded manually.
     * </p>
     *
     * @param clazz class name as a <code>String</code> object
     * @return a <code>Class</code> instance
     * @exception ClassNotFoundException if an error occurs
     */
    private Class getClass( final String clazz )
        throws ClassNotFoundException
    {
        if( "ecm".equalsIgnoreCase( clazz ) )
        {
            return Class.forName( "org.apache.excalibur.xfc.modules.ecm.ECM" );
        }

        if( "fortress".equalsIgnoreCase( clazz ) )
        {
            return Class.forName( "org.apache.excalibur.xfc.modules.fortress.Fortress" );
        }

        if( "merlin".equalsIgnoreCase( clazz ) )
        {
            return Class.forName( "org.apache.excalibur.xfc.modules.merlin.Merlin" );
        }

        // assume custom module
        return Class.forName( clazz );
    }

    /**
     * Helper method to return the current logging instance, LogEnabled style.
     *
     * @return a <code>Logger</code> value
     */
    private Logger getLogger()
    {
        return m_logger;
    }
}
