/* 
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avalon.repository.impl;

import java.util.jar.Manifest ;

import org.apache.avalon.repository.provider.BlockManifest;

/**
 * An extended manifest that provides a set of convinience operations
 * to access block related attributes.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/01/24 23:20:04 $
 */
public final class DefaultBlockManifest implements BlockManifest
{

   /**
    * Group identifier.
    */
    private final String m_group;

   /**
    * Name.
    */
    private final String m_name;

   /**
    * Version identifier.
    */
    private final String m_version;

   /**
    * Creation of a new block manifest
    * @param manifest the jar manifest
    */
    public DefaultBlockManifest( Manifest manifest ) throws NullPointerException 
    {
        m_group = (String) manifest.getMainAttributes().getValue( BLOCK_GROUP_KEY );
        m_name = (String) manifest.getMainAttributes().getValue( BLOCK_NAME_KEY );
        m_version = (String) manifest.getMainAttributes().getValue( BLOCK_VERSION_KEY );
    }

    public String getBlockGroup()
    {
        return m_group;
    }

    public String getBlockName()
    {
        return m_name;
    }

    public String getBlockVersion()
    {
        return m_version;
    }
}
