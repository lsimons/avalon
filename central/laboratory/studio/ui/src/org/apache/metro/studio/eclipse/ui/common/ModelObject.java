/*
 * Created on 20.08.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.metro.studio.eclipse.ui.common;

import java.util.Hashtable;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * @author EH2OBCK
 *
 * This ModelObject implements the Presentation IModelObject
 * without using the event handling
 * It only shield the differences between the stores used by PreferencePages
 * or PropertyPages so that this Panel can be reused in either situation.
 */
public class ModelObject implements IModelObject {
	private transient Hashtable defaults = new Hashtable();
	
	private IResource element;
	private IViewPresentationService service;
	private IPreferenceStore store;
	/**
	 * 
	 */
	public ModelObject(DialogPage page) {
		super();
		if(page instanceof PropertyPage)
		{
			element = (IResource)((PropertyPage)page).getElement();
		} else
		{
			store = ((PreferencePage)page).getPreferenceStore();
		}
	}
	public String getDefaultString(String name)
	{
		if(element != null)
		{return (String)defaults.get(name);
		}
		if(store != null)
		{
			return store.getDefaultString(name);
		}
		return null;
	}
	public String getString(String name)
	{
		try {
			if(element != null)
			{return (String)element.getPersistentProperty(new QualifiedName("", name));
			}
			if(store != null)
			{
				return store.getString(name);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * @param repository_panel
	 */
	public ModelObject controlClicked(String controlName) {
		
		// local = service.clicked(controlName, this);
		return this;
	}
	public void setDefaultString(String name, String defaultValue)
	{
		if(element != null)
		{defaults.put(name, defaultValue);
		}
		if(store != null)
		{
			store.setDefault(name, defaultValue);
		}
	}
	
	public void setString(String name, String value)
	{
		try {
			if(element != null)
			{element.setPersistentProperty(new QualifiedName("", name),
					value);
			}
			if(store != null)
			{
				store.setValue(name, value);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	/**
	 * @param repository_panel
	 * @return
	 */
	public ModelObject windowCreated(String repository_panel) {
		
		return this;
	}

}
