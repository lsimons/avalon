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

import java.util.Iterator;
import java.util.List;

import org.apache.avalon.ide.eclipse.core.resource.ProjectResourceManager;
import org.apache.avalon.ide.eclipse.core.xmlmodel.ProjectModel;
import org.apache.avalon.ide.eclipse.core.xmlmodel.ProjectModelConfiguration;
import org.apache.avalon.ide.eclipse.merlin.ui.MerlinDeveloperUI;
import org.apache.avalon.ide.eclipse.merlin.wizards.WizardUtil.SelectionPageCellModifier;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.internal.ui.elements.ListContentProvider;
import org.eclipse.pde.internal.ui.parts.FormBrowser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a> 
 */
public class NewMerlinProjectSelectionPage extends WizardPage implements ISelectionChangedListener
{

    private WizardSelectedAction doubleClickAction = new WizardSelectedAction();
    private FormBrowser text;
    private TableViewer projectTabel;
    private ProjectModel finalSelection;
    private List projectModels;
   

    private class WizardSelectedAction extends Action
    {
        public WizardSelectedAction()
        {
            super("wizardSelection");
        }
    }

    public NewMerlinProjectSelectionPage(String pageName)
    {
        super(pageName);
        setPageComplete(false);
    }

    /**
	 * Main method to create all controls of this page. This method is called
	 * by the wizard framework.
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
    public void createControl(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        createContainerGroup(composite);
        setPageComplete(false);
        setErrorMessage(null);
        setMessage(null);
        setControl(composite);
    }

    /**
	 * create all controls.
	 * 
	 * @param pParent
	 *            (Composite)
	 */
    protected void createContainerGroup(Composite parent)
    {
        Composite area = new Composite(parent, SWT.NULL);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.makeColumnsEqualWidth = true;
        gridLayout.numColumns = 2;
        area.setLayout(gridLayout);
        area.setLayoutData(new GridData(GridData.FILL_BOTH));

        {
            /**
             * Titel for all controls
             */
            final Label label = new Label(area, SWT.NONE);
            final GridData gridData = new GridData();
            gridData.horizontalSpan = 2;
            label.setLayoutData(gridData);
            label.setText("available generator wizards");
        }
        {
            /**
             * Tabel for Project Types
             */
            Table table = createTable(area);
            projectTabel = new TableViewer(table);
            projectTabel.setColumnProperties(new String []{"", "", ""});
            projectTabel.setContentProvider(new ListContentProvider());
            projectTabel.setLabelProvider(WizardUtil.TABLE_LABEL_PROVIDER);
            // Set the cell modifier for the viewer
            projectTabel.setCellModifier(new SelectionPageCellModifier(this));
            // Create the cell editors
            CellEditor[] editors = new CellEditor[1];
            // Column 1 : Completed (Checkbox)
            editors[0] = new CheckboxCellEditor(table);
            // Assign the cell editors to the viewer 
            projectTabel.setCellEditors(editors);
            
            projectTabel.addDoubleClickListener(new IDoubleClickListener()
             {
                public void doubleClick(DoubleClickEvent event)
                {
                    doubleClickAction.run();
                }
            });
            projectTabel.addSelectionChangedListener(this);

            // list view pane. Add a border to the pane.
            projectTabel.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

             // Add the editors and images to the table

        }
        
        {
            /**
             * HTML Text Editor foe showing the project description
             */
            text = new FormBrowser(SWT.BORDER | SWT.V_SCROLL);
            text.createControl(area);
            final GridData gridData = new GridData(GridData.FILL_BOTH);
            Control c = text.getControl();
            c.setLayoutData(gridData);
        }
        
        /**
         * Fill values to the Table
         */
        
        // Get the Metainfo for the NewProjectWizards
        ProjectModelConfiguration projectConfig =
            ProjectResourceManager.getProjectModelConfiguration(
                "/properties/newProjectWizard.xcfg",
                MerlinDeveloperUI.PLUGIN_ID);

        // show all avalilable ProjectTypes
        projectModels = projectConfig.getProjectModels();
        Iterator it = projectModels.iterator();

        while (it.hasNext())
        {
            ProjectModel meta = (ProjectModel) it.next();
            projectTabel.add(meta);
        }
        if (projectConfig.getProjectModels().size() > 0)
        {
            text.setText(((ProjectModel) projectConfig.getProjectModels().get(0)).getDescription());
            ((Table) projectTabel.getControl()).select(0);
            finalSelection = (ProjectModel) projectConfig.getProjectModels().get(0);
        }
        /**
         * End filling the values
         */

    }

    public TableViewer getProjectTable(){
        return projectTabel;
    }

    /**
     * Create the Table Control, which is part of the TableViewer
     * @param pParent
     * @param pStyle
     * @return
     */
    private Table createTable(Composite parent)
    {
        int style = SWT.SINGLE | SWT.BORDER |  
        SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
        
        Table table = new Table(parent, style);

        TableColumn column = new TableColumn(table, SWT.NONE, 0);
        column.setWidth(22);
        column = new TableColumn(table, SWT.NONE, 1);
        column.setWidth(20);
        column = new TableColumn(table, SWT.NONE, 2);
        column.setWidth(150);
        TableLayout layout = new TableLayout();
        //layout.addColumnData(new ColumnWeightData(100));
        table.setLayout(layout);
        return table;
    }

    public void selectionChanged(SelectionChangedEvent event)
    {
        setErrorMessage(null);
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        ProjectModel currentSelectedWizard = null;
        Iterator iter = selection.iterator();

        if (iter.hasNext())
            currentSelectedWizard = (ProjectModel) iter.next();
        if (currentSelectedWizard == null)
        {
            text.setText("");
            return;
        }
        finalSelection = currentSelectedWizard;

        BusyIndicator.showWhile(projectTabel.getControl().getDisplay(), new Runnable()
        {
            public void run()
            {

                text.setText(finalSelection.getDescription());

            }
        });

    }

    /**
	 * @return the selected projectModel
	 */
    public ProjectModel getSelectedProjectModel()
    {
        return finalSelection;
    }

    /**
	 * Called by the main Wizard to check, whether one can call the next wizard
	 * page.
	 * 
	 * @see org.eclipse.jface.wizard.IWizardPage#isPageComplete()
	 */
    public boolean isPageComplete()
    {

        return (finalSelection != null);
    }

    /**
     * @param pTask
     */
    public void updateTable(ProjectModel pTask)
    {
        Iterator it = projectModels.iterator();
        while(it.hasNext()){
            ProjectModel model = (ProjectModel)it.next();
            if(model.isSelected() && !(model.equals(pTask))){
                    model.setSelected(false);
                    projectTabel.update(model, new String[]{"",""});
                }
        }
        
        projectTabel.update(pTask, new String[]{"",""});
    }
}