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
package org.apache.excalibur.store.impl;

import java.io.File;
import java.io.IOException;

import com.coyotegulch.jisp.BTreeIndex;
import com.coyotegulch.jisp.IndexedObjectDatabase;
import com.coyotegulch.jisp.KeyNotFound;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.store.Store;

/**
 * This store is based on the Jisp library
 * (http://www.coyotegulch.com/jisp/index.html). This store uses B-Tree indexes
 * to access variable-length serialized data stored in files.
 *
 * @author <a href="mailto:g-froehlich@gmx.de">Gerhard Froehlich</a>
 * @author <a href="mailto:vgritsenko@apache.org">Vadim Gritsenko</a>
 * @version CVS $Id: JispFilesystemStore.java,v 1.3 2003/08/12 15:55:34 vgritsenko Exp $
 */
public class JispFilesystemStore extends AbstractJispFilesystemStore
    implements Store,
               ThreadSafe,
               Parameterizable,
               Disposable {

    /**
     *  Configure the Component.<br>
     *  A few options can be used
     *  <UL>
     *    <LI> directory - The directory to store the two files belowe
     *    </LI>
     *    <LI> data-file = the name of the data file (Default: store.dat)
     *    </LI>
     *    <LI> index-file = the name of the index file (Default: store.idx)
     *    </LI>
     *    <LI> order = The page size of the B-Tree</LI>
     *  </UL>
     *
     * @param params the configuration paramters
     * @exception  ParameterException
     */
     public void parameterize(Parameters params) throws ParameterException
     {
        // get the directory to use
        try 
        {
            final String dir = params.getParameter("directory");
            this.setDirectory(new File(dir));
        } 
        catch (IOException e) 
        {
            throw new ParameterException("Unable to set directory", e);
        }

        final String databaseName = params.getParameter("data-file", "store.dat");
        final String indexName = params.getParameter("index-file", "store.idx");
        final int order = params.getParameterAsInteger("order", 301);
        if (getLogger().isDebugEnabled()) 
        {
            getLogger().debug("Database file name = " + databaseName);
            getLogger().debug("Index file name = " + indexName);
            getLogger().debug("Order=" + order);
        }

        final File databaseFile = new File(m_directoryFile, databaseName);
        final File indexFile = new File(m_directoryFile, indexName);

        if (getLogger().isDebugEnabled()) 
        {
            getLogger().debug("Initializing JispFilesystemStore");
        }

        try
        {
            final boolean isOld = databaseFile.exists();
            if (getLogger().isDebugEnabled()) 
            {
                getLogger().debug("initialize(): Datafile exists: " + isOld);
            }

            if (!isOld) {
                m_Index = new BTreeIndex(indexFile.toString(),
                                         order, super.getNullKey(), false);
            } else {
                m_Index = new BTreeIndex(indexFile.toString());
            }
            m_Database = new IndexedObjectDatabase(databaseFile.toString(), !isOld);
            m_Database.attachIndex(m_Index);
        } 
        catch (KeyNotFound ignore) 
        {
        } 
        catch (Exception e) 
        {
            getLogger().error("initialize(..) Exception", e);
        }
    }

    public void dispose()
    {
        try
        {
            getLogger().debug("Disposing");

            if (m_Index != null)
            {
                m_Index.close();
            }

            if (m_Database != null)
            {
                m_Database.close();
            }
        }
        catch (Exception e) 
        {
            getLogger().error("dispose(..) Exception", e);
        }
    }
}
