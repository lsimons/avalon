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

package org.apache.avalon.composition.model.impl;

import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.ProtectionDomain;

import java.util.HashMap;

import org.apache.avalon.composition.model.ClassLoaderModel;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.DeploymentModel;

public class CodeSecurityPolicy extends Policy 
{
    private HashMap m_permissions;
    private ContainmentModel m_model;
    
    public CodeSecurityPolicy( ContainmentModel model )
    {
        m_model = model;
        refresh();        
    }
    
    public PermissionCollection getPermissions( CodeSource cs )
    {
        return (PermissionCollection) m_permissions.get( cs );
    }
    
    public void refresh()
    {
        m_permissions = new HashMap();
        refresh( m_model );
    }
    
    public void refresh( ContainmentModel model )
    {
        ClassLoaderModel clModel = model.getClassLoaderModel();
        updatePermissions( clModel );
        DeploymentModel[] m = model.getModels();
        for( int i=0 ; i < m.length ; i++ )
        {
            if( m[i] instanceof ContainmentModel )
            {
                ContainmentModel child = (ContainmentModel) m[i];
                refresh( child );
            }
        }
    }
    
    private void updatePermissions( ClassLoaderModel clModel )
    {
        ProtectionDomain[] pd = clModel.getProtectionDomains();
        for( int i=0 ; i < pd.length ; i++ )
        {
            CodeSource cs = pd[i].getCodeSource();
            PermissionCollection perm = pd[i].getPermissions();
            m_permissions.put( cs, perm );
        }
    }
}
