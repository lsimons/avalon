package org.apache.avalon.aspect;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DefaultKernel implements Kernel {
    
    private final EventBus eventBus = new EventBus ();
    
    private final Map aspects = new HashMap ();
    private final Map handlers = new HashMap ();
    
    public void addAspect (String name, Aspect aspect) {
        aspect.initAspect(this);
        aspects.put(name, aspect);
        Iterator iter = getEventListeners (KernelEventListener.class);
        while (iter.hasNext ()) {
            ((KernelEventListener) iter.next ()).aspectAdded (name, aspect);
        }
    }
    
    public Aspect getAspect (String name) {
        return (Aspect) aspects.get (name);
    }
    
    public String[] getAspects () {
        return (String[]) aspects.keySet ().toArray(new String[0]);
    }
    
    public void addHandler (String name, Handler handler) {
        handlers.put (name, handler);
        Iterator iter = getEventListeners (KernelEventListener.class);
        while (iter.hasNext ()) {
            ((KernelEventListener) iter.next ()).handlerAdded (name, handler);
        }        
    }

    public Handler getHandler (String name) {
        return (Handler) handlers.get (name);
    }
    
    public String[] getHandlers () {
        return (String[]) handlers.keySet ().toArray(new String[0]);
    }
    
    public void registerEventListener (Class eventListenerClass, Object listener) {
        eventBus.registerEventListener (eventListenerClass, listener);
    }
    
    public void unregisterEventListener (Class eventListenerClass, Object listener) {
        eventBus.unregisterEventListener (eventListenerClass, listener);
    }
    
    public Iterator getEventListeners (Class eventListenerClass) {
        return eventBus.getEventListeners (eventListenerClass);
    }
}