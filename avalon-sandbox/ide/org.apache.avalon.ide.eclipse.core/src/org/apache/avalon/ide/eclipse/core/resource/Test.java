/*
 * Created on 30.01.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.avalon.ide.eclipse.core.resource;

/**
 * @author Andreas Develop
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Test
{

    public static void main(String[] args)
    {
        String str = SystemResource.replaceAll("hallo %test% gg %test% o", "%test%", "neu");
        Object obj = str;
    }
}
