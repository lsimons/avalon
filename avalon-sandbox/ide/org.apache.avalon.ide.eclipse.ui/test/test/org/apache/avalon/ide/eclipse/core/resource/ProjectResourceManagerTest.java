/*
 * Created on 03.01.2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package test.org.apache.avalon.ide.eclipse.core.resource;

import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.avalon.ide.eclipse.core.resource.ProjectResourceManager;
import org.apache.avalon.ide.eclipse.core.xmlmodel.Directory;
import org.apache.avalon.ide.eclipse.core.xmlmodel.ProjectModel;
import org.apache.avalon.ide.eclipse.core.xmlmodel.ProjectModelConfiguration;
import org.apache.avalon.ide.eclipse.merlin.ui.MerlinDeveloperUI;

/**
 * @author Andreas
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class ProjectResourceManagerTest extends TestCase
{

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(ProjectResourceManagerTest.class);
    }

    /*
	 * Class to test for void ProjectResourceManager(String, String)
	 */
    public void testGetProjectModelConfiguration()
    {
        ProjectModelConfiguration projectConfig =
            ProjectResourceManager.getProjectModelConfiguration(
                "/properties/NewProjectConfigTest.xcfg",
                MerlinDeveloperUI.PLUGIN_ID);

        Iterator it = projectConfig.getProjectModels().iterator();
        while (it.hasNext())
        {
            // Project 1
            ProjectModel model = (ProjectModel) it.next();
            this.assertEquals(
                model.getLabel() + "model test1 ",
                4,
                model.getDirectories().size());
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
            this.assertEquals(
                model.getLabel() + "model test2 ",
                4,
                model.getDirectories().size());
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
            this.assertEquals(
                model.getLabel() + "model test3 ",
                0,
                model.getDirectories().size());

        }
        this.assertEquals("project models test", 3, projectConfig.getProjectModels().size());
    }

}
