package org.apache.merlin.magic;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.TestCase;


/**
 * @author Niclas Hedhman, niclas@hedhman.org
 */
public class PluginContextTestCase extends TestCase
{

    private PluginContext m_Context;
    private File m_PluginDir;
    private File m_SystemDir;
    private File m_ProjectDir;
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        m_ProjectDir = new File( "target/projectdir");
        m_ProjectDir.mkdir();
        
        Properties projectProps = new Properties();
        InputStream in = getClass().getResourceAsStream( "test.properties");
        projectProps.load( in );
        
        m_PluginDir = new File( "target/plugins");
        m_PluginDir.mkdir();
        
        m_SystemDir = new File( "target/system");
        m_SystemDir.mkdir();

        m_Context = new PluginContext( " testcase project ", m_ProjectDir, projectProps,
                "testcase plugin", m_PluginDir, m_SystemDir );
        m_Context.setPluginClassname( "TestCasePlugin");
    }

    public void testGetProjectName()
    {
        assertEquals( "Project Name failed.", "testcase project", m_Context.getProjectName());
    }

    public void testGetProjectDir()
    {
        assertEquals( "Project Dir failed.", m_ProjectDir, m_Context.getProjectDir());
    }

    public void testGetProjectProperties()
    {
    }

    public void testGetPluginName()
    {
        assertEquals( "Plugin Name failed.", "testcase plugin", m_Context.getPluginName());
    }

    public void testGetPluginDir()
    {
        assertEquals( "Plugin Dir failed.", m_PluginDir, m_Context.getPluginDir());
    }

    public void testGetSystemDir()
    {
        assertEquals( "System Dir failed.", m_SystemDir, m_Context.getSystemDir());
    }

    public void testGetPluginClassname()
    {
        assertEquals( "Plugin ClassName failed.", "TestCasePlugin", m_Context.getPluginClassname());
    }

    public void testGetProperty()
    {
        String p1 = "niclas${abc.def}hedhman";
        String value = m_Context.resolve( p1 );
        assertEquals( "Unresolvable failed.", p1, value );

        p1 = "niclas ${a.property } hedhman";
        value = m_Context.resolve( p1 );
        assertEquals( "Single Level resolution failed.", "niclas has the surname of hedhman", value );

        p1 = "${a2.this}";
        value = m_Context.resolve( p1 );
        assertEquals( "Property resolution failed.", "this is", value );

        p1 = "Hey, ${a2.${a1}} ${a2.${a4}} ${a3}";
        value = m_Context.resolve( p1 );
        assertEquals( "Nested resolution failed.", "Hey, this is this is not this is funky", value );

        p1 = "${${${${${${b1}}}}}}";
        value = m_Context.resolve( p1 );
        assertEquals( "Nested resolution failed.", "YEAH!!!!", value );
    }

    public void testGetAntProject()
    {
        assertTrue( "Ant Project failed.", m_Context.getAntProject() != null );
        assertEquals( "Ant Project failed.", "testcase project", m_Context.getAntProject().getName() );
    }
}
