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

package org.apache.avalon.merlin.env;

/**
 * A simple wrapper exception around exceptions that could occur while accessing
 * environment parameters.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.2 $
 */
public class EnvAccessException extends Exception
{
    /** the environment variable name if available */
    public final String m_variable ;
    
    public final Throwable m_cause;
    
    /**
     * Creates an exception denoting a failure while attempting to access an 
     * environment variable within an operating system and shell specific 
     * environment that is caused by another exception.
     * 
     * @param a_variable the variable whose value was to be accessed
     * @param a_message the reason for the access failure 
     * @param a_cause the underlying exception that caused the failure
     */
    EnvAccessException( final String a_variable, final Throwable a_cause )
    {
        super() ;
        
        m_variable = a_variable ;
        m_cause = a_cause;
    }


    /**
     * Creates an exception denoting a failure while attempting to access an 
     * environment variable within an operating system and shell specific 
     * environment.
     * 
     * @param a_variable the variable whose value was to be accessed
     * @param a_message the reason for the access failure 
     */
    EnvAccessException( final String a_variable, final String a_message )
    {
        super( a_message ) ;
        
        m_variable = a_variable ;
        m_cause = null;
    }


    /**
     * Gets the variable that was to be accessed.
     * 
     * @return the value of the variable 
     */
    public String getVariable()
    {
        return m_variable ;
    }

    /**
     * Return the causal exception.
     * 
     * @return the exception that caused this exception (possibly null)
     */
    public Throwable getCause()
    {
        return m_cause;
    }

    /**
     * Prepends variable name to the base message.
     * 
     * @see java.lang.Throwable#getMessage()
     */
    public String getMessage()
    {
        String l_base = super.getMessage() ;
        
        if ( null == l_base )
        {    
            return "Failed to access " + m_variable + " environment variable" ;
        }
        
        return "Failed to access " + m_variable 
            + " environment variable - " + l_base ;
    }
}



