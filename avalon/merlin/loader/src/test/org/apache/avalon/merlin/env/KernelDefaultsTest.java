/*
 * $Id: KernelDefaultsTest.java,v 1.2 2003/11/03 07:02:51 akarasulu Exp $
 *
 * -- (c) LDAPd Group                                                    --
 * -- Please refer to the LICENSE.txt file in the root directory of      --
 * -- any LDAPd project for copyright and distribution information.      --
 *
 * Created on Oct 27, 2003
 */
package org.apache.avalon.merlin.env;

import org.apache.avalon.merlin.kernel.KernelDefaults;

import junit.framework.TestCase;


/**
 * @todo
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: akarasulu $
 * @version $Revision: 1.2 $
 */
public class KernelDefaultsTest extends TestCase
{

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(KernelDefaultsTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Constructor for KernelDefaultsTest.
     * @param arg0
     */
    public KernelDefaultsTest(String arg0)
    {
        super(arg0);
    }

    final public void testGetTempPath()
    {
        //assertEquals( KernelDefaults.getTempPath(), 
        //        System.getProperty( "java.io.tmpdir" ) ) ;
    }

}
