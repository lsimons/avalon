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

package org.apache.avalon.meta.info.builder;

import java.io.InputStream;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.meta.info.Service;

/**
 * A ServiceBuilder is responsible for building {@link Service}
 * objects from Configuration objects.
 *
 * <p><b>UML</b></p>
 * <p><image src="doc-files/ServiceBuilder.gif" border="0"/></p>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 08:15:24 $
 */
public final class ServiceBuilder
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( ServiceBuilder.class );

    private final ServiceCreator m_xmlServiceCreator = createXMLServiceCreator();
    private final ServiceCreator m_serialServiceCreator = new SerializedServiceCreator();

    /**
     * Create a {@link Service} object for specified Class.
     *
     * @param clazz The class of Component
     * @return the created Service
     * @throws Exception if an error occurs
     */
    public Service build( final Class clazz )
        throws Exception
    {
        return build( clazz.getName(), clazz.getClassLoader() );
    }

    /**
     * Create a {@link Service} object for specified
     * classname, in specified ClassLoader.
     *
     * @param classname The classname of Component
     * @param classLoader the ClassLoader to load info from
     * @return the created Service
     * @throws Exception if an error occurs
     */
    public Service build( final String classname,
                                    final ClassLoader classLoader )
        throws Exception
    {
        final Service info = buildFromSerDescriptor( classname, classLoader );
        if( null != info )
        {
            return info;
        }
        else
        {
            return buildFromXMLDescriptor( classname, classLoader );
        }
    }

    /**
     * Build Service from the XML descriptor format.
     *
     * @param classname The classname of Component
     * @param classLoader the ClassLoader to load info from
     * @return the created Service
     * @throws Exception if an error occurs
     */
    private Service buildFromSerDescriptor( final String classname,
                                                      final ClassLoader classLoader )
        throws Exception
    {
        final String xinfo =
            classname.replace( '.', '/' ) + ".sinfo";
        final InputStream inputStream =
            classLoader.getResourceAsStream( xinfo );
        if( null == inputStream )
        {
            return null;
        }

        return m_serialServiceCreator.createService( classname, inputStream );
    }

    /**
     * Build Service from the XML descriptor format.
     *
     * @param classname The classname of Component
     * @param classLoader the ClassLoader to load info from
     * @return the created Service
     * @throws Exception if an error occurs
     */
    private Service buildFromXMLDescriptor( final String classname,
                                                      final ClassLoader classLoader )
        throws Exception
    {
        //
        // get the input stream for the .xservice resource
        //

        final String xservice =
            classname.replace( '.', '/' ) + ".xservice";
        final InputStream inputStream =
            classLoader.getResourceAsStream( xservice );

        if( null == inputStream )
        {
            final String message =
                REZ.getString( "builder.missing-info.error",
                               classname );
            throw new Exception( message );
        }

        //
        // build the type
        //

        final ServiceCreator xmlServiceCreator = getXMLServiceCreator( classname );
        return xmlServiceCreator.createService( classname, inputStream );
    }

    /**
     * Utility to get xml info builder, else throw
     * an exception if missing descriptor.
     *
     * @return the ServiceCreator
     */
    private ServiceCreator getXMLServiceCreator( final String classname )
        throws Exception
    {
        if( null != m_xmlServiceCreator )
        {
            return m_xmlServiceCreator;
        }
        else
        {
            final String message =
                REZ.getString( "builder.missing-xml-creator.error",
                               classname );
            throw new Exception( message );
        }
    }

    /**
     * Utility to get XMLServiceCreator if XML files are on
     * ClassPath.
     *
     * @return the XML {@link ServiceCreator}
     */
    private static ServiceCreator createXMLServiceCreator()
    {
        ServiceCreator xmlServiceCreator = null;
        try
        {
            xmlServiceCreator = new XMLServiceCreator();
        }
        catch( final Exception e )
        {
            //Ignore it if ClassNot found due to no
            //XML Classes on classpath
        }
        return xmlServiceCreator;
    }
}
