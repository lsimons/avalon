package org.apache.metro.studio.eclipse.ui.preferences;

import org.apache.metro.studio.eclipse.ui.MetroStudioUI;
import org.apache.metro.studio.eclipse.ui.common.ModelObject;
import org.apache.metro.studio.eclipse.ui.panels.RepositorySettingsPanel;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage </samp>,
 * we can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */
public class RepositoryPreferencePage extends PreferencePage
		implements
			IWorkbenchPreferencePage {
	
	RepositorySettingsPanel panel;
	
	public RepositoryPreferencePage() {
		super();
		setPreferenceStore(MetroStudioUI.getDefault().getPreferenceStore());
		setDescription("A demonstration of a preference page implementation");
	}

	
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		FillLayout layout = new FillLayout();
		composite.setLayout(layout);

		panel = new RepositorySettingsPanel();

		panel.createControls(composite);
		panel.initializeControls(new ModelObject(this));
		return composite;
	}
	
	public void init(IWorkbench workbench) {
	}
	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performApply()
	 */
	protected void performApply() {
		panel.performApply();
		super.performApply();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		panel.performDefaults();
		super.performDefaults();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		panel.performApply();
		return super.performOk();
	}
}