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

 4. The names "Apache Cocoon" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
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
 on  behalf of the Apache Software  Foundation and was  originally created by
 Stefano Mazzocchi  <stefano@apache.org>. For more  information on the Apache
 Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.excalibur.xml.xpath;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *  Implementation of the <code>NodeList</code> interface.<P>
 *
 * @author <a href="mailto:cziegeler@s-und-n.de">Carsten Ziegeler</a>
 * @version CVS $Id: NodeListImpl.java,v 1.1 2003/11/09 12:47:42 leosimons Exp $
*/
public class NodeListImpl implements NodeList {

    private Node[] nodelist;

    /**
     * Construct a NodeList by copying.
     */
    public NodeListImpl(NodeList list) {
      if (list == null || list.getLength() == 0) {
        nodelist = null;
      } else {
        nodelist = new Node[list.getLength()];
        for(int i = 0; i < list.getLength(); i++) {
          nodelist[i] = list.item(i).cloneNode(true);
        }
      }
    }

    /**
     * Constructor
     */
    public NodeListImpl(Node[] nodes) {
        this.nodelist = nodes;
    }

    /**
     * Constructor
     */
    public NodeListImpl() {}

    /**
     * Construct a NodeList by copying.
     */
    public NodeListImpl(DocumentFragment fragment, String rootName) {
      if (fragment != null) {
        Element root = fragment.getOwnerDocument().createElementNS(null, rootName);
        Node    current;
        while (fragment.hasChildNodes() == true) {
          current = fragment.getFirstChild();
          fragment.removeChild(current);
          root.appendChild(current);
        }
        nodelist = new Node[1];
        nodelist[0] = root;
      }
    }

    /**
     * Add a node to list
     */
    public void addNode(Node node) {
        if (this.nodelist == null) {
            this.nodelist = new Node[1];
            this.nodelist[0] = node;
        } else {
            Node[] copy = new Node[this.nodelist.length+1];
            System.arraycopy(this.nodelist, 0, copy, 0, this.nodelist.length);
            copy[copy.length-1] = node;
            this.nodelist = copy;
        }
    }

    /**
     *  Returns the <code>index</code> th item in the collection. If
     * <code>index</code> is greater than or equal to the number of nodes in
     * the list, this returns <code>null</code> .
     * @param index  Index into the collection.
     * @return  The node at the <code>index</code> th position in the
     *   <code>NodeList</code> , or <code>null</code> if that is not a valid
     *   index.
     */
    public Node item(int index) {
      if (nodelist == null || index >= nodelist.length) {
        return null;
      } else {
        return nodelist[index];
      }
    }

    /**
     *  The number of nodes in the list. The range of valid child node indices
     * is 0 to <code>length-1</code> inclusive.
     */
    public int getLength() {
      return (nodelist == null ? 0 : nodelist.length);
    }

}
