package org.apache.avalon.aspect;

import java.util.Iterator;

public interface Kernel {
    void addAspect (String name, Aspect aspect);
    Aspect getAspect (String name);
    String[] getAspects ();
    
    void addHandler (String name, Handler handler);
    Handler getHandler (String name);
    String[] getHandlers ();
    
    void   registerEventListener (Class eventListenerClass, Object listener);
    void   unregisterEventListener (Class eventListenerClass, Object listener);
    Iterator getEventListeners (Class eventListenerClass);
}