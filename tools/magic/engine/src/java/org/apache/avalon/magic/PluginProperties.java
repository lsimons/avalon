/*
Copyright 2004 The Apache Software Foundation
Licensed  under the  Apache License,  Version 2.0  (the "License");
you may not use  this file  except in  compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed  under the  License is distributed on an "AS IS" BASIS,
WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
implied.

See the License for the specific language governing permissions and
limitations under the License.
*/

package org.apache.avalon.magic;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class PluginProperties extends Properties
{
    private PropertyResolver m_Resolver;
    
    PluginProperties()
    {
        m_Resolver = new DefaultResolver();
    }
    
    PluginProperties( Properties content )
    {
        super();
        Iterator list = content.entrySet().iterator();
        while( list.hasNext() )
        {
            Map.Entry entry = (Map.Entry) list.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            put( key, value );
        }
    }
    
    public String getProperty( String name, PropertyResolver resolver )
    {
        name = name.trim();
        String value = super.getProperty( name );
        if( value == null )
            return null;
        value = value.trim();
        return resolver.resolve( this, value );
    }
    
    public String getProperty( String name )
    {
        return getProperty( name, m_Resolver );
    }
    
}
