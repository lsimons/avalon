package org.apache.avalon.attributes;

import java.util.Set;
import java.util.Map;

/**
 * Interface implemented by all attribute repository classes.
 * This interface is used internally and should not be used
 * by clients. The only reason it is public is because the
 * classes implementing it may be in any package.
 */
public interface AttributeRepositoryClass {
    public Set getClassAttributes ();
    public Map getFieldAttributes ();
    public Map getMethodAttributes ();
    public Map getConstructorAttributes ();
}