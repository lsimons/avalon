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
import org.apache.avalon.composition.data.Parameter;
import org.apache.avalon.composition.data.MetaDataException;

/**
 * ParameterTestCase does XYZ
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $ Revision: 1.1 $
 */
public class ParameterTestCase extends TestCase
{
    public ParameterTestCase( String name )
    {
        super( name );
    }

    public void testParameters() throws MetaDataException
    {
        String className = ParameterTestCase.class.getName();
        String value = "val";
        Parameter[] params = new Parameter[] {
            new Parameter( "java.io.File", value ),
            new Parameter( ParameterTestCase.class.getName(), value )
        };

        try
        {
            new Parameter(null);
            // Success!!
        }
        catch (NullPointerException npe)
        {
            fail("Null signifes a null argument.");
        }

        try
        {
            new Parameter( null, value );
            fail( "Null classname must throw a NullPointerException" );
        }
        catch ( NullPointerException npe )
        {
            // Success!!
        }

        try
        {
            new Parameter( className, (String)null );
            // Success!!
        }
        catch ( NullPointerException npe )
        {
            fail( "Null argument is a valid argument." );
        }

        try
        {
            new Parameter( null, params );
            fail( "NullPointerException must be thrown for a null classname." );
        }
        catch ( NullPointerException npe )
        {
            // Success!!
        }

        try
        {
            new Parameter( className, (Parameter[]) null );
            fail( "NullPointerException must be thorwn for null parameters." );
        }
        catch ( NullPointerException npe )
        {
            // Success!!
        }

        Parameter param = new Parameter( value );
        assertEquals( String.class.getName(), param.getClassname());

        param = new Parameter( className, params );
        assertEquals( className, param.getClassname() );
        assertEquals( params.length, param.getParameters().length );
        assertEquals( params.length, param.getParameters().length );
        assertEquals( "java.io.File", param.getParameters()[0].getClassname() );
        assertEquals( ParameterTestCase.class.getName(), param.getParameters()[1].getClassname() );

    }
}