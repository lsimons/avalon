package org.apache.avalon.ide.repository.testrepo;

import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.resources.*;
import java.util.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class TestrepoPlugin extends AbstractUIPlugin
{
    //The shared instance.
    private static TestrepoPlugin plugin;
    //Resource bundle.
    private ResourceBundle resourceBundle;

    /**
	 * The constructor.
	 */
    public TestrepoPlugin(IPluginDescriptor descriptor)
    {
        super(descriptor);
        plugin = this;
        try
        {
            resourceBundle =
                ResourceBundle.getBundle(
                    "org.apache.avalon.ide.repository.testrepo.TestrepoPluginResources");
        } catch (MissingResourceException x)
        {
            resourceBundle = null;
        }
    }

    /**
	 * Returns the shared instance.
	 */
    public static TestrepoPlugin getDefault()
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
        ResourceBundle bundle = TestrepoPlugin.getDefault().getResourceBundle();
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
}
