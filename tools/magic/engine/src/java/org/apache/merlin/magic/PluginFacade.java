package org.apache.merlin.magic;

public interface PluginFacade
{
    /** Returns the Plugin instance of that this PluginFacade refers to.
     */
    Plugin resolve() throws Exception;

    /** Invalidates the plugin, and any created instance should be 
     *  recreated.
     **/    
    void invalidate();
    
    /** Returns the context for the Plugin.
     */
    PluginContext getPluginContext();
    
    /** Return the Classname of the Plugin represented by the Facade.
     **/
    String getPluginClassname();
} 
