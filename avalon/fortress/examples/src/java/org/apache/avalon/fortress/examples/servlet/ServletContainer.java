/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

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
package org.apache.avalon.fortress.examples.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Fortress based servlet example. Presents a simple page to the user
 * displaying the possible languages they can see the text 'hello world'
 * written in.
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Revision: 1.6 $ $Date: 2003/04/11 07:36:20 $
 */
public final class ServletContainer extends org.apache.avalon.fortress.impl.DefaultContainer
{
    public static final String KEY = "hello-world";

    private org.apache.avalon.fortress.examples.components.Translator m_translator;

    /**
     * Initializes this component.
     *
     * @exception java.lang.Exception if an error occurs
     */
    public void initialize()
        throws Exception
    {
        super.initialize();

        m_translator = (org.apache.avalon.fortress.examples.components.Translator)m_serviceManager.lookup( org.apache.avalon.fortress.examples.components.Translator.ROLE );
    }

    /**
     * Simple method to handle requests sent to the container from the
     * controlling servlet. This container simply displays a page containing
     * a list of possible languages the user can see the text 'hello world'
     * written in.
     *
     * @param request a <code>ServletRequest</code> instance
     * @param response a <code>ServletResponse</code> instance
     * @exception ServletException if a servlet error occurs
     * @exception java.io.IOException if an IO error occurs
     */
    public void handleRequest( ServletRequest request, ServletResponse response )
        throws ServletException, IOException
    {
        java.io.PrintWriter out = response.getWriter();
        String selected = request.getParameter( "language" );
        String[] languages = m_translator.getSupportedLanguages( KEY );

        out.println( "<html>" );
        out.println( "<head><title>Hello World!</title></head>" );
        out.println( "<body>" );
        out.println( "<hr>" );

        out.println( "<h1>" );

        if( selected == null )
        {
            out.println( "Please select your language" );
        }
        else
        {
            out.println( m_translator.getTranslation( KEY, selected ) );
        }

        out.println( "</h1>" );
        out.println( "<hr>" );

        out.println( "Available languages:" );

        out.println( "<form action='' name='languagelist'>" );
        out.println( "<select size='1' name='language'>" );

        for( int i = 0; i < languages.length; ++i )
        {
            String lang = languages[ i ];
            out.print( "<option value='" + lang + "'" );

            // preselect chosen language

            if( lang.equals( selected ) )
            {
                out.print( " selected" );
            }

            out.println( ">" + lang + "</option>" );
        }

        out.println( "</select>" );
        out.println( "<input value='OK' type='submit'>" );
        out.println( "</form>" );

        out.println( "</body>" );
        out.println( "</html>" );

        out.close();
    }

    /**
     * Release resources
     */
    public void dispose()
    {
        if( m_translator != null )
            m_serviceManager.release( m_translator );

        super.dispose();
    }
}

