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
 * @author <a href="mailto:fumagalli@exoffice.com">Pierpaolo Fumagalli</a>
 *         (Apache Software Foundation, Exoffice Technologies)
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/22 10:06:04 $
 */
public interface XMLConsumer extends ContentHandler, LexicalHandler
{
}
