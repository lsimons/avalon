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
package org.apache.excalibur.util;

/**
 * A set of utility operations that provide necessary information about the
 * architecture of the machine that the system is running on.  The values
 * provided are automatically determined at JVM startup.  The SystemUtils uses
 * a plugin architecture so that it can be extended for more than just Linux/
 * Windows support.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.4 $ $Date: 2002/10/02 01:46:58 $
 */
public final class SystemUtil
{
    private static final int m_processors;
    private static final String m_cpuInfo;
    private static final String m_architecture;
    private static final String m_osName;
    private static final String m_osVersion;

    static
    {
        m_architecture = System.getProperty( "os.arch" );
        m_osName = System.getProperty( "os.name" );
        m_osVersion = System.getProperty( "os.version" );
        int procs = 0;
        String info = "";

        try
        {
            String name = "org.apache.excalibur.util.system." +
                stripWhitespace( m_osName );
            Class klass = Class.forName( name );
            CPUParser parser = (CPUParser)klass.newInstance();

            procs = parser.numProcessors();
            info = parser.cpuInfo();
        }
        catch( Exception e )
        {
            String proc = System.getProperty( "os.arch.cpus", "1" );
            info = System.getProperty(
                "os.arch.info",
                m_architecture +
                " Family n, Model n, Stepping n, Undeterminable"
            );

            procs = Integer.parseInt( proc );
        }

        m_processors = procs;
        m_cpuInfo = info;
    }

    /**
     * Utility method to strip whitespace from specified name.
     *
     * @param mosname the name
     * @return the whitespace stripped version
     */
    private static String stripWhitespace( String mosname )
    {
        final StringBuffer sb = new StringBuffer();

        final int size = mosname.length();
        for( int i = 0; i < size; i++ )
        {
            final char ch = mosname.charAt( i );
            if( ch != '\t' && ch != '\r' &&
                ch != '\n' && ch != '\b' )
            {
                sb.append( ch );
            }
        }

        return sb.toString();
    }

    /** keep utility from being instantiated */
    private SystemUtil()
    {
    }

    /**
     * Return the number of processors available on this machine.  This is useful
     * in classes like Thread/Processor thread pool models.
     */
    public static final int numProcessors()
    {
        return m_processors;
    }

    public static final String cpuInfo()
    {
        return m_cpuInfo;
    }

    /**
     * Return the architecture name
     */
    public static final String architecture()
    {
        return m_architecture;
    }

    /**
     * Return the Operating System name
     */
    public static final String operatingSystem()
    {
        return m_osName;
    }

    /**
     * Return the Operating System version
     */
    public static final String osVersion()
    {
        return m_osVersion;
    }
}

