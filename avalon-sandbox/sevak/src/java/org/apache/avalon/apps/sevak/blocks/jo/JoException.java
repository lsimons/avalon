/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.apps.sevak.blocks.jo;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 *
 * Date: Jan 15, 2002
 * Time: 7:04:54 PM
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 * @version $Id: JoException.java,v 1.1 2002/09/22 09:35:01 hammant Exp $
 */
public class JoException extends Exception {
   private Exception nestedException;
   public JoException(String s) {
      super(s);
   }
   public JoException(Exception nestedException) {
      super(nestedException.toString());
      setNestedException(nestedException);
   }

   public Exception getNestedException() {
      return nestedException;
   }

   private void setNestedException(Exception nestedException) {
      this.nestedException = nestedException;
   }

   public void printStackTrace() {
      if (nestedException != null) nestedException.printStackTrace();
      super.printStackTrace();
   }

   public void printStackTrace(PrintStream s) {
      if (nestedException != null) nestedException.printStackTrace(s);
      super.printStackTrace(s);
   }

   public void printStackTrace(PrintWriter s) {
      if (nestedException != null) nestedException.printStackTrace(s);
      super.printStackTrace(s);
   }
}
