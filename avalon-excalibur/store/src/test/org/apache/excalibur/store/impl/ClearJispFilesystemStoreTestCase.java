/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 2002-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

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

package org.apache.excalibur.store.impl;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.NullLogger;
import org.apache.avalon.framework.parameters.Parameters;

/**
 * This TestCase fills a Jisp store with <code>MAX_ENTRIES</code>
 * and then clears it.
 * The test is sucsessful if we have 0 entries after cleaning the store.
 *  
 * @author Charles Borges, charlesborges_dev at yahoo.fr
 */
public class ClearJispFilesystemStoreTestCase extends TestCase {

    /** permanent Jisp store */
    private JispFilesystemStore m_store;

    /** logger for this test */
    private final Logger m_logger =
        new ConsoleLogger(ConsoleLogger.LEVEL_DEBUG);

    /** max entries to fill the store */
    private int MAX_ENTRIES = 100;

    /** temp dir for this test */
    private File m_tempDir;

    /**
     * TestCase Constructor
     * @param methodName
     */
    public ClearJispFilesystemStoreTestCase(String methodName) {
        super(methodName);
    }

    /**
     * Set up the the jisp database to the clear test.
     */
    public void setUp() throws Exception {
        m_tempDir = File.createTempFile("jisp", "test");
        m_tempDir.delete();
        m_tempDir.mkdir();

        m_store = new JispFilesystemStore();

        //enable logging
        m_store.enableLogging(new NullLogger());

        //parameters
        final Parameters params = new Parameters();
        params.setParameter("directory", m_tempDir.toString());
        params.makeReadOnly();

        //parameterize it
        m_store.parameterize(params);

        //fill the store
        
        fillStore();

    }

    /**
     * Fills the store
     * @throws IOException
     */
    private void fillStore() throws IOException {
        String key = null;
        String value = null;
        m_logger.debug("filling the database...");
        for (int i = 0; i < MAX_ENTRIES; i++) {
            key = key + i;
            value = value + i;
            m_store.store(key, value);
        }
        m_logger.debug("filling the database...OK");
    }

    /**
     * Test clear() on <code>JispFilesystemStore</code>
     * @throws Exception
     */
    public void testClear() throws Exception {
        final int sizeBefore = m_store.size();
        m_logger.debug("store size before clear:" + sizeBefore);
        m_logger.debug("index count before clear:" + m_store.m_Index.count());
        m_store.clear();
        final int sizeAfter = m_store.size();
        m_logger.debug("store size after clear:" + sizeAfter);
        m_logger.debug("index count after clear:" + m_store.m_Index.count());
        assertTrue(sizeAfter == 0);
    }

    /**
     * Clean the resources after <code>testClear()</code>
     */
    protected void tearDown() throws Exception {
        m_logger.debug("deleting index and database");
        deleteAll(m_tempDir);
    }

    /**
     * Deletes files in directory recursively
     * @param f
     */
    private void deleteAll(File f) {
        if (f.isDirectory()) {
            File[] children = f.listFiles();
            for (int i = 0; i < children.length; i++) {
                deleteAll(children[i]);
            }
        }

        f.delete();
    }
}
