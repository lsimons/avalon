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
 *  
 */
package org.apache.avalon.ide.eclipse.repository.preferences;

import org.apache.avalon.ide.eclipse.repository.RepositoryPlugin;
import org.apache.avalon.ide.repository.RepositoryTypeRegistry;
import org.apache.avalon.ide.repository.RepositorySchemeDescriptor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Niclas Hedhman, niclas@hedhman.org
 */
public class AddRepositoryDialog extends Dialog
    implements SelectionListener
{
    private String m_TextValue;
    private RepositorySchemeDescriptor m_Selected;

    private IInputValidator m_Validator;

    public AddRepositoryDialog(Shell parent, IInputValidator validator)
    {
        super(parent);
        m_Validator = validator;
    }

    protected Control createDialogArea(Composite parent)
    {
        Composite panel = (Composite) super.createDialogArea(parent);
        GridLayout layout = new GridLayout();
        panel.setLayout(layout);

        Label label = new Label(panel, SWT.WRAP);
        label.setText("Location:");
        GridData data =
            new GridData(
                GridData.GRAB_HORIZONTAL
                    | GridData.HORIZONTAL_ALIGN_FILL
                    | GridData.VERTICAL_ALIGN_END);
        data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        label.setLayoutData(data);

        Text textField = new Text(panel, SWT.SINGLE | SWT.BORDER);
        textField.setText("");
        textField.setLayoutData(
            new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
        textField.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent event )
            {
                m_TextValue = ((Text) event.getSource()).getText();
            }
        } );

        Group group = new Group(panel, SWT.SHADOW_ETCHED_IN);
        layout = new GridLayout();
        group.setLayout(layout);

        group.setText("Registered Types");
        data =
            new GridData(
                GridData.HORIZONTAL_ALIGN_FILL
                    | GridData.GRAB_VERTICAL
                    | GridData.VERTICAL_ALIGN_BEGINNING);
        group.setLayoutData(data);

        RepositoryTypeRegistry reg = RepositoryPlugin.getDefault().getRepositoryTypeRegistry();
        RepositorySchemeDescriptor[] urns = reg.getRegisteredURNs();
        if( urns.length > 0 )
        {    
            m_Selected = urns[0];
            for (int i = 0; i < urns.length; i++)
            {
                Button b1 = new Button(group, SWT.RADIO);
                GridData gd = new GridData();
                gd.grabExcessHorizontalSpace = true;
                gd.grabExcessVerticalSpace = true;
                b1.setLayoutData(gd);
                b1.setText(urns[i].getName());
                b1.setData(urns[i]);
                b1.setToolTipText(urns[i].getDescription());
                b1.addSelectionListener( this );            
                if (i == 0)
                    b1.setSelection(true);
            }
        }
        return panel;
    }

    public void widgetDefaultSelected( SelectionEvent event )
    {
        System.out.println( "widgetDefaultSelected" );
    }

    public void widgetSelected( SelectionEvent event )
    {
        m_Selected = (RepositorySchemeDescriptor) ((Button) event.getSource()).getData();
    }

    public String getValue()
    {
        return "urn:" + m_Selected.getScheme() + ":" + m_TextValue;
    }
}
