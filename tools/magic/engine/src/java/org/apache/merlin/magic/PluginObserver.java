package org.apache.merlin.magic;

public interface PluginObserver
{
    void preMethod( Plugin source, String method );
    
    void postMethod( Plugin source, String method );

    void step( Plugin source, String method, int stepIndex );
} 
