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
package org.apache.avalon.ide.eclipse.merlin.ui.tools;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.avalon.ide.eclipse.merlin.ui.MerlinDeveloperUI;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IJavadocCompletionProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;

/**
 * @author Andreas Develop
 *
 */
public class JavadocProcessor implements IJavadocCompletionProcessor
{

    /* (non-Javadoc)
     * @see org.eclipse.jdt.ui.text.java.IJavadocCompletionProcessor#computeContextInformation(org.eclipse.jdt.core.ICompilationUnit, int)
     */
    public IContextInformation[] computeContextInformation(ICompilationUnit cUnit, int offset)
    {
        // 
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.ui.text.java.IJavadocCompletionProcessor#computeCompletionProposals(org.eclipse.jdt.core.ICompilationUnit, int, int, int)
     */
    public IJavaCompletionProposal[] computeCompletionProposals(
        ICompilationUnit cUnit,
        int offset,
        int length,
        int flags)
    {
        // TODO Auto-generated method stub
        List props = new ArrayList();

        ResourceBundle bundle = MerlinDeveloperUI.getDefault().getResourceBundle();
        Enumeration enum = bundle.getKeys();
        while (enum.hasMoreElements())
        {
            String key = (String) enum.nextElement();
            if (key.startsWith("javadoc.avalon") && (! key.endsWith(".tag")))
            {
                AvalonProposals prop = new AvalonProposals(offset, length);
                prop.setKey("@" + key.substring(8,key.length()));
                prop.setDescription(MerlinDeveloperUI.getResourceString(key));
                prop.setTag(MerlinDeveloperUI.getResourceString(key + ".tag"));
                props.add(prop);
            }
        }
        return (IJavaCompletionProposal[]) props.toArray(new AvalonProposals[props.size()]);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.ui.text.java.IJavadocCompletionProcessor#getErrorMessage()
     */
    public String getErrorMessage()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /**
     * 
     */
    public JavadocProcessor()
    {
        super();
        // TODO Auto-generated constructor stub
    }

}
