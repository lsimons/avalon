/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

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
package org.apache.avalon.phoenix.containerkit.creator;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * This defines the interface via which containers are created.
 * The notion of container is suitable vague to allow arbitary
 * objects to be created as containers - not necessarily related
 * to Avalon containers in anyway.
 *
 * @author Peter Donald
 * @version $Revision: 1.2 $ $Date: 2003/12/05 15:14:36 $
 */
public final class ContainerManager
{
    /**
     * The key used to specify the name of the Container factory.
     */
    public static final String INITIAL_CONTAINER_FACTORY =
        ContainerFactory.class.getName();

    /**
     * Cache of created container->factory map.
     */
    private static final Map c_containers = new WeakHashMap();

    /**
     * Create an instance of container using specified input data.
     *
     * @param data the initial config data for container
     * @return the newly created container
     * @throws Exception if unable to create a container
     */
    public static Object create( Map data )
        throws Exception
    {
        final String classname = (String)data.remove( INITIAL_CONTAINER_FACTORY );
        if( null == classname )
        {
            final String message = "No INITIAL_CONTAINER_FACTORY specified.";
            throw new Exception( message );
        }
        final Class clazz = Class.forName( classname );
        final ContainerFactory factory = (ContainerFactory)clazz.newInstance();
        final Object container = factory.create( data );
        c_containers.put( container, factory );
        return container;
    }

    /**
     * Destroy a container created with this factory.
     *
     * @param container the container
     * @throws Exception if unable to destroy container
     */
    public static void destroy( final Object container )
        throws Exception
    {
        final ContainerFactory factory = (ContainerFactory)c_containers.remove( container );
        if( null == factory )
        {
            final String message = "Container was not created by " +
                "ContainerManager or has already been destroyed.";
            throw new Exception( message );
        }
        factory.destroy( container );
    }
}
