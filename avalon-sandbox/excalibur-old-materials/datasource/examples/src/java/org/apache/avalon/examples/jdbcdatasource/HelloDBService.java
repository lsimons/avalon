/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.examples.jdbcdatasource;

import org.apache.avalon.framework.component.Component;

/**
 * This example application creates a conmponent which makes use of a JdbcDataSource to
 *  connect to a Hypersonic SQL database.  It then adds a row to a table that it creates
 *  displaying a list of all the rows in the table.
 *
 * Note, this code ignores exceptions to keep the code simple.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/10/03 03:36:06 $
 * @since 4.1
 */
public interface HelloDBService
    extends Component
{
    /** The lookup key for the HelloDBService */
    String ROLE = "org.apache.avalon.examples.jdbcdatasource.HelloDBService";
    
    /**
     * Adds a single row to the database.
     *
     * @param title  The title for the row.
     */
    void addRow( String title );
    
    /**
     * Ask the component to delete all rows in the database.
     */
    void deleteRows();
    
    /**
     * Ask the component to log all of the rows in the database to the logger
     *  with the info log level.
     */
    void logRows();
}

