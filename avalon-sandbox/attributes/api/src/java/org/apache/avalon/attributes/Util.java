package org.apache.avalon.attributes;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

class Util {
    
    public static String getSignature (Method m) {
        return m.getName () + "(" + getParameterList (m.getParameterTypes ()) + ")";
    }
    
    public static String getSignature (Constructor c) {
        return "(" + getParameterList (c.getParameterTypes ()) + ")";
    }
    
    public static String decodedClassName (String rawName) throws IllegalArgumentException {
        if (!rawName.startsWith ("[")) {
            return rawName;
        } else {
            StringBuffer nesting = new StringBuffer ();
            int i = 0;
            while (rawName.charAt (i) == '[') {
                nesting.append ("[]");
                i++;
            }
            String type = "";
            switch (rawName.charAt (i)) {
            case 'B': type = "byte"; break;
            case 'C': type = "char"; break;
            case 'D': type = "double"; break;
            case 'F': type = "float"; break;
            case 'I': type = "int"; break;
            case 'J': type = "long"; break;
            case 'L': type = rawName.substring (i + 1, rawName.length () - 1); break;
            case 'S': type = "short"; break;
            case 'Z': type = "boolean"; break;
            default: throw new IllegalArgumentException ("Can't decode " + rawName);
            } 
            
            return type + nesting.toString ();
        }
    }
    
    public static String getParameterList (Class[] params) {
        StringBuffer sb = new StringBuffer ();
        for (int i = 0; i < params.length; i++) {
            if (i > 0) {
                sb.append (",");
            }
            sb.append (decodedClassName (params[i].getName ()));                
        }
        return sb.toString ();
    }
}