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
package org.apache.avalon.ide.eclipse.merlin.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a> *
 */
public class NewMerlinProjectWizardPage extends WizardPage implements Listener
{

    private Text containerName;
    private Text version;
    private Text serviceName;
    private Text serviceClassName;
    private Text componentClassName;
    private Label help;

    public NewMerlinProjectWizardPage(String pageName)
    {
        super(pageName);
        this.setPageComplete(false);
    }

    public void createControl(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        setPageComplete(false);

        createControls(composite);

        setControl(composite);
    }

    protected void createControls(Composite area)
    {

        {
            final Group group = new Group(area, SWT.NONE);
            group.setText("Container");
            final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            group.setLayoutData(gridData);
            final GridLayout gridLayout_1 = new GridLayout();
            gridLayout_1.numColumns = 2;
            group.setLayout(gridLayout_1);
            {
                final Label label = new Label(group, SWT.NONE);
                label.setText("Name:");
            }
            {
                containerName = new Text(group, SWT.BORDER);
                final GridData gridData_1 = new GridData(GridData.FILL_HORIZONTAL);
                gridData_1.horizontalSpan = 1;
                containerName.setLayoutData(gridData_1);
            }
            {
                final Label label = new Label(group, SWT.NONE);
                final GridData gridData_1 = new GridData();
                gridData_1.widthHint = 86;
                label.setLayoutData(gridData_1);
                label.setText("Version:");
            }
            {
                version = new Text(group, SWT.BORDER);
                final GridData gridData_1 = new GridData();
                gridData_1.widthHint = 130;
                version.setLayoutData(gridData_1);
            }
        }
        {
            final Group group = new Group(area, SWT.NONE);
            group.setText("Service");
            group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            final GridLayout gridLayout_1 = new GridLayout();
            gridLayout_1.numColumns = 2;
            group.setLayout(gridLayout_1);
            {
                final Label label = new Label(group, SWT.NONE);
                label.setText("Name:");
            }
            {
                serviceName = new Text(group, SWT.BORDER);
                final GridData gridData = new GridData();
                gridData.widthHint = 130;
                serviceName.setLayoutData(gridData);
            }
            {
                final Label label = new Label(group, SWT.NONE);
                label.setText("Service Class:");
            }
            {
                serviceClassName = new Text(group, SWT.BORDER);
                serviceClassName.addFocusListener(new FocusAdapter()
                {
                    public void focusGained(FocusEvent e)
                    {
                        setMessage("Please enter the fully qualified classname of the service without the '.java' extension");
                    }
                    public void focusLost(FocusEvent e)
                    {
                        setMessage(null);
                        if (componentClassName.getText().length() > 0)
                            checkClassName(serviceClassName);
                    }
                });
                final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
                gridData.horizontalSpan = 1;
                serviceClassName.setLayoutData(gridData);
            }
            {
                final Label label = new Label(group, SWT.NONE);
                label.setText("Component Class:");
            }
            {
                componentClassName = new Text(group, SWT.BORDER);
                componentClassName.addFocusListener(new FocusAdapter()
                {
                    public void focusGained(FocusEvent e)
                    {
                        setMessage("Please enter the fully qualified classname of the component without the '.java' extension");
                    }
                    public void focusLost(FocusEvent e)
                    {
                        setMessage(null);
                        if (componentClassName.getText().length() > 0)
                            checkClassName(componentClassName);
                    }
                });
                componentClassName.addModifyListener(new ModifyListener()
                {
                    public void modifyText(ModifyEvent e)
                    {
                        setMessage(null);
                        if (componentClassName.getText().indexOf('.') > 0)
                        {
                            setPageComplete(true);
                        } else
                        {
                            setPageComplete(false);
                        }
                    }
                });

                componentClassName.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
            }
        }
    }

    protected void checkClassName(Text textField)
    {

        if (textField.getText().endsWith(".java"))
        {
            setErrorMessage("Please enter the class name without '.java' extension");
            setPageComplete(false);
            return;
        }
        if (textField.getText().indexOf('.') < 0)
        {
            setErrorMessage("Please give a package name. A default package is not allowed");
            textField.selectAll();
            textField.setFocus();
            setPageComplete(false);
        } else
        {
            setPageComplete(true);
            setMessage(null);
            setErrorMessage(null);
        }
    }

    /**
     * @return the ContainerName text value
     */
    public String getContainerName()
    {
        return containerName.getText();
    }

    /**
     * @return the version number
     */
    public String getVersion()
    {
        return version.getText();
    }

    /**
     * @return the serviceName text value
     */
    public String getServiceName()
    {
        return serviceName.getText();
    }

    /**
     * @return the serviceClassName text value
     */
    public String getServiceClassName()
    {
        return serviceClassName.getText();
    }

    /**
     * @return the componentClassName text value
     */
    public String getComponentClassName()
    {
        return componentClassName.getText();
    }

    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        if (visible)
            containerName.setFocus();
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    public void handleEvent(Event pEvent)
    {
        // Nothing to right now
        
    }

}