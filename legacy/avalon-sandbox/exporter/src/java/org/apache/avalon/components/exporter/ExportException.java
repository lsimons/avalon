package org.apache.avalon.components.exporter;

import org.apache.avalon.framework.CascadingException;

/**
 * @version $Id: ExportException.java,v 1.1 2003/09/28 02:22:19 farra Exp $
 */

public class ExportException
    extends CascadingException
{
  public ExportException(String message) {
    super(message);
  }

  public ExportException(String message, Exception cause){
    super(message,cause);
  }

}