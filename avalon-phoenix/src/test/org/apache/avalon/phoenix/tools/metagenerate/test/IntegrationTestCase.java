/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.tools.metagenerate.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.LineNumberReader;
import junit.framework.TestCase;

public class IntegrationTestCase extends TestCase
{
    public IntegrationTestCase(String name)
    {
        super(name);
    }

    public void testBlockInfoOutput() throws Exception
    {

        String fileName
                = "org/apache/avalon/phoenix/tools/metagenerate/test/TestBlock.xinfo";
        fileName.replace('\\',File.separatorChar);
        fileName.replace('/',File.separatorChar);

        LineNumberReader reader = null;
        try
        {
            reader = new LineNumberReader(new FileReader(fileName));
        }
        catch (FileNotFoundException e)
        {
            fail("The generated xinfo file is missing");
        }
        String line = reader.readLine();
        int ix =0;
        while (line != null)
        {
            assertEquals("Line not expected", XINFO[ix].trim(), line.trim());
            ix++;
            line = reader.readLine();
        }
    }

    public void testNonBlockInfoOutput() throws Exception
    {
        String fileName
                = "org/apache/avalon/phoenix/tools/metagenerate/test/TestNonBlock.xinfo";
        fileName.replace('\\',File.separatorChar);
        fileName.replace('/',File.separatorChar);

        try
        {
            new LineNumberReader(new FileReader(fileName));
            fail("Non Block should not generate an xinfo file");
        }
        catch (FileNotFoundException e)
        {
            // expected.
        }

    }

    public void testMBeanOutput() throws Exception
    {

        String fileName
                = "org/apache/avalon/phoenix/tools/metagenerate/test/TestMBean.mxinfo";
        fileName.replace('\\',File.separatorChar);
        fileName.replace('/',File.separatorChar);

        LineNumberReader reader = null;
        try
        {
            reader = new LineNumberReader(new FileReader(fileName));
        }
        catch (FileNotFoundException e)
        {
            fail("The generated mxinfo file was missing");
        }
        String line = reader.readLine();
        int ix =0;
        while (line != null)
        {
            assertEquals("Line not expected", MXINFO[ix].trim(), line.trim());
            ix++;
            line = reader.readLine();
        }
    }


    private static final String XINFO[] = new String[] {
    "    <?xml version=\"1.0\"?>",
    "    <!DOCTYPE blockinfo PUBLIC \"-//PHOENIX/Block Info DTD Version 1.0//EN\"",
    "                      \"http://jakarta.apache.org/avalon/dtds/phoenix/blockinfo_1_0.dtd\">",
    "",
    "    <blockinfo>",
    "",
    "      <!-- section to describe block -->",
    "      <block>",
    "        <version>1.0</version>",
    "      </block>",
    "",
    "      <!-- services that are offered by this block -->",
    "      <services>",
    "        <service name=\"blah.BlahService\"/>",
    "      </services>",
    "",
    "      <!-- interfaces that may be exported to manange this block -->",
    "      <management-access-points>",
    "        <service name=\"YeeeHaaa\"/>",
    "      </management-access-points>",
    "",
    "      <!-- services that are required by this block -->",
    "      <dependencies>",
    "        <dependency>",
    "          <service name=\"blah.OtherBlahService\"/>",
    "        </dependency>",
    "      </dependencies>",
    "    </blockinfo>" };

    private static final String MXINFO[] = new String[] {
    "<?xml version=\"1.0\"?>",
    "<!DOCTYPE mxinfo PUBLIC \"-//PHOENIX/Mx Info DTD Version 1.0//EN\"",
    "                  \"http://jakarta.apache.org/avalon/dtds/phoenix/mxinfo_1_0.dtd\">",
    "",
    "<mxinfo>",
    "",
    "    <topic name=\"Greeting\" >",
    "",
    "      <!-- attributes -->",
    "      <attribute",
    "        name=\"greeting\"",
    "        description=\"The greeting that is returned to each HTTP request\"",
    "        type=\"void\"",
    "      />",
    "",
    "      <!-- operations -->",
    "",
    "      <operation",
    "        name=\"someOperation\"",
    "        description=\"Blah Blah Blah Blah.\"",
    "        type=\"java.lang.String\">",
    "        <param",
    "          name=\"parm1\"",
    "          description=\"parameter one\"",
    "          type=\"java.lang.String\"",
    "          />",
    "        <param",
    "          name=\"parm2\"",
    "          description=\"parameter two\"",
    "          type=\"java.lang.String\"",
    "          />",
    "      </operation>",
    "",
    "    </topic>",
    "",
    "</mxinfo>" };
}
