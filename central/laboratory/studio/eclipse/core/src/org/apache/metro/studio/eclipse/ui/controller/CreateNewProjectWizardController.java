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
package org.apache.metro.studio.eclipse.ui.controller;

import java.rmi.RemoteException;

import org.apache.metro.facility.presentationservice.api.ControllerEventService;
import org.apache.metro.facility.presentationservice.api.ValueObjectEventListener;
import org.apache.metro.facility.presentationservice.impl.PresentationEvent;
import org.apache.metro.facility.presentationservice.impl.PresentationServiceFactory;
import org.apache.metro.studio.eclipse.core.MetroStudioCore;
import org.apache.metro.studio.eclipse.core.templateengine.ProjectManager;
import org.apache.metro.studio.eclipse.core.templateengine.ResourceTemplateManager;
import org.apache.metro.studio.eclipse.core.tools.DynProjectParam;
import org.eclipse.core.resources.IProject;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Metro Development Team</a>
 * 14.08.2004
 * last change:
 * 
 */
public class CreateNewProjectWizardController
{
    final static String baseDir = MetroStudioCore.getDefault()
    .getPluginLocation().toString();

    final static String resourcesLocation = baseDir
    + "config/resources.test_cfg";

    private String selectedTemplate;
    /**
     * 
     */
    public CreateNewProjectWizardController()
    {
        super();
        initializeListeners();
    }

    public void initializeListeners()
    {
        ControllerEventService service = null;

        try
        {
            service = PresentationServiceFactory.getControllerEventService();

            // create project when apply button is pressed
            service.addClickedListener(
                    "CreateNewProjectWizard.applyButton",
                    new ValueObjectEventListener()
                    {
                        public PresentationEvent notify(PresentationEvent evt)
                                throws RemoteException
                        {
                            String projectName = (String)evt.getData().get("selectedTemplate");
                            IProject project = ProjectManager.createBlockProject("HelloWorld Tutorial");
                            ResourceTemplateManager rm = ResourceTemplateManager
                                    .load(resourcesLocation);
                            rm.create(project, selectedTemplate, new DynProjectParam());

                            return evt;

                        }
                    });

            service.addClickedListener(
                    "CreateNewProjectWizard.exampleProjectList",
                    new ValueObjectEventListener()
                    {
                        public PresentationEvent notify(PresentationEvent evt)
                                throws RemoteException
                        {
                            selectedTemplate = (String)evt.getData().get("selectedTemplate");
                            return evt;

                        }
                    });
        } catch (Exception e)
        {
            MetroStudioCore.log(e, "Can't initialize PresentationService");
        }

    }

}
