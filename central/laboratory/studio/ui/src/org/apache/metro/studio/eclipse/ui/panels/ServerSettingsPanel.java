/*
 * Created on 17.08.2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.metro.studio.eclipse.ui.panels;

import org.apache.metro.facility.presentationservice.api.IModelChannel;
import org.apache.metro.facility.presentationservice.api.ChannelException;

import org.apache.metro.facility.presentationservice.impl.ModelChannel;

import org.apache.metro.studio.eclipse.ui.common.CommonDialogs;
import org.apache.metro.studio.eclipse.ui.common.ModelObject;
import org.apache.metro.studio.eclipse.ui.controller.PreferencesController;

import org.eclipse.swt.SWT;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author EH2OBCK
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */

public class ServerSettingsPanel 
{

    public static final String SERVER_PANEL = "server.panel";
    public static final String SERVER_APPLY = "server.apply";
    public static final String SERVER_DEFAULT = "server.default";
    
    public static final String SERVER_STARTUP_JAR = "server.startup.jar";
    public static final String SERVER_ROOT_FACILITY = "server.facility";
    public static final String SERVER_ROOT_BLOCK = "server.block";
    public static final String SERVER_LANG = "server.lang";
    public static final String SERVER_TIMEOUT = "server.timeout";
    public static final String SERVER_DEBUG = "server.debug";
    public static final String SERVER_DEPLOY = "server.deploy";
    public static final String SERVER_EXECUTE = "server.execute";
    public static final String SERVER_ASSAMBLY = "server.assambly";
    public static final String SERVER_SECURITY = "server.security";

    private Text deploymentTimeout;
    private Text language;
    private Text rootBlock;
    private Text rootFacility;
    private Text serverLib;

    private Shell shell;
    private IModelChannel model;
    
    /**
     *  
     */
    public ServerSettingsPanel() 
    {
        super();
    }
    
    public void initializeControls(ModelObject cModel)
    {
        try
        {
            // initialize the controller
            new PreferencesController().initialize();
            model = new ModelChannel("server");
            model.windowCreated(SERVER_PANEL);
            showControls();
        } catch (ChannelException e)
        {
            e.printStackTrace();
        }
    }

    public void performDefaults()
    {
        try
        {
            model.controlClicked(SERVER_DEFAULT);
            showControls();
        } catch (ChannelException e)
        {
            e.printStackTrace();
        }
    }

    public void performApply()
    {
        try
        {
            storeControls();
            model.controlClicked(SERVER_APPLY);
        } catch (ChannelException e)
        {
            e.printStackTrace();
        }
    }
    
    private void showControls()
    {
        deploymentTimeout.setText(model.getValue(SERVER_DEPLOY));
        language.setText(model.getValue(SERVER_LANG));
        rootBlock.setText(model.getValue(SERVER_ROOT_BLOCK));
        rootFacility.setText(model.getValue(SERVER_ROOT_FACILITY));
        serverLib.setText(model.getValue(SERVER_STARTUP_JAR));
    }
    
    private void storeControls()
    {
        model.putValue(SERVER_DEPLOY, deploymentTimeout.getText());
        model.putValue(SERVER_LANG, language.getText());
        model.putValue(SERVER_ROOT_BLOCK, rootBlock.getText());
        model.putValue(SERVER_ROOT_FACILITY, rootFacility.getText());
        model.putValue(SERVER_STARTUP_JAR, serverLib.getText());
    }
    
    public Control createControls(Composite parent) {
        shell = parent.getShell();
        
        Composite contents = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        contents.setLayout(gridLayout);
        contents.setLayoutData(new GridData(GridData.FILL_BOTH));
        {
            final Label label = new Label(contents, SWT.NONE);
            label.setText("Server &Lib (*.jar):");
        }
        {
            serverLib = new Text(contents, SWT.BORDER);
            serverLib.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        }
        {
            final Button button = new Button(contents, SWT.NONE);
            button.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    serverLib.setText(CommonDialogs.getFilePath(shell));
                }
            });
            button.setText("...");
        }
        {
            final Label label = new Label(contents, SWT.NONE);
            label.setText("Root &Facility:");
        }
        {
            rootFacility = new Text(contents, SWT.BORDER);
            rootFacility.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        }
        {
            final Button button = new Button(contents, SWT.NONE);
            button.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    rootFacility.setText(CommonDialogs.getFilePath(shell));
                }
            });
            button.setText("...");
        }
        {
            final Label label = new Label(contents, SWT.NONE);
            label.setText("Root &Block:");
        }
        {
            rootBlock = new Text(contents, SWT.BORDER);
            rootBlock.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        }
        {
            final Button button = new Button(contents, SWT.NONE);
            button.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) 
                {
                    rootBlock.setText(CommonDialogs.getFilePath(shell));
                }
            });
            button.setText("...");
        }
        {
            final Label label = new Label(contents, SWT.NONE);
            label.setText("Language:");
        }
        {
            language = new Text(contents, SWT.BORDER);
            final GridData gridData = new GridData();
            gridData.horizontalSpan = 2;
            gridData.widthHint = 150;
            language.setLayoutData(gridData);
        }
        {
            final Label label = new Label(contents, SWT.NONE);
            label.setText("Deployment Timeout:");
        }
        {
            deploymentTimeout = new Text(contents, SWT.BORDER);
            final GridData gridData = new GridData();
            gridData.horizontalSpan = 2;
            gridData.widthHint = 150;            
            deploymentTimeout.setLayoutData(gridData);
        }
        {
            final Label label = new Label(contents, SWT.NONE);
        }
        {
            final Button button = new Button(contents, SWT.CHECK);
            final GridData gridData = new GridData();
            gridData.horizontalSpan = 2;
            button.setLayoutData(gridData);
            button.setText("Show debug messages");
        }
        {
            final Label label = new Label(contents, SWT.NONE);
        }
        {
            final Button button = new Button(contents, SWT.CHECK);
            final GridData gridData = new GridData();
            gridData.horizontalSpan = 2;
            button.setLayoutData(gridData);
            button.setText("Show deployment parameters");
        }
        {
            final Label label = new Label(contents, SWT.NONE);
        }
        {
            final Button button = new Button(contents, SWT.CHECK);
            final GridData gridData = new GridData();
            gridData.horizontalSpan = 2;
            button.setLayoutData(gridData);
            button.setText("Start in server mode");
        }
        {
            final Label label = new Label(contents, SWT.NONE);
        }
        {
            final Button button = new Button(contents, SWT.CHECK);
            final GridData gridData = new GridData();
            gridData.horizontalSpan = 2;
            button.setLayoutData(gridData);
            button.setText("Show assambly model");
        }
        {
            final Label label = new Label(contents, SWT.NONE);
        }
        {
            final Button button = new Button(contents, SWT.CHECK);
            final GridData gridData = new GridData();
            gridData.horizontalSpan = 2;
            button.setLayoutData(gridData);
            button.setText("Enable code security:");
        }

        return contents;
    }

}
