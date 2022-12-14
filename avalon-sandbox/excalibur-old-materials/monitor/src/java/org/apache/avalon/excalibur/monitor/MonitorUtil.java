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
package org.apache.avalon.excalibur.monitor;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;

/**
 * A class that contains a few utility methods for working
 * creating resource sets from Avalons configuration objects.
 *
 * @author Peter Donald
 * @version $Revision: 1.7 $ $Date: 2003/12/05 15:15:15 $
 */
class MonitorUtil
{
    private static final Class[] c_constructorParams =
        new Class[]{String.class};

    public static Resource[] configureResources( final Configuration[] resources,
                                                 final Logger logger )
    {
        final ArrayList results = new ArrayList();
        for( int i = 0; i < resources.length; i++ )
        {
            final Configuration initialResource = resources[ i ];
            final String key =
                initialResource.getAttribute( "key", "** Unspecified key **" );
            final String className =
                initialResource.getAttribute( "class", "** Unspecified class **" );

            try
            {
                final Resource resource = createResource( className, key );
                results.add( resource );

                if( logger.isDebugEnabled() )
                {
                    final String message =
                        "Initial Resource: \"" + key + "\" Initialized.";
                    logger.debug( message );
                }
            }
            catch( final Exception e )
            {
                if( logger.isWarnEnabled() )
                {
                    final String message =
                        "Initial Resource: \"" + key +
                        "\" Failed (" + className + ").";
                    logger.warn( message, e );
                }
            }
        }

        return (Resource[])results.toArray( new Resource[ results.size() ] );
    }

    private static Resource createResource( final String className,
                                            final String key )
        throws Exception
    {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final Class clazz = loader.loadClass( className );
        final Constructor initializer =
            clazz.getConstructor( c_constructorParams );
        return (Resource)initializer.newInstance( new Object[]{key} );
    }
}
