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
package org.apache.metro.studio.eclipse.ui.wizard.test;

import java.util.Hashtable;

import junit.framework.TestCase;

import org.apache.metro.facility.presentationservice.api.ViewEventService;
import org.apache.metro.facility.presentationservice.impl.PresentationEvent;
import org.apache.metro.facility.presentationservice.impl.PresentationServiceFactory;
import org.apache.metro.studio.eclipse.core.templateengine.ResourceTemplate;
import org.apache.metro.studio.eclipse.ui.controller.CreateNewProjectWizardController;
import org.eclipse.core.resources.IProject;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Metro Development Team</a>
 * 11.08.2004
 * last change:
 * 
 */
public class CreateNewProjectWizardMock extends TestCase
{

    private static IProject project;
    private ViewEventService service;
    public final String TOPIC = "CreateNewProjectWizard"; 
    
    public static void main(String[] args)
    {
    }

    /**
     * This test is done here to test the functionality of the presentation service
     * All tests of template creation is done in MetroStudioCore in detail.
     */
    public final void testCreateHelloWorldProject()
    {
        ResourceTemplate template;
        new CreateNewProjectWizardController().initializeListeners();

		try
        {
            service = PresentationServiceFactory.getViewEventService();
            Hashtable value = new Hashtable();
            
            // mock the "wizard page created event" to get all ResourceTemplate names
            
            // mock the "template selected event" in TemplateSelectionWizardPage
            value.put("selectedTemplate", "HelloWorld Tutorial");
            PresentationEvent event = new PresentationEvent(this, TOPIC + ".exampleProjectList", value);
            event = service.clicked(event);
            
            // mock the "template apply event" in NewProjectWizard
            value.put("projectName", "HelloWorld Tutorial");
            event = new PresentationEvent(this, TOPIC + ".applyButton", value);
            event = service.clicked(event);

        } catch (Exception e)
        {
            fail("could not create HelloWorld Project");
        }
	}

}
