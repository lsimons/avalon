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

package org.apache.avalon.ide.eclipse.merlin.launch;

import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>*
 *         The main plugin class to be used in the desktop.
 */
public class MerlinDeveloperLaunch extends AbstractUIPlugin
{
    //The shared instance.
    private static MerlinDeveloperLaunch plugin;
    //Resource bundle.
    private ResourceBundle resourceBundle;

    public static final String PLUGIN_ID = "org.apache.avalon.MerlinDeveloperLaunch";
    public static final String MERLIN_PROJECT_NATURE_ID =
        PLUGIN_ID + ".merlinProjectNature";
    public static final String MERLIN_PROJECT_CONFIG_NATURE_ID =
        PLUGIN_ID + ".merlinConfigNature";
    public static final String MERLIN_BUILDER_ID =
    PLUGIN_ID + ".merlinBuilder";
    public static final String ID_MERLIN_CONTAINER = 
        MerlinDeveloperLaunch.PLUGIN_ID + ".merlinLaunchConfigurationDelegate"; //$NON-NLS-1$
    public static final String ATTR_MERLIN_CONTAINER_ID = "merlinContainerID"; //$NON-NLS-1$
    /**
	 * The constructor.
	 */
    public MerlinDeveloperLaunch(IPluginDescriptor descriptor)
    {
        super(descriptor);
        plugin = this;
        try
        {
            // activate EnterpriseDeveloper if present
            Platform.getPlugin("biz.softwarefabrik.j4ee.core");
            
            resourceBundle =
                ResourceBundle.getBundle(
                    "org.apache.avalon.ide.eclipse.merlin.launch.MerlinDeveloperLaunchResources");
        } catch (MissingResourceException x)
        {
            resourceBundle = null;
        }
    }

    /**
	 * Returns the shared instance.
	 */
    public static MerlinDeveloperLaunch getDefault()
    {
        return plugin;
    }

    /**
	 * Returns the workspace instance.
	 */
    public static IWorkspace getWorkspace()
    {
        return ResourcesPlugin.getWorkspace();
    }

    /**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
    public static String getResourceString(String key)
    {
        ResourceBundle bundle = MerlinDeveloperLaunch.getDefault().getResourceBundle();
        try
        {
            return (bundle != null ? bundle.getString(key) : key);
        } catch (MissingResourceException e)
        {
            return key;
        }
    }

    /**
	 * Returns the plugin's resource bundle,
	 */
    public ResourceBundle getResourceBundle()
    {
        return resourceBundle;
    }

    public static void log(IStatus status)
    {
        getDefault().getLog().log(status);
    }

    public static void log(Throwable e, String message)
    {
        boolean isDebugging = true; // change to false for production
        IStatus status =
            new Status(
                IStatus.ERROR,
                getDefault().getDescriptor().getUniqueIdentifier(),
                IStatus.ERROR,
                message,
                e);

        log(status);
        if (isDebugging)
        {
            System.out.println(message + ": " + e.getMessage()); //$NON-NLS-1$
        }
    }

    public IPath getPluginLocation()
    {
        return getPluginLocation(MerlinDeveloperLaunch.PLUGIN_ID);
    }

    public IPath getPluginLocation(String pluginId)
    {
        try
        {
            URL installURL =
                Platform.getPluginRegistry().getPluginDescriptor(pluginId).getInstallURL();
            return new Path(Platform.resolve(installURL).getFile());
        } catch (Exception e)
        {
            log(e, "getPluginLocation() handling Exception"); //$NON-NLS-1$
            return null;
        }
    }
}
