/* 
 * Copyright 2002-2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.excalibur.xml.xpath.test;

import org.apache.avalon.excalibur.testcase.ExcaliburTestCase;
import org.apache.avalon.framework.component.Component;
import org.apache.excalibur.xml.dom.DOMParser;
import org.apache.excalibur.xml.xpath.XPathProcessor;
import org.apache.excalibur.xml.xpath.PrefixResolver;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;

import java.io.StringReader;

/**
 * Test Saxon 7 XPath processor
 * @version CVS $Revision: 1.2 $ $Date: 2004/02/19 08:28:32 $
 */
public class Saxon7TestCase extends XPathTestCase
{
    public Saxon7TestCase(String name) {
        super(name);
    }
}
