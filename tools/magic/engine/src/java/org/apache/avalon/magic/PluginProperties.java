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

import java.io.IOException;
import java.io.InputStream;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PluginProperties
{
    private Properties m_Properties;
    
    PluginProperties()
    {
        m_Properties = new Properties();
    }
    
    PluginProperties( PluginProperties content )
    {
        this();
        putAll( content );
    }
    
    void putAll( PluginProperties content )
    {    
        Iterator list = content.entrySet().iterator();
        while( list.hasNext() )
        {
            Map.Entry entry = (Map.Entry) list.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            m_Properties.put( key, value );
        }
    }
    
    public String getProperty( String name )
    {
        String value = m_Properties.getProperty( name ).trim();
        if( value.indexOf( "${" ) >= 0 )
            throw new IllegalArgumentException( "The value of '" + name + "' contains a variable, and not supported to resolve with this method:" + value );
        return value;
    }
    
    public String getProperty( String name, PropertyResolver resolver )
    {
        name = name.trim();
        String value = m_Properties.getProperty( name );
        if( value == null )
            return null;
        value = value.trim();
        return resolver.resolve( this, value );
    }
    
    public String resolve( String data, PropertyResolver resolver  )
    {
        return resolver.resolve( this, data ).trim();
    }

    public void setProperty( String name, String value )
    {
        m_Properties.setProperty( name, value );
    }
    
    void load( InputStream in )
        throws IOException
    {
        m_Properties.load( in );
    }
    
    Set entrySet()
    {
        return m_Properties.entrySet();
    }

    Set keySet()
    {
        return m_Properties.keySet();
    }
}
