package org.apache.avalon.aspect;

public interface LifecycleInterceptor {
    public Object interceptCreation (Object instance);
    public Object interceptAccess (String accessor, Object instance);
}