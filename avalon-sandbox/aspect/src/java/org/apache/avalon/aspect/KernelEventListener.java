package org.apache.avalon.aspect;

public interface KernelEventListener {
    public void handlerAdded (String key, Handler handler);
    public void aspectAdded (String key, Aspect aspect);
}