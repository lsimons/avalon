/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.store;

import org.apache.avalon.framework.component.Component;
import java.util.Iterator;

/**
 * Interface for the StoreJanitors
 *
 * @author <a href="mailto:g-froehlich@gmx.de">Gerhard Froehlich</a>
 * @version CVS $Id: StoreJanitor.java,v 1.1 2002/05/02 08:55:39 cziegeler Exp $
 */
public interface StoreJanitor
    extends Component
{

    String ROLE = StoreJanitor.class.getName();

    /** register method for the stores */
    void register(Store store);

    /** unregister method for the stores */
    void unregister(Store store);

    /** get an iterator to list registered stores */
    Iterator iterator();
}
