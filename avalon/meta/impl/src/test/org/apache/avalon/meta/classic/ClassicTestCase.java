

package org.apache.avalon.meta.classic;

import org.apache.avalon.meta.info.Type;
import org.apache.avalon.meta.info.builder.TypeBuilder;
import junit.framework.TestCase;

public class ClassicTestCase extends TestCase
{
    private Type m_type;

    public ClassicTestCase( )
    {
        this( "classic" );
    }

    public ClassicTestCase( String name )
    {
        super( name );
    }

    protected void setUp() throws Exception
    {
        TypeBuilder builder = new TypeBuilder();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Class clazz = loader.loadClass( "org.apache.avalon.meta.classic.Test" );
        m_type = builder.buildType( clazz );
    }

    public void testName() throws Exception
    {
        assertTrue( m_type.getInfo().getName().equals( "test" ) );
    }

    public void testLifestyle() throws Exception
    {
        assertTrue( m_type.getInfo().getLifestyle().equals( "singleton" ) );
    }

    public void testClassName() throws Exception
    {
        assertTrue( 
          m_type.getInfo().getClassname().equals( "org.apache.avalon.meta.classic.Test" ) );
    }
}
