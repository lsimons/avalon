/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.avalon.composition.data.test;

import junit.framework.TestCase;
import org.apache.avalon.composition.data.CategoryDirective;

import java.io.*;

/**
 * CategoryTestCase does XYZ
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $ Revision: 1.1 $
 */
public class CategoryDirectiveTestCase extends TestCase
{
    public CategoryDirectiveTestCase( String name )
    {
        super( name );
    }

    public void testCategory()
    {
        String catName = "name";
        CategoryDirective cat = new CategoryDirective(catName);

        testCategory( cat, catName, null, null );
    }

    public void testLogPriority()
    {
        String catName = "name";
        String priority = CategoryDirective.DEBUG;
        CategoryDirective cat = new CategoryDirective( catName, priority);

        testCategory( cat, catName, priority, null );

        priority = CategoryDirective.ERROR;
        cat = new CategoryDirective( catName, priority );

        testCategory( cat, catName, priority, null );

        priority = CategoryDirective.INFO;
        cat = new CategoryDirective( catName, priority );

        testCategory( cat, catName, priority, null );

        priority = CategoryDirective.WARN;
        cat = new CategoryDirective( catName, priority );

        testCategory( cat, catName, priority, null );
    }

    public void testLogTarget()
    {
        String name = "name";
        String priority = CategoryDirective.DEBUG;
        String target = "test";
        CategoryDirective cat = new CategoryDirective( name, priority, target);

        testCategory( cat, name, priority, target );
    }

    private void testCategory( CategoryDirective cat, String name, String priority, String target )
    {
        assertEquals( name, cat.getName() );
        assertEquals( priority, cat.getPriority() );
        assertEquals( target, cat.getTarget() );
    }

    public void testSerialization() throws IOException, ClassNotFoundException
    {
        File file = new File("name.test");
        String name = "name";
        String priority = CategoryDirective.WARN;
        String target = "test";

        CategoryDirective original = new CategoryDirective( name, priority, target );

        testCategory( original, name, priority, target );

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(original);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream( new FileInputStream(file));
        CategoryDirective serialized = (CategoryDirective)ois.readObject();
        ois.close();

        file.delete();

        testCategory( serialized, name, priority, target );

        assertEquals( original, serialized );
        assertEquals( original.hashCode(), serialized.hashCode() );
    }
}