/*

 Copyright 2004. The Apache Software Foundation.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License. 

 */
package org.apache.metro.studio.eclipse.core.templateengine.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import junit.framework.TestCase;

import org.apache.metro.studio.eclipse.core.MetroStudioCore;
import org.apache.metro.studio.eclipse.core.templateengine.DirectoryTemplateManager;
import org.apache.metro.studio.eclipse.core.templateengine.ProjectManager;
import org.apache.metro.studio.eclipse.core.templateengine.Resource;
import org.apache.metro.studio.eclipse.core.templateengine.ResourceTemplate;
import org.apache.metro.studio.eclipse.core.templateengine.ResourceTemplateManager;
import org.apache.metro.studio.eclipse.core.tools.DynProjectParam;
import org.eclipse.core.resources.IProject;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Metro Development Team </a>
 *         12.08.2004 last change:
 *  
 */
public class ResourceTemplateManagerTest extends TestCase
{
    final static String directoryLocation = DirectoryTemplateManagerTest.configFileLocation;

    final static String baseDir = MetroStudioCore.getDefault()
            .getPluginLocation().toString();

    final static String resourcesLocation = baseDir
            + "config/resources.test_cfg";

    static IProject project;

    public static void main(String[] args)
    {
    }

    public final void testCreateResourceTemplate()
    {
        XStream xstream = new XStream(new DomDriver());
        ResourceTemplateManager.initXStream(xstream);
        ResourceTemplateManager rm = new ResourceTemplateManager();

        // import DirectoryTemplates, which are created in
        // DirectoryTemplatemanagerTest
        DirectoryTemplateManager dm = DirectoryTemplateManager
                .load(directoryLocation);
        dm.addXStreamAliases(xstream);
        rm.importDirectoryTemplates(dm);

        rm.addResourceTemplate(createHelloWorldResources());
        rm.addResourceTemplate(createCompositionApplicationResources());

        try
        {
            Writer out = new FileWriter(resourcesLocation);
            xstream.toXML(rm, out);
        } catch (IOException e)
        {
            fail("unable to write config file");
        }

        rm = ResourceTemplateManager.load(resourcesLocation);

        assertNotNull("Could not reload xml file", rm);

    }

    public final void testCreateHelloWorldProject()
    {
        project = ProjectManager.createBlockProject("HelloWorld Tutorial");
        ResourceTemplateManager rm = ResourceTemplateManager
                .load(resourcesLocation);
        rm.create(project, "HelloWorld Tutorial", new DynProjectParam());
        
        String testpath;
        assertEquals("Project was not created", true, (new File(project.getLocation().toString()).exists()));
        testpath = "/src/tutorial/HelloComponent.java";
        assertEquals("HelloComponent not created", true, (new File(project.getLocation().toString()+testpath).exists()));
        testpath = "/src/BLOCK-INF/block.xml";
        assertEquals("block.xml not created", true, (new File(project.getLocation().toString()+testpath).exists()));

        // ProjectManager.delete(project);
    }

    public final void testCreateCompositionApplicationProject()
    {
        project = ProjectManager.createBlockProject("Composition (Application) Tutorial");
        ResourceTemplateManager rm = ResourceTemplateManager
                .load(resourcesLocation);
        rm.create(project, "Composition (Application) Tutorial", new DynProjectParam());
        
        String testpath;
        assertEquals("Project was not created", true, (new File(project.getLocation().toString()).exists()));
        testpath = "/impl/tutorial/Application/Application.java";
        assertEquals("HelloComponent not created", true, (new File(project.getLocation().toString()+testpath).exists()));
        testpath = "/impl/BLOCK-INF/block.xml";
        assertEquals("block.xml not created", true, (new File(project.getLocation().toString()+testpath).exists()));
        testpath = "/impl/BLOCK-INF/debug.xml";
        assertEquals("debug.xml not created", true, (new File(project.getLocation().toString()+testpath).exists()));

        // ProjectManager.delete(project);
    }

    /**
     * @return
     */
    private ResourceTemplate createHelloWorldResources()
    {
        ResourceTemplate rt = new ResourceTemplate();
        rt.setTemplateId("HelloWorld Tutorial");
        rt.setDirectoryType("StandardBlock");

        Resource r = new Resource();
        r.setRootSegment("src");
        r.setSourceFilePathName(baseDir+"templates/hello/HelloComponent.java");
        r.setPackageName("tutorial");
        rt.addResource(r);

        r = new Resource();
        r.setRootSegment("src/BLOCK-INF");
        r.setSourceFilePathName(baseDir+"templates/hello/block.xml");
        r.setPackageName("");
        rt.addResource(r);

        return rt;
    }
    /**
     * @return
     */
    private ResourceTemplate createCompositionApplicationResources()
    {
        ResourceTemplate rt = new ResourceTemplate();
        rt.setTemplateId("Composition (Application) Tutorial");
        rt.setDirectoryType("ImplApiBlock");

        Resource r = new Resource();
        r.setRootSegment("impl");
        r.setSourceFilePathName(baseDir+"templates/composition/application/Application.java");
        r.setPackageName("tutorial.application");
        rt.addResource(r);

        r = new Resource();
        r.setRootSegment("impl/BLOCK-INF");
        r.setSourceFilePathName(baseDir+"templates/composition/application/block.xml");
        r.setPackageName("");
        rt.addResource(r);

        r = new Resource();
        r.setRootSegment("impl/BLOCK-INF");
        r.setSourceFilePathName(baseDir+"templates/composition/application/debug.xml");
        r.setPackageName("");
        rt.addResource(r);

        return rt;
    }

}