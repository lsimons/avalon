/*
 * 
 * ============================================================================
 * The Apache Software License, Version 1.1
 * ============================================================================
 * 
 * Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowledgment: "This product includes software developed by the Apache
 * Software Foundation (http://www.apache.org/)." Alternately, this
 * acknowledgment may appear in the software itself, if and wherever such
 * third-party acknowledgments normally appear. 4. The names "Jakarta", "Apache
 * Avalon", "Avalon Framework" and "Apache Software Foundation" must not be
 * used to endorse or promote products derived from this software without prior
 * written permission. For written permission, please contact
 * apache@apache.org. 5. Products derived from this software may not be called
 * "Apache", nor may "Apache" appear in their name, without prior written
 * permission of the Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the Apache Software Foundation. For more information on the
 * Apache Software Foundation, please see <http://www.apache.org/> .
 */
package org.apache.avalon.ide.eclipse.merlin.wizards;

import org.apache.avalon.ide.eclipse.core.xmlmodel.ProjectModel;
import org.apache.avalon.ide.eclipse.merlin.ui.MerlinDeveloperUI;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.pde.internal.ui.elements.ElementLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>*
 */
public class WizardUtil
{

    /**
	 * This class is used as Labelprovider in SelectionPage
	 *  
	 */
    static class TableLabelProvider extends ElementLabelProvider implements ITableLabelProvider
    {

        // Names of images used to represent checkboxes
        public static final String CHECKED_IMAGE = "checked";
        public static final String UNCHECKED_IMAGE = "unchecked";
        public static final String MERLIN_IMAGE = "merlin";

        // For the checkbox images
        private static ImageRegistry imageRegistry = new ImageRegistry();

        /**
		 * Note: An image registry owns all of the image objects registered
		 * with it, and automatically disposes of them the SWT Display is
		 * disposed.
		 */
        static {
            imageRegistry.put(
                CHECKED_IMAGE,
                MerlinDeveloperUI.getImageDescriptor("icons/" + CHECKED_IMAGE + ".gif"));
            imageRegistry.put(
                UNCHECKED_IMAGE,
                MerlinDeveloperUI.getImageDescriptor("icons/" + UNCHECKED_IMAGE + ".gif"));
            imageRegistry.put(
                MERLIN_IMAGE,
                MerlinDeveloperUI.getImageDescriptor("icons/" + MERLIN_IMAGE + ".gif"));
            
        }

        /**
		 * Returns the image with the given key, or <code>null</code> if not
		 * found.
		 */
        private Image getImage(boolean isSelected)
        {
            String key = isSelected ? CHECKED_IMAGE : UNCHECKED_IMAGE;
            return imageRegistry.get(key);
        }

        /**
		 * show the text label
		 * 
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
		 *      int)
		 */
        public String getColumnText(Object o, int index)
        {
            String result = "";
            switch (index)
            {
                case 0 : // COMPLETED_COLUMN
                    result = "";
                    break;
                case 1 : // merlin icon
                    result = "";
                    break;
                case 2 : // merlin icon
                    result = ((ProjectModel) o).getLabel();
                    break;
                default :
            }
            return result;
        }
        /**
		 * show the icon
		 * 
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
		 *      int)
		 */
        public Image getColumnImage(Object element, int columnIndex)
        {
            Image result = null;
            switch (columnIndex)
            {
                case 0 : // COMPLETED_COLUMN
                    result = getImage(((ProjectModel) element).isSelected());
                    break;
                case 1 : // merlin icon
                    result = imageRegistry.get(MERLIN_IMAGE);
                    break;
                case 2:
                    result = null;
                    break;
                default :
            }
            return result;
        }

    }
    public static class SelectionPageCellModifier implements ICellModifier
    {
        private NewMerlinProjectSelectionPage selectionPage;
        private String[] columnNames;

        /**
		 * Constructor
		 * 
		 * @param selectionPage
		 *            an instance of a selectionPage
		 */
        public SelectionPageCellModifier(NewMerlinProjectSelectionPage tableViewer)
        {
            super();
            this.selectionPage = tableViewer;
        }

        /**
		 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object,
		 *      java.lang.String)
		 */
        public boolean canModify(Object element, String property)
        {
            return true;
        }

        /**
		 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object,
		 *      java.lang.String)
		 */
        public Object getValue(Object element, String property)
        {

            // Find the index of the column
            int columnIndex = 0;

            Object result = null;
            ProjectModel task = (ProjectModel) element;

            switch (columnIndex)
            {
                case 0 : // COMPLETED_COLUMN
                    result = new Boolean(task.isSelected());
                    break;
                default :
                    result = "";
            }
            return result;
        }

        /**
		 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object,
		 *      java.lang.String, java.lang.Object)
		 */
        public void modify(Object element, String property, Object value)
        {

            // Find the index of the column
            int columnIndex = 0;

            TableItem item = (TableItem) element;
            ProjectModel task = (ProjectModel) item.getData();

            switch (columnIndex)
            {
                case 0 : // COMPLETED_COLUMN
                    task.setSelected(((Boolean) value).booleanValue());
                    break;
                default :
                    }
            selectionPage.updateTable(task);
        }
    }
    public static final ILabelProvider TABLE_LABEL_PROVIDER = new TableLabelProvider();

    public WizardUtil()
    {
        super();
    }
}
