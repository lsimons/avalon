package org.apache.avalon.aspect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class EventBus {
    
    private final Map listeners = new HashMap ();
    private final List EMPTY_LIST = new ArrayList ();
    
    public void registerEventListener (Class eventListenerClass, Object listener) {
        synchronized (this) {
            List li = (List) listeners.get (eventListenerClass);
            List newList = null;
            if (li != null) {
                newList = new ArrayList (li);
            } else {
                newList = new ArrayList ();
            }
            newList.add (listener);
            
            listeners.put (eventListenerClass, newList);
        }
    }
    
    public void unregisterEventListener (Class eventListenerClass, Object listener) {
        synchronized (this) {
            List li = (List) listeners.get (eventListenerClass);
            List newList = null;
            if (li != null) {
                newList = new ArrayList (li);
            } else {
                newList = new ArrayList ();
            }
            newList.remove (listener);
            if (newList.size () == 0) {
                listeners.remove (eventListenerClass);
            } else {
                listeners.put (eventListenerClass, newList);
            }
        }
    }
    
    public Iterator getEventListeners (Class eventListenerClass) {
        synchronized (this) {
            List li = (List) listeners.get (eventListenerClass);
            if (li != null) {
                return li.iterator ();
            } else {
                return EMPTY_LIST.iterator ();
            }
        }
    }
}