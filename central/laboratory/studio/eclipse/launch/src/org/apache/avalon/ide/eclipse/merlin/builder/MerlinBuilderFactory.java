/*
 * One has to add the builder to a project first.
 * This is done, when the project is build in
 * MerlinDeveloperCore ProjectResource.addBuilder()
 */
package org.apache.avalon.ide.eclipse.merlin.builder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * @author Andreas Develop
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MerlinBuilderFactory extends IncrementalProjectBuilder
{
    private static List builderList = new ArrayList();

    /**
     * 
     */
    public MerlinBuilderFactory()
    {
        super();
    }

    /*
     * Add builders to the build process. This method is also called
     * by other plug-in (e.g. EnterpriseDeveloper).
     * Registration of builders is done in the main plug-in class (eg. EnterpriseDeveloperCore.java)
     *   
     */
    public static void addBuilder(IMerlinBuilder builder)
    {

        // builderList.add(new YourOwnBuilder());
        builderList.add(builder);

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IProject[] build(int pKind, Map pArgs, IProgressMonitor pMonitor)
        throws CoreException
    {
        if(getDelta(getProject())==null) return null;
        
        IResourceDelta delta[] = getDelta(getProject()).getAffectedChildren();
        List files = getChangedResource(delta);


        Iterator it = builderList.iterator();
        while (it.hasNext())
        {
            IMerlinBuilder builder = (IMerlinBuilder) it.next();
            builder.build(pKind, getProject(), files, pMonitor);
        }
        IJavaProject proj = JavaCore.create(getProject());
        proj.getProject().refreshLocal(IProject.DEPTH_INFINITE, null);
        return null;
    }

    /*
     * retrieves the changed resource.
     * only returns changed java resources (java source files) 
     */
    private List getChangedResource(IResourceDelta delta[])
    {

        List res = new ArrayList();

        for (int i = 0; delta.length > i; i++)
        {
            if (delta[i].getAffectedChildren().length > 0)
            {
                res.addAll(getChangedResource(delta[i].getAffectedChildren()));
            } else
            {
                res.add(delta[i].getResource());
            }
        }

        return res;
    }

}
