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

package org.apache.avalon.ide.eclipse.merlin.preferences;


import org.apache.avalon.ide.eclipse.merlin.ui.MerlinDeveloperUI;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class MerlinMainPreferencePage
	extends PreferencePage
	implements IWorkbenchPreferencePage {

	public MerlinMainPreferencePage() {
		super();
		setPreferenceStore(MerlinDeveloperUI.getDefault().getPreferenceStore());
		setDescription("General settings for Merlin Container");
		initializeDefaults();
	}
/**
 * Sets the default values of the preferences.
 */
	private void initializeDefaults() {
		IPreferenceStore store = getPreferenceStore();
	}
	
	public Control createContents(Composite pParent){
        
        Composite area = new Composite(pParent, SWT.NONE);
        area.setLayoutData(new GridData(GridData.FILL_BOTH));
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.verticalSpacing = 15;
        area.setLayout(gridLayout_1);
        {
            final Group group = new Group(area, SWT.NONE);
            group.setText("Installation Properties");
            group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            final GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 3;
            group.setLayout(gridLayout);
            
            {
                final Label label = new Label(group, SWT.NONE);
                label.setText("Merlin Insallation Directory:");
            }
            {
                final Text text = new Text(group, SWT.BORDER);
                text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            }
            {
                final Button button = new Button(group, SWT.NONE);
                button.setText("browse");
            }
            {
                final Label label = new Label(group, SWT.NONE);
                label.setText("Merlin Configuration Directory:");
            }
            {
                final Text text = new Text(group, SWT.BORDER);
                text.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
            }
            {
                final Button button = new Button(group, SWT.NONE);
                button.setText("browse");
            }
            {
                final Label label = new Label(group, SWT.NONE);
                label.setText("Kernel Configuration File:");
            }
            {
                final Text text = new Text(group, SWT.BORDER);
                text.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
            }
            {
                final Button button = new Button(group, SWT.NONE);
                button.setText("browse");
            }
            {
                final Label label = new Label(group, SWT.NONE);
                label.setText("System Repository:");
            }
            {
                final Text text = new Text(group, SWT.BORDER);
                text.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
            }
            {
                final Button button = new Button(group, SWT.NONE);
                button.setText("browse");
            }
            {
                final Label label = new Label(group, SWT.NONE);
                label.setText("Application Repository:");
            }
            {
                final Text text = new Text(group, SWT.BORDER);
                text.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
            }
            {
                final Button button = new Button(group, SWT.NONE);
                button.setText("browse");
            }
        }
        {
        	final Group group = new Group(area, SWT.NONE);
        	group.setText("User Properties");
        	group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        	final GridLayout gridLayout = new GridLayout();
        	gridLayout.numColumns = 3;
        	group.setLayout(gridLayout);
        }
        {
        	final Group group = new Group(area, SWT.NONE);
        	group.setText("System Properties");
        	group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        	final GridLayout gridLayout = new GridLayout();
        	gridLayout.numColumns = 3;
        	group.setLayout(gridLayout);
        }
        {
            final Group group = new Group(area, SWT.NONE);
            group.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
            group.setText("Standard Properties");
            final GridLayout gridLayout = new GridLayout();
            group.setLayout(gridLayout);
            {
                final Button button = new Button(group, SWT.CHECK);
                button.setText("Show Infoheader at Startup");
            }
            {
                final Button button = new Button(group, SWT.CHECK);
                button.setText("Show Debug Informations");
            }
            {
                final Button button = new Button(group, SWT.CHECK);
                button.setText("Start Server as permanent Task");
            }
        }
        
        return null;
    }
	public void init(IWorkbench workbench) {
	}
}