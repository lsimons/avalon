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
package org.apache.avalon.ide.eclipse.merlin.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a> 
 * The main plugin class to be used in the desktop.
 */
public class MerlinDeveloperUI extends AbstractUIPlugin
{
    //The shared instance.
    private static MerlinDeveloperUI plugin;
    //Resource bundle.
    private ResourceBundle resourceBundle;
    public static final String PLUGIN_ID = "org.apache.avalon.MerlinDeveloperUI";
    /**
	 * The constructor.
	 */
    public MerlinDeveloperUI(IPluginDescriptor descriptor)
    {
        super(descriptor);
        plugin = this;
        try
        {
            resourceBundle =
                ResourceBundle.getBundle(
                    "org.apache.avalon.ide.eclipse.merlin.ui.MerlinDeveloperUIResources");
        } catch (MissingResourceException x)
        {
            resourceBundle = null;
        }
    }

    /**
	 * Returns the shared instance.
	 */
    public static MerlinDeveloperUI getDefault()
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
        ResourceBundle bundle = MerlinDeveloperUI.getDefault().getResourceBundle();
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

    public static ImageDescriptor getImageDescriptor(String path)
    {
        try
        {
            URL prefix = MerlinDeveloperUI.getDefault().getDescriptor().getInstallURL();
            return ImageDescriptor.createFromURL(new URL(prefix, path));
        } catch (MalformedURLException e)
        {
            log(e, "getResourceBundle() handling MalformedURLException"); //$NON-NLS-1$
            return null;
        }
    }
}
