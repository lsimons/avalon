/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.excalibur.source.test;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.ConcurrentModificationException;

import junit.framework.TestCase;

import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceUtil;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.FileSource;

/**
 * Test case for FileSource.
 *
 * @author <a href="mailto:sylvain@apache.org">Sylvain Wallez</a>
 * @version $Id: FileSourceTestCase.java,v 1.5 2003/06/10 14:19:35 bloritsch Exp $
 */
public class FileSourceTestCase extends TestCase
{

    private File m_tempDir;

    public FileSourceTestCase()
    {
        this("FileSource");
    }

    public FileSourceTestCase(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        // Create a temp file
        m_tempDir = File.createTempFile("filesource", "test");
        // and make it a directory
        m_tempDir.delete();
        m_tempDir.mkdir();
    }

    public void testDirExistence() throws Exception
    {
        m_tempDir.mkdirs();
        long time = m_tempDir.lastModified();
        FileSource src = new FileSource("file", m_tempDir);
        assertTrue("Temp dir doesn't exist", src.exists());
        assertTrue("Temp dir is not traversable", src.isCollection());
        // Check it was created less than 1 secs ago
        assertEquals("Wrong creation date", time, src.getLastModified());

        assertTrue("Temp dir is not empty", src.getChildren().isEmpty());
    }

    public void testChildCreation() throws Exception
    {
        final String text = "Writing to a source";

        FileSource src = new FileSource("file", m_tempDir);

        FileSource child = (FileSource) src.getChild("child.txt");
        assertTrue("New file already exists", !child.exists());

        // Should not have a validity, since it doesn't exist
        assertNull("New file has a validity", child.getValidity());

        // Test the name
        assertEquals("Wrong name", "child.txt", child.getName());

        // Feed with some content
        fillSource(child, text);

        // And test it
        assertEquals(
            "Wrong length",
            text.length() + System.getProperty("line.separator").length(),
            child.getContentLength());
        assertEquals("Wrong content-type", "text/plain", child.getMimeType());
        assertTrue("New file is traversable", !child.isCollection());

        // Check that parent now has children
        Collection children = src.getChildren();
        assertEquals("Wrong number of children", 1, children.size());

        // And also that crawling up the hierarchy is OK
        Source parent = child.getParent();
        assertEquals("Wrong parent URI", src.getURI(), parent.getURI());

    }

    public void testMove() throws Exception
    {
        final String text = "Original text";

        FileSource src = new FileSource("file", m_tempDir);

        FileSource child = (FileSource) src.getChild("child.txt");
        assertTrue("New file already exists", !child.exists());

        fillSource(child, text);
        assertTrue("New file doesn't exist", child.exists());
        long length = child.getContentLength();

        FileSource child2 = (FileSource) src.getChild("child2.txt");
        assertTrue("Second file already exist", !child2.exists());

        SourceUtil.move(child, child2);
        assertTrue("First file still exists", !child.exists());
        assertTrue("Second file doesn't exist", child2.exists());
        assertEquals("Wrong length of second file", length, child2.getContentLength());
    }

    public void testCopy() throws Exception
    {
        final String text = "Original text";

        FileSource src = new FileSource("file", m_tempDir);

        FileSource child = (FileSource) src.getChild("child.txt");
        assertTrue("New file already exists", !child.exists());

        fillSource(child, text);
        assertTrue("New file doesn't exist", child.exists());
        long length = child.getContentLength();

        FileSource child2 = (FileSource) src.getChild("child2.txt");
        assertTrue("Second file already exist", !child2.exists());

        SourceUtil.copy(child, child2);

        assertTrue("First file doesn't exist", child.exists());
        assertTrue("Second file doesn't exist", child2.exists());
        assertEquals("Wrong length of second file", length, child2.getContentLength());

    }

    public void testDelete() throws Exception
    {
        final String text = "Original text";

        FileSource src = new FileSource("file", m_tempDir);

        FileSource child = (FileSource) src.getChild("child.txt");
        assertTrue("New file already exists", !child.exists());
        fillSource(child, text);
        assertTrue("New file doesn't exist", child.exists());

        child.delete();
        assertTrue("File still exists", !child.exists());
    }

    public void testConcurrentAccess() throws Exception
    {
        FileSource src = new FileSource("file", m_tempDir);

        FileSource child = (FileSource) src.getChild("child.txt");
        assertTrue("New file already exists", !child.exists());

        child.getOutputStream();

        try
        {
            // Get it a second time
            child.getOutputStream();
        }
        catch (ConcurrentModificationException cme)
        {
            return; // This is what is expected
        }
        fail("Undedected concurrent modification");

    }

    public void testAtomicUpdate() throws Exception
    {
        final String text = "Blah, blah";
        FileSource src = new FileSource("file", m_tempDir);

        FileSource child = (FileSource) src.getChild("child.txt");
        assertTrue("New file already exists", !child.exists());
        fillSource(child, text + " and blah!");

        long length = child.getContentLength();

        SourceValidity validity = child.getValidity();
        assertEquals("Validity is not valid", 1, validity.isValid());

        // Wait 2 seconds before updating the file
        Thread.sleep(2 * 1000L);

        // Now change its content
        PrintWriter pw = new PrintWriter(child.getOutputStream());
        pw.write(text);

        assertEquals("File length modified", length, child.getContentLength());

        pw.close();

        assertTrue("File length not modified", length != child.getContentLength());

        assertEquals("Validity is valid", -1, validity.isValid());
    }

    protected void tearDown() throws Exception
    {
        deleteAll(m_tempDir);
    }

    // Recursively delete a file or directory
    private void deleteAll(File f)
    {
        if (f.isDirectory())
        {
            File[] children = f.listFiles();
            for (int i = 0; i < children.length; i++)
            {
                deleteAll(children[i]);
            }
        }

        f.delete();
    }

    private void fillSource(ModifiableSource src, String text) throws Exception
    {
        OutputStream os = src.getOutputStream();
        PrintWriter pw = new PrintWriter(os);

        pw.println(text);
        pw.close();
    }

}
