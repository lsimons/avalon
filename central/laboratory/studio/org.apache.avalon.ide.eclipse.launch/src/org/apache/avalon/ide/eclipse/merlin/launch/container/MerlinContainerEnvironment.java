/*
 * Created on 14.02.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.avalon.ide.eclipse.merlin.launch.container;

import java.io.IOException;

import org.apache.avalon.ide.eclipse.merlin.launch.MerlinDeveloperLaunch;
import org.apache.avalon.util.defaults.DefaultsBuilder;


/**
 * @author Andreas Develop
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MerlinContainerEnvironment
{

    private DefaultsBuilder merlinBuilder;
    private DefaultsBuilder avalonBuilder;
    
    public static void main(String[] args)
    {

        try
        {
            new DefaultsBuilder("merlin", null);
            
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
    public MerlinContainerEnvironment(){
        
        try
        {
            merlinBuilder = new DefaultsBuilder("merlin", null);
            avalonBuilder = new DefaultsBuilder("avalon", null);
        } catch (Exception e)
        {
            MerlinDeveloperLaunch.log(e, "Error while reading the Avalon environment");
        }
        
    }
    /**
     * @return
     */
    public String getAvalonHome()
    {
        String path; 
        try
        {
            path = avalonBuilder.getHomeDirectory().getCanonicalPath();
        } catch (IOException e)
        {
            MerlinDeveloperLaunch.log(e, "Error while reading the Avalon Home Directory");
            return null;
        }
        return path;
    }
    /**
     * @return
     */
    public String getMerlinHome()
    {
        String path; 
        try
        {
            path = merlinBuilder.getHomeDirectory().getCanonicalPath();
        } catch (IOException e)
        {
            MerlinDeveloperLaunch.log(e, "Error while reading the Merlin Home Directory");
            return null;
        }
        return path;
    }
    /**
     * 
     */
    public void setAvalonDefaultsHome()
    {
    }
    /**
     * 
     */
    public void setMerlinDefaultsHome()
    {
        // System.setProperty("merlin.home", "");
        
    }
    
}
