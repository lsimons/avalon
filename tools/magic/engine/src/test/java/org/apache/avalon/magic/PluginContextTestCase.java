package org.apache.avalon.magic;

import java.io.File;
import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.tools.ant.Project;

/**
 * @author Niclas Hedhman, niclas@hedhman.org
 */
public class PluginContextTestCase extends TestCase
{

    private PluginContext m_Context;
    private File m_PluginDir;
    private File m_SystemDir;
    private File m_ProjectDir;
    private File m_ProjectSystemDir;
    private File m_TempDir;
    private Project m_Project;
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        m_ProjectDir = new File( "target/test/projectdir");
        m_ProjectDir.mkdirs();
        
        PluginProperties projectProps = new PluginProperties();
        InputStream in = getClass().getResourceAsStream( "test.properties");
        projectProps.load( in );
        
        m_PluginDir = new File( "target/test/plugins");
        m_PluginDir.mkdirs();
        
        m_SystemDir = new File( "target/test/system");
        m_SystemDir.mkdirs();
        
        m_TempDir = new File( "target/test/temp");
        m_TempDir.mkdirs();
        
        m_ProjectSystemDir = new File( "../../../central/system");

        m_Project = new Project();
        m_Project.setBaseDir( m_ProjectDir );
        m_Project.setCoreLoader( this.getClass().getClassLoader() );
        m_Project.setName( "testcase project" );
        m_Project.init();
                
        m_Context = new PluginContext
        ( 
            " testcase project ", 
            m_ProjectDir,
            m_ProjectSystemDir, 
            projectProps,
            "testcase plugin", 
            m_PluginDir, 
            m_SystemDir,
            m_TempDir,
            m_Project
        );
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

    public void testGetNullProperty()
    {
        String p1 = null;
        String value = m_Context.resolve( p1 );
        assertNull( "Null lookup failed.", value );
    }
    
    public void testGetProperty1()
    {
        String p1 = "niclas${abc.def}hedhman";
        String value = m_Context.resolve( p1 );
        assertEquals( "Unresolvable failed.", p1, value );
    }

    public void testGetProperty2()
    {
        String p1 = "niclas ${a.property } hedhman";
        String value = m_Context.resolve( p1 );
        assertEquals( "Single Level resolution failed.", "niclas has the surname of hedhman", value );
    }

    public void testGetProperty3()
    {
        String p1 = "${a2.this}";
        String value = m_Context.resolve( p1 );
        assertEquals( "Property resolution failed.", "this is", value );
    }

    public void testGetProperty4()
    {
        String p1 = "Hey, ${a2.${a1}} ${a2.${a4}} ${a3}";
        String value = m_Context.resolve( p1 );
        assertEquals( "Nested resolution failed.", "Hey, this is this is not this is funky", value );
    }

    public void testGetProperty5()
    {
        String p1 = "${${${${${${b1}}}}}}";
        String value = m_Context.resolve( p1 );
        assertEquals( "Nested resolution failed.", "YEAH!!!!", value );
    }

    public void testGetAntProject()
    {
        assertTrue( "Ant Project failed.", m_Context.getAntProject() != null );
        assertEquals( "Ant Project failed.", "testcase project", m_Context.getAntProject().getName() );
    }
    
    public void testResolvePluginDir()
    {
        String expected = "This is " + m_PluginDir.getAbsolutePath() + " right.";
        assertEquals( "PluginDir not retrievable.", expected, m_Context.resolve( "This is ${plugin.dir} right." ) );
    }
}
