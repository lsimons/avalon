/*
 * Created on 20.08.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.metro.studio.eclipse.ui.common;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author EH2OBCK
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class CommonDialogs {
	
	public static String getPath(Shell shell)
	{
		File directory = null;
		DirectoryDialog fileDialog = new DirectoryDialog(shell, SWT.OPEN);
		//if (startingDirectory != null)
			//fileDialog.setFilterPath(startingDirectory.getPath());
		String dir = fileDialog.open();
		if (dir != null) {
			dir = dir.trim();
			if (dir.length() > 0)
				directory = new File(dir);
		}
		if(dir == null) return null;
		return directory.getAbsolutePath();
	}
	public static String getFilePath(Shell shell)
	{
		File directory = null;
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
		//if (startingDirectory != null)
			//fileDialog.setFilterPath(startingDirectory.getPath());
		String dir = fileDialog.open();
		if (dir != null) {
			dir = dir.trim();
			if (dir.length() > 0)
				directory = new File(dir);
		}
		if(dir == null) return null;
		return directory.getAbsolutePath();
	}
}
