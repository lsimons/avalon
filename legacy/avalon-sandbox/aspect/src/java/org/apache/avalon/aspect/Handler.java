package org.apache.avalon.aspect;

public interface Handler {
    public Object get (String accessor);
    public void release (String accessor, Object o);
}