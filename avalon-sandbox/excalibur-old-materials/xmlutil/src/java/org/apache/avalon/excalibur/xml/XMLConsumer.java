/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.xml;

import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

/**
 * This interfaces identifies classes that consume XML data, receiving
 * notification of SAX events.
 * <br>
 * This interface unites the idea of SAX <code>ContentHandler</code> and
 * <code>LexicalHandler</code>.
 *
 * @deprecated Moved to org.apache.excalibur.xml.sax package.
 * @author <a href="mailto:fumagalli@exoffice.com">Pierpaolo Fumagalli</a>
 *         (Apache Software Foundation, Exoffice Technologies)
 * @version CVS $Revision: 1.3 $ $Date: 2002/10/15 23:20:18 $
 */
public interface XMLConsumer
    extends ContentHandler, LexicalHandler
{
}
