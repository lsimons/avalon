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
package test.org.apache.avalon.ide.eclipse.core.resource;

import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.avalon.ide.eclipse.core.resource.ProjectResourceManager;
import org.apache.avalon.ide.eclipse.core.tools.DynProjectParam;
import org.apache.avalon.ide.eclipse.core.xmlmodel.Directory;
import org.apache.avalon.ide.eclipse.core.xmlmodel.ProjectModel;
import org.apache.avalon.ide.eclipse.core.xmlmodel.ProjectModelConfiguration;
import org.apache.avalon.ide.eclipse.merlin.launch.MerlinDeveloperLaunch;
import org.apache.avalon.ide.eclipse.merlin.nature.MerlinProjectNature;
import org.apache.avalon.ide.eclipse.merlin.ui.MerlinDeveloperUI;

/**
 * @author Andreas
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class ProjectResourceManagerTest extends TestCase
{
    private static ProjectModelConfiguration projectConfig;
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(ProjectResourceManagerTest.class);
    }

    /*
     * Class to test for void ProjectResourceManager(String, String)
     */
    public void testGetProjectModelConfiguration()
    {
        projectConfig =
            ProjectResourceManager.getProjectModelConfiguration(
                "/properties/NewProjectConfigTest.xcfg",
                MerlinDeveloperUI.PLUGIN_ID);

        Iterator it = projectConfig.getProjectModels().iterator();
        while (it.hasNext())
        {
            // Project 1
            ProjectModel model = (ProjectModel) it.next();
            this.assertEquals(model.getLabel() + "model test1 ", 4, model.getDirectories().size());
            // Directory1
            Iterator d = model.getDirectories().iterator();
            while (d.hasNext())
            {
                Directory dir = (Directory) d.next();
                this.assertEquals(
                    dir.getName() + "Directory test1 (api) ",
                    0,
                    dir.getTemplates().size());

                dir = (Directory) d.next();
                this.assertEquals(
                    dir.getName() + "Directory test2 (impl) ",
                    0,
                    dir.getTemplates().size());

                dir = (Directory) d.next();
                this.assertEquals(
                    dir.getName() + "Directory test3 (impl/block-inf) ",
                    1,
                    dir.getTemplates().size());

                dir = (Directory) d.next();
                this.assertEquals(
                    dir.getName() + "Directory test4 (test) ",
                    0,
                    dir.getTemplates().size());
            }
            // Project2
            model = (ProjectModel) it.next();
            this.assertEquals(model.getLabel() + "model test2 ", 4, model.getDirectories().size());
            // Directory1
            d = model.getDirectories().iterator();
            while (d.hasNext())
            {
                Directory dir = (Directory) d.next();
                this.assertEquals(
                    dir.getName() + "Directory test1 (api) ",
                    0,
                    dir.getTemplates().size());

                dir = (Directory) d.next();
                this.assertEquals(
                    dir.getName() + "Directory test2 (impl) ",
                    2,
                    dir.getTemplates().size());

                dir = (Directory) d.next();
                this.assertEquals(
                    dir.getName() + "Directory test3 (impl/block-inf) ",
                    1,
                    dir.getTemplates().size());

                dir = (Directory) d.next();
                this.assertEquals(
                    dir.getName() + "Directory test4 (test) ",
                    0,
                    dir.getTemplates().size());
            }

            // Project3
            model = (ProjectModel) it.next();
            this.assertEquals(model.getLabel() + "model test3 ", 0, model.getDirectories().size());

        }
        this.assertEquals("project models test", 3, projectConfig.getProjectModels().size());
    }
/*
 * Test the creation of a new project
 */
    public void testCreateProject() throws Exception
    {
        final String projectName = "MerlinTest";
        final String nature = MerlinDeveloperLaunch.MERLIN_PROJECT_NATURE_ID;
        DynProjectParam param = new DynProjectParam();
        param.setProjectName(projectName);
        param.setContainerName("m_test");
        param.setFullImplementationClassName("tutorial.impl.HelloWorldIml");
        param.setFullServiceClassName("tutorial.api.HelloWorld");
        param.setVersion("1.0.1");
        param.setVirtualServiceName("hello world");

        Iterator it = projectConfig.getProjectModels().iterator();
        ProjectResourceManager prm = null;

        while (it.hasNext())
        {
            prm =
                new ProjectResourceManager(
                    (ProjectModel) it.next(),
                    param,
                    MerlinDeveloperUI.PLUGIN_ID,
                    nature);
        }

        assertEquals("test project name ", prm.getProject().getName(), projectName);
        assertEquals(
            "test project nature ",
            (prm.getProject().getNature(nature) instanceof MerlinProjectNature),
            true);

    }

}
