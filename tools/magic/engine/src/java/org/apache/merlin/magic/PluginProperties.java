package org.apache.merlin.magic;

import java.util.Properties;
import java.util.Stack;
import java.util.StringTokenizer;

public class PluginProperties extends Properties
{
    public PluginProperties()
    {
    }
    
    public PluginProperties( Properties content )
    {
        super( content );
    }
    
    public String getProperty( String name )
    {
        name = name.trim();
        String value = super.getProperty( name );
        if( value == null )
            return null;
        value = value.trim();
        return resolve( value );
    }
    
    public String resolve( String value )
    {
        // optimization for common case.
        int pos1 = value.indexOf( "${" );
        if( pos1 < 0 )
            return value;
        
        Stack stack = new Stack();
        StringTokenizer st = new StringTokenizer( value, "${}", true );
        
        while( st.hasMoreTokens() )
        {
            String token = st.nextToken();
            if( token.equals( "}" ) )
            {
                String name = (String) stack.pop();
                String open = (String) stack.pop();
                if( open.equals( "${" ) )
                {
                    String propValue = getProperty( name );
                    if( propValue == null )
                        push( stack, "${" + name + "}" );
                    else
                        push( stack, propValue );
                }
                else
                {
                    push( stack, "${" + name + "}" );
                }
            }
            else
            {
                if( token.equals( "$" ) )
                    stack.push( "$" );
                else
                {
                    push( stack, token );
                }
            }
        }
        String result = "";
        while( stack.size() > 0 )
        {
            result = (String) stack.pop() + result;
        }
        return result;
    }
    
    private void push( Stack stack , String value )
    {
        if( stack.size() > 0 )
        {
            String data = (String) stack.pop();
            if( data.equals( "${" ) )
            {
                stack.push( data );
                stack.push( value );
            }
            else
            {
                stack.push( data + value );
            }
        }
        else
        {
            stack.push( value );
        }
    }
}
