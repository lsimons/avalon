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
package org.apache.avalon.fortress.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

/**
 * This class handles looking up service providers on the class path.
 * It implements the system described in:
 *
 * <a href="http://java.sun.com/j2se/1.3/docs/guide/jar/jar.html#Service Provider">
 * File Specification Under Service Provider</a>.  Note that this interface is
 * very similar to the one they describe whiehc seems to be missing in the JDK.
 *
 * This class adapted from <code>org.apache.batik.util.Service</code>
 *
 * @author <a href="bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version 1.0
 */
public final class Service
{
    private static final String SERVICES = "META-INF/services/";
    private static final HashMap providers = new HashMap();

    /**
     * Private constructor to keep from instantiating this class
     */
    private Service()
    {
    }

    /**
     * Get all the providers for the specified services.
     *
     * @param klass  the interface <code>Class</code>
     *
     * @return an <code>Iterator</code> for the providers.
     */
    public static synchronized Iterator providers( final Class klass )
    {
        final String serviceFile = SERVICES + klass.getName();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        if ( null == loader )
        {
            loader = klass.getClassLoader();
        }

        Set providerSet = (Set) providers.get( serviceFile );

        if ( null == providerSet )
        {
            providerSet = new HashSet();
            Enumeration enum = null;
            boolean errorOccurred = false;

            providers.put( serviceFile, providerSet );

            try
            {
                enum = loader.getResources( serviceFile );
            }
            catch ( IOException ioe )
            {
                errorOccurred = true;
            }

            if ( !errorOccurred )
            {
                while ( enum.hasMoreElements() )
                {
                    try
                    {
                        final URL url = (URL) enum.nextElement();
                        final InputStream is = url.openStream();
                        final BufferedReader reader = new BufferedReader(
                            new InputStreamReader( is,
                                "UTF-8" ) );

                        String line = reader.readLine();
                        while ( null != line )
                        {
                            try
                            {
                                final int comment = line.indexOf( '#' );

                                if ( comment > -1 )
                                {
                                    line = line.substring( 0, comment );
                                }

                                line.trim();

                                if ( line.length() > 0 )
                                {
                                    // We just want the types, not the instances
                                    providerSet.add( loader.loadClass( line ) );
                                }
                            }
                            catch ( Exception e )
                            {
                                // try the next line
                            }

                            line = reader.readLine();
                        }
                    }
                    catch ( Exception e )
                    {
                        // try the next file
                    }
                }
            }
        }

        return providerSet.iterator();
    }
}
