/*
 * Created on 15.03.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.avalon.ide.eclipse.merlin.builder;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Andreas Develop
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface IMerlinBuilder
{

    /**
     * @param pKind
     * @param pArgs
     * @param pMonitor
     */
    public void build(int pKind, IProject project, List pFiles, IProgressMonitor pMonitor);
}
